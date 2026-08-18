package simulator;
import model.*;
import java.time.LocalTime;
import java.util.List;
public class BusSimulator {
    private static final double SPEED_KMH = 40.0;
    private static final int DWELL_DURATION_TICKS = 1;
    private static final String STOP_ID_PREFIX = "STOP_";
    private final Trip trip;
    private final Bus bus;
    private final Route route;
    private final List<RouteStop> routeStops;
    private double distanceCoveredKm = 0.0;
    private int currentStopIndex = 0;
    private int stopDwellTicks = 0;
    public BusSimulator(Trip trip, Bus bus, Route route, List<RouteStop> routeStops) {
        this.trip = trip;
        this.bus = bus;
        this.route = route;
        this.routeStops = routeStops;
    }
    public void restoreState() {
        this.distanceCoveredKm = trip.getCoveredDistanceKm();
        this.currentStopIndex = trip.getCurrentStopIndex();
        this.stopDwellTicks = trip.getDwellTimeRemainingTicks();
        System.out.println("[SIM][RESTORE] Bus " + bus.getBusId() + " resumed Trip " + trip.getTripId()
                + " at " + String.format("%.2f", distanceCoveredKm) + " km (stop index " + currentStopIndex
                + ", dwell ticks " + stopDwellTicks + ") on route " + route.getRouteId());
    }
    public void updateState(double timeStepInHours) {
        if (trip.getStatus() == TripStatus.COMPLETED) {
            return;
        }
        if (trip.getStatus() == TripStatus.AT_STOP) {
            stopDwellTicks--;
            trip.setDwellTimeRemainingTicks(stopDwellTicks);
            if (stopDwellTicks <= 0) {
                trip.setStatus(TripStatus.RUNNING);
                bus.setStatus(BusStatus.RUNNING.name());
                String stopName = currentStopIndex > 0 ? routeStops.get(currentStopIndex - 1).getStopName() : "source";
                System.out.println("[SIM] Bus " + bus.getBusId() + " DEPARTED stop '" + stopName + "' - continuing to "
                        + trip.getDestination() + " on Trip " + trip.getTripId());
            } else {
                return;
            }
        }
        double stepDistance = SPEED_KMH * timeStepInHours;
        distanceCoveredKm += stepDistance;
        trip.setCoveredDistanceKm(distanceCoveredKm);
        if (distanceCoveredKm >= trip.getTotalDistanceKm()) {
            distanceCoveredKm = trip.getTotalDistanceKm();
            trip.setCoveredDistanceKm(distanceCoveredKm);
            trip.setStatus(TripStatus.COMPLETED);
            bus.setStatus(BusStatus.AVAILABLE.name());
            bus.setAvailableFrom(trip.getDestination());
            System.out.println("[SIM] Trip " + trip.getTripId() + " COMPLETED: Bus " + bus.getBusId()
                    + " reached destination '" + trip.getDestination() + "' and is now AVAILABLE there.");
            return;
        }
        checkIntermediateStops();
    }
    private void checkIntermediateStops() {
        if (routeStops == null || currentStopIndex >= routeStops.size()) {
            return;
        }
        double cumulativeTargetDistance = 0.0;
        for (int i = 0; i <= currentStopIndex; i++) {
            cumulativeTargetDistance += routeStops.get(i).getDistanceFromPrevious();
        }
        if (distanceCoveredKm >= cumulativeTargetDistance) {
            String stopName = routeStops.get(currentStopIndex).getStopName();
            trip.setStatus(TripStatus.AT_STOP);
            bus.setStatus(BusStatus.AT_STOP.name());
            stopDwellTicks = DWELL_DURATION_TICKS;
            trip.setDwellTimeRemainingTicks(stopDwellTicks);
            currentStopIndex++;
            trip.setCurrentStopIndex(currentStopIndex);
            System.out.println("[SIM] Bus " + bus.getBusId() + " REACHED stop '" + stopName + "' (dwelling for "
                    + DWELL_DURATION_TICKS + "s) on Trip " + trip.getTripId());
        }
    }
    public BusLocation generateLiveLocation() {
        double remaining = Math.max(0.0, trip.getTotalDistanceKm() - distanceCoveredKm);
        double progress = (trip.getTotalDistanceKm() > 0) ? (distanceCoveredKm / trip.getTotalDistanceKm()) * 100.0 : 0.0;
        String currentStopName = (routeStops != null && currentStopIndex > 0 && currentStopIndex <= routeStops.size())
                ? routeStops.get(currentStopIndex - 1).getStopName()
                : trip.getOrigin();
        String nextStopName = (routeStops != null && currentStopIndex < routeStops.size())
                ? routeStops.get(currentStopIndex).getStopName()
                : trip.getDestination();
        return new BusLocation(
                bus.getBusId(),
                SPEED_KMH,
                STOP_ID_PREFIX + currentStopIndex,
                currentStopName,
                STOP_ID_PREFIX + (currentStopIndex + 1),
                nextStopName,
                LocalTime.now().toString(),
                trip.getScheduleId(),
                trip.getRouteId(),
                trip.getStatus().name(),
                distanceCoveredKm,
                remaining,
                progress,
                0.0,
                trip.getArrivalTime().toString()
        );
    }
    public Trip getTrip() { return trip; }
    public Bus getBus() { return bus; }
    public Route getRoute() { return route; }
}
