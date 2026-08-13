package simulator;
 
import listener.BusSimulationListener;

import model.*;
 
import java.time.LocalTime;

import java.util.*;

import java.util.concurrent.*;
 
public class SimulationManager {
 
    private final ScheduledExecutorService centralScheduler = Executors.newSingleThreadScheduledExecutor();

    private final List<BusSimulator> activeSimulators = new CopyOnWriteArrayList<>();

    private final List<BusSimulationListener> listeners = new CopyOnWriteArrayList<>();
 
    private final ScheduleManager scheduleManager;

    private final Map<String, Route> routeMap;

    private final List<Bus> busInventory;
 
    // Real-time synchronization variable

    private LocalTime simulatedTime;
 
    public SimulationManager(ScheduleManager scheduleManager, Map<String, Route> routeMap, List<Bus> busInventory) {

        this.scheduleManager = scheduleManager;

        this.routeMap = routeMap;

        this.busInventory = busInventory;

    }
 
    public void addListener(BusSimulationListener listener) {

        listeners.add(listener);

    }
 
    public void removeListener(BusSimulationListener listener) {

        listeners.remove(listener);

    }
 
    public void startSimulation() {

        // Runs every 1 second in real life

        centralScheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);

    }
 
    private void tick() {

        try {

            // 1. Fetch exact Real-World System Time (e.g., 12:25 PM)

            simulatedTime = LocalTime.now();
 
            System.out.println("[TICK] Real-World Time: " + simulatedTime.toString().substring(0, 8) 

                    + " | Active Buses: " + activeSimulators.size());
 
            // 2. Check and activate scheduled trips matching current real time

            List<Trip> newTrips = scheduleManager.checkAndAssignTrips(simulatedTime);

            for (Trip trip : newTrips) {

                Route route = routeMap.get(trip.getRouteId());

                Bus assignedBus = findBusById(trip.getAssignedBusId());
 
                if (assignedBus != null && route != null) {

                    BusSimulator sim = new BusSimulator(trip, assignedBus, route);

                    activeSimulators.add(sim);
 
                    System.out.println(">>> BUS STARTED: Bus " + assignedBus.getBusId() + " assigned to Trip " + trip.getTripId());
 
                    notifyBusAssigned(trip, assignedBus.getBusId());

                    notifyTripStatusChanged(trip);

                }

            }
 
            // 3. Update active bus movement (1 second step = 1.0 / 3600.0 hours)

            for (BusSimulator sim : activeSimulators) {

                TripStatus oldStatus = sim.getTrip().getStatus();
 
                sim.updateState(1.0 / 3600.0);
 
                BusLocation liveData = sim.generateLiveLocation();

                notifyLocationUpdated(liveData);
 
                if (oldStatus != sim.getTrip().getStatus()) {

                    notifyTripStatusChanged(sim.getTrip());

                }
 
                if (sim.getTrip().getStatus() == TripStatus.COMPLETED) {

                    System.out.println(">>> TRIP COMPLETED: Bus " + sim.getBus().getBusId());

                    activeSimulators.remove(sim);

                }

            }
 
        } catch (Exception e) {

            e.printStackTrace();

        }

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
 