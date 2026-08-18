package simulator;
import listener.BusSimulationListener;
import model.*;
import repository.BusLocationRepository;
import repository.BusRepository;
import repository.RouteStopRepository;
import repository.TripRepository;
import service.BusLocationReceiver;
import service.BusLocationReceiverServiceImpl;
import service.TripReceiver;
import service.TripReceiverServiceImpl;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
public class SimulationManager {
    private final ScheduledExecutorService centralScheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<BusSimulator> activeSimulators = new CopyOnWriteArrayList<>();
    private final List<BusSimulationListener> listeners = new CopyOnWriteArrayList<>();
    private final ScheduleManager scheduleManager;
    private final ConcurrentHashMap<String, Route> routeMap;
    private final List<Bus> busInventory;
    private final BusAssignmentManager busAssignmentManager;
    private final RouteStopRepository routeStopRepository;
    private final TripRepository tripRepository;
    private final TripReceiver tripReceiver;
    private final BusRepository busRepository;
    private final BusLocationRepository busLocationRepository;
    private final BusLocationReceiver busLocationReceiver;
    private LocalTime simulatedTime;
    private long tickCounter = 0;
    private static final long DB_WRITE_INTERVAL_TICKS = 5;
    public SimulationManager(ScheduleManager scheduleManager, ConcurrentHashMap<String, Route> routeMap,
                             List<Bus> busInventory, BusAssignmentManager busAssignmentManager,
                             RouteStopRepository routeStopRepository) {
        this.scheduleManager = scheduleManager;
        this.routeMap = routeMap;
        this.busInventory = busInventory;
        this.busAssignmentManager = busAssignmentManager;
        this.routeStopRepository = routeStopRepository;
        this.tripRepository = new TripRepository();
        this.tripReceiver = new TripReceiverServiceImpl();
        this.busRepository = new BusRepository();
        this.busLocationRepository = new BusLocationRepository();
        this.busLocationReceiver = new BusLocationReceiverServiceImpl();
    }
    public void addListener(BusSimulationListener listener) {
        listeners.add(listener);
    }
    public void removeListener(BusSimulationListener listener) {
        listeners.remove(listener);
    }
    public void startSimulation() {
        restoreActiveTrips();
        centralScheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.MINUTES);
    }
    private void restoreActiveTrips() {
        try {
            List<Trip> savedTrips = tripRepository.getAllTrips();
            if (savedTrips.isEmpty()) {
                System.out.println("[RESTORE] No persisted trip state found - starting fresh.");
                return;
            }
            int restored = 0;
            for (Trip saved : savedTrips) {
                if (saved.getStatus() == null || saved.getStatus() == TripStatus.SCHEDULED
                        || saved.getStatus() == TripStatus.COMPLETED) {
                    continue;
                }
                Trip rebuilt = scheduleManager.findTripById(saved.getTripId());
                if (rebuilt == null) {
                    System.out.println("[RESTORE] Skipping persisted trip " + saved.getTripId() + " - no matching schedule trip.");
                    cleanupOrphanedTrip(saved);
                    continue;
                }
                Bus bus = findBusById(saved.getAssignedBusId());
                Route route = routeMap.get(saved.getRouteId());
                if (bus == null || route == null) {
                    System.out.println("[RESTORE] Skipping persisted trip " + saved.getTripId()
                            + " - bus or route no longer available.");
                    cleanupOrphanedTrip(saved);
                    continue;
                }
                rebuilt.setAssignedBusId(saved.getAssignedBusId());
                rebuilt.setStatus(saved.getStatus());
                rebuilt.setCoveredDistanceKm(saved.getCoveredDistanceKm());
                rebuilt.setCurrentStopIndex(saved.getCurrentStopIndex());
                rebuilt.setDwellTimeRemainingTicks(saved.getDwellTimeRemainingTicks());
                if (saved.getStatus() == TripStatus.AT_STOP) {
                    bus.setStatus(BusStatus.AT_STOP.name());
                } else {
                    bus.setStatus(BusStatus.RUNNING.name());
                }
                List<RouteStop> stops = routeStopRepository.getRouteStopsByRouteId(route.getRouteId());
                BusSimulator sim = new BusSimulator(rebuilt, bus, route, stops);
                sim.restoreState();
                activeSimulators.add(sim);
                restored++;
            }
            System.out.println("[RESTORE] Restored " + restored + " active trip(s) from persisted state.");
        } catch (Exception e) {
            System.out.println("[RESTORE] Failed to restore persisted trips: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void cleanupOrphanedTrip(Trip saved) {
        System.out.println("[RESTORE] Cleaning up orphaned trip state " + saved.getTripId());
        tripRepository.deleteTrip(saved.getTripId());
        Bus bus = findBusById(saved.getAssignedBusId());
        if (bus != null && !BusStatus.AVAILABLE.name().equalsIgnoreCase(bus.getStatus())) {
            bus.setStatus(BusStatus.AVAILABLE.name());
            bus.setAvailableFrom(saved.getOrigin());
            busRepository.updateBus(bus);
            busLocationRepository.deleteBusLocation(bus.getBusId());
            System.out.println("[RESTORE] Bus " + bus.getBusId() + " freed and returned to '" + saved.getOrigin() + "'.");
        }
    }
    private void tick() {
        try {
            simulatedTime = LocalTime.now();
            tickCounter++;
            List<Trip> newTrips = scheduleManager.checkAndAssignTrips(simulatedTime);
            for (Trip trip : newTrips) {
                Route route = routeMap.get(trip.getRouteId());
                Bus assignedBus = findBusById(trip.getAssignedBusId());
                if (assignedBus != null && route != null) {
                    List<RouteStop> stops = routeStopRepository.getRouteStopsByRouteId(route.getRouteId());
                    BusSimulator sim = new BusSimulator(trip, assignedBus, route, stops);
                    activeSimulators.add(sim);
                    System.out.println(">>> BUS STARTED: Bus " + assignedBus.getBusId() + " assigned to Trip " + trip.getTripId()
                            + " (" + trip.getOrigin() + " -> " + trip.getDestination() + ")");
                    tripReceiver.onReceiveLiveData(trip);
                    busRepository.updateBus(assignedBus);
                    notifyBusAssigned(trip, assignedBus.getBusId());
                    notifyTripStatusChanged(trip);
                }
            }
            for (BusSimulator sim : activeSimulators) {
                TripStatus oldStatus = sim.getTrip().getStatus();
                sim.updateState(1.0 / 60.0);
                BusLocation liveData = sim.generateLiveLocation();
                notifyLocationUpdated(liveData);
                busLocationReceiver.onReceiveLiveData(liveData);
                if (tickCounter % DB_WRITE_INTERVAL_TICKS == 0) {
                    tripReceiver.onReceiveLiveData(sim.getTrip());
                }
                if (oldStatus != sim.getTrip().getStatus()) {
                    System.out.println("[SIM] Trip " + sim.getTrip().getTripId() + " status changed: "
                            + oldStatus + " -> " + sim.getTrip().getStatus());
                    notifyTripStatusChanged(sim.getTrip());
                }
                if (sim.getTrip().getStatus() == TripStatus.COMPLETED) {
                    System.out.println(">>> TRIP COMPLETED: Bus " + sim.getBus().getBusId()
                            + " finished Trip " + sim.getTrip().getTripId()
                            + " | now AVAILABLE at '" + sim.getBus().getAvailableFrom() + "'");
                    tripRepository.deleteTrip(sim.getTrip().getTripId());
                    busRepository.updateBus(sim.getBus());
                    busLocationRepository.deleteBusLocation(sim.getBus().getBusId());
                    activeSimulators.remove(sim);
                    System.out.println("[AVAILABILITY] Destination pool at '" + sim.getBus().getAvailableFrom()
                            + "' increased by 1.");
                }
            }
            if (tickCounter % 15 == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("[STATUS] ").append(simulatedTime.toString().substring(0, 8))
                        .append(" | Active Buses: ").append(activeSimulators.size());
                for (BusSimulator sim : activeSimulators) {
                    sb.append(" | ").append(sim.getBus().getBusId()).append(": ")
                            .append(String.format("%.2f", sim.getTrip().getCoveredDistanceKm())).append("/")
                            .append(sim.getTrip().getTotalDistanceKm()).append(" km ")
                            .append(sim.getTrip().getStatus().name());
                }
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshAllData() {
        List<Bus> freshBuses = new BusRepository().getAllBuses();
        busInventory.clear();
        busInventory.addAll(freshBuses);
        List<Route> freshRoutes = new repository.RouteRepository().getAllRoutes();
        for (Route route : freshRoutes) {
            if (route != null && route.getRouteId() != null) {
                routeMap.put(route.getRouteId(), route);
            }
        }
        System.out.println("[SIM] All data refreshed: " + busInventory.size() + " buses, " + routeMap.size() + " routes.");
    }
    private void notifyLocationUpdated(BusLocation location) {
        for (BusSimulationListener listener : listeners) {
            listener.onBusLocationUpdated(location);
        }
    }
    private void notifyTripStatusChanged(Trip trip) {
        for (BusSimulationListener listener : listeners) {
            listener.onTripStatusChanged(trip);
        }
    }
    private void notifyBusAssigned(Trip trip, String busId) {
        for (BusSimulationListener listener : listeners) {
            listener.onBusAssigned(trip, busId);
        }
    }
    private Bus findBusById(String busId) {
        if (busId == null) return null;
        return busInventory.stream()
                .filter(b -> b.getBusId() != null && b.getBusId().equalsIgnoreCase(busId))
                .findFirst()
                .orElse(null);
    }
    public void stopSimulation() {
        centralScheduler.shutdown();
    }
    public LocalTime getSimulatedTime() {
        return simulatedTime;
    }
    public List<BusSimulator> getActiveSimulators() {
        return activeSimulators;
    }
}
