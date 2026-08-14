
package simulator;
 
import model.*;
 
import java.time.LocalTime;

import java.util.List;
 
public class BusSimulator {
 
    private final Trip trip;

    private final Bus bus;

    private final Route route;
 
    private double currentSpeedKmh = 40.0;

    private double distanceCoveredKm = 0.0;

    private int currentStopIndex = 0;

    private int stopDwellTicks = 0;
 
    private static final int DWELL_DURATION_TICKS = 3;
 
    public BusSimulator(Trip trip, Bus bus, Route route) {

        this.trip = trip;

        this.bus = bus;

        this.route = route;

    }

    /**
     * Task 2: restores the simulator from a previously persisted Trip state so a
     * restarted simulator resumes from the current position on the route instead of
     * restarting from the source.
     */
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
  
        // Handle dwell time at stops

        if (trip.getStatus() == TripStatus.AT_STOP) {

            stopDwellTicks--;

            trip.setDwellTimeRemainingTicks(stopDwellTicks);

            if (stopDwellTicks <= 0) {

                trip.setStatus(TripStatus.RUNNING);

                bus.setStatus(BusStatus.RUNNING.name());

                String stopName = currentStopIndex > 0 ? route.getRouteStops().get(currentStopIndex - 1).getStopName() : "source";

                System.out.println("[SIM] Bus " + bus.getBusId() + " DEPARTED stop '" + stopName + "' - continuing to "
                        + trip.getDestination() + " on Trip " + trip.getTripId());

            } else {

                return;

            }

        }
  
        // Move bus forward

        double stepDistance = currentSpeedKmh * timeStepInHours;

        distanceCoveredKm += stepDistance;

        trip.setCoveredDistanceKm(distanceCoveredKm);
  
        // Check if full trip is completed

        if (distanceCoveredKm >= trip.getTotalDistanceKm()) {

            distanceCoveredKm = trip.getTotalDistanceKm();

            trip.setCoveredDistanceKm(distanceCoveredKm);

            trip.setStatus(TripStatus.COMPLETED);

            bus.setStatus(BusStatus.AVAILABLE.name());

            // Task 1: bus is now stationed at the destination location,
            // joining the destination's available-from bus pool.

            bus.setAvailableFrom(trip.getDestination());

            System.out.println("[SIM] Trip " + trip.getTripId() + " COMPLETED: Bus " + bus.getBusId()
                    + " reached destination '" + trip.getDestination() + "' and is now AVAILABLE there.");

            return;

        }
  
        checkIntermediateStops();

    }
  
    private void checkIntermediateStops() {

        List<RouteStop> stops = route.getRouteStops();

        if (stops == null || currentStopIndex >= stops.size()) {

            return;

        }
  
        double cumulativeTargetDistance = 0.0;

        for (int i = 0; i <= currentStopIndex; i++) {

            cumulativeTargetDistance += stops.get(i).getDistanceFromPrevious();

        }
  
        if (distanceCoveredKm >= cumulativeTargetDistance) {

            String stopName = stops.get(currentStopIndex).getStopName();

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
 
        List<RouteStop> stops = route.getRouteStops();
 
        String currentStopName = (stops != null && currentStopIndex > 0 && currentStopIndex <= stops.size())

                ? stops.get(currentStopIndex - 1).getStopName()

                : trip.getOrigin();
 
        String nextStopName = (stops != null && currentStopIndex < stops.size())

                ? stops.get(currentStopIndex).getStopName()

                : trip.getDestination();
 
        return new BusLocation(

                bus.getBusId(),

                currentSpeedKmh,

                "STOP_" + currentStopIndex,

                currentStopName,

                "STOP_" + (currentStopIndex + 1),

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
 