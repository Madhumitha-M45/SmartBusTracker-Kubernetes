
package simulator;
import model.*;

import java.time.Duration;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import java.util.*;
public class ScheduleManager {

    private final List<Trip> allTrips = new ArrayList<>();

    private final BusAssignmentManager assignmentManager;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleManager(BusAssignmentManager assignmentManager) {

        this.assignmentManager = assignmentManager;

    }

    public void initializeSchedules(List<Schedule> schedules, Map<String, Route> routeMap) {

        for (Schedule schedule : schedules) {

            Route route = routeMap.get(schedule.getRouteId());

            List<String> depTimes = schedule.getDepartureTimes();

            List<String> arrTimes = schedule.getArrivalTimes();

            if (depTimes == null || depTimes.isEmpty()) {

                continue;

            }

            for (int i = 0; i < depTimes.size(); i++) {

                LocalTime dep = LocalTime.parse(depTimes.get(i), TIME_FORMATTER);

                LocalTime arr;

                if (arrTimes != null && i < arrTimes.size()) {

                    arr = LocalTime.parse(arrTimes.get(i), TIME_FORMATTER);

                } else {

                    double distance = (route != null) ? route.getDistance() : 30.0;

                    long estimatedMinutes = (long) ((distance / 40.0) * 60);

                    arr = dep.plusMinutes(estimatedMinutes > 0 ? estimatedMinutes : 60);

                }

                // Extract stop names into List<String> to match Trip model

                List<String> stopNames = new ArrayList<>();

                if (route != null && route.getRouteStops() != null) {

                    for (RouteStop rs : route.getRouteStops()) {

                        stopNames.add(rs.getStopName());

                    }

                }

                // Deterministic trip ID so saved progress can be resumed after a restart

                String tripId = schedule.getScheduleId() + "_" + depTimes.get(i).replace(":", "");

                Trip trip = new Trip(

                        tripId,

                        schedule.getScheduleId(),

                        schedule.getRouteId(),

                        schedule.getSourceName(),       // origin

                        schedule.getDestinationName(),  // destination

                        dep,

                        arr,

                        stopNames,

                        route != null ? route.getDistance() : 0.0

                );

                allTrips.add(trip);

                System.out.println("[SCHEDULE] Built Trip " + tripId + " | " + schedule.getSourceName()
                        + " -> " + schedule.getDestinationName() + " | dep " + depTimes.get(i) + " | arr " + arrTimes.get(i));

            }

        }

        System.out.println("[SCHEDULE] Total trips built: " + allTrips.size());

    }

    public List<Trip> checkAndAssignTrips(LocalTime currentTime) {

        List<Trip> newlyAssignedTrips = new ArrayList<>();

        for (Trip trip : allTrips) {

            if (trip.getStatus() == TripStatus.SCHEDULED && !currentTime.isBefore(trip.getDepartureTime())) {

                Bus assignedBus = assignmentManager.assignBusForTrip(trip);

                if (assignedBus != null) {

                    trip.setStatus(TripStatus.RUNNING);

                    newlyAssignedTrips.add(trip);

                    if (currentTime.isAfter(trip.getDepartureTime())) {

                        long delayMin = java.time.Duration.between(trip.getDepartureTime(), currentTime).toMinutes();

                        System.out.println("[SCHEDULE] Trip " + trip.getTripId() + " started late (scheduled "
                                + trip.getDepartureTime().format(TIME_FORMATTER) + ", now " + currentTime.format(TIME_FORMATTER)
                                + ") - Bus " + assignedBus.getBusId() + " departs from source at current time, delay "
                                + delayMin + " min");

                    }

                }

            }

        }

        return newlyAssignedTrips;

    }

    // Finds a rebuilt trip by its deterministic ID (used to restore saved progress)
    public Trip findTripById(String tripId) {

        for (Trip trip : allTrips) {

            if (trip.getTripId() != null && trip.getTripId().equals(tripId)) {

                return trip;

            }

        }

        return null;

    }

    public List<Trip> getAllTrips() {

        return allTrips;

    }

}
