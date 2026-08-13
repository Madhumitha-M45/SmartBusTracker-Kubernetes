package simulator;
 
import model.Bus;

import model.BusStatus;

import model.Trip;

import model.TripStatus;
 
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeParseException;

import java.util.List;
 
public class BusAssignmentManager {
 
    private final List<Bus> availableBuses;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
 
    public BusAssignmentManager(List<Bus> availableBuses) {

        this.availableBuses = availableBuses;

    }
 
    public Bus assignBusForTrip(Trip trip) {

        if (trip == null || trip.getStatus() != TripStatus.SCHEDULED) {

            return null;

        }
 
        LocalTime tripDepTime = trip.getDepartureTime();
 
        for (Bus bus : availableBuses) {

            // Check status against BusStatus enum name (e.g. "AVAILABLE")

            if (bus.getStatus() != null && bus.getStatus().equalsIgnoreCase(BusStatus.AVAILABLE.name())) {
 
                // Check time availability against bus.getAvailableFrom()

                LocalTime busAvailableTime = parseTimeSafely(bus.getAvailableFrom());

                boolean isTimeReady = (busAvailableTime == null || tripDepTime == null) || !busAvailableTime.isAfter(tripDepTime);
 
                if (isTimeReady) {

                    // Assign bus ID to trip

                    trip.setAssignedBusId(bus.getBusId());

                    // Update bus status to RUNNING

                    bus.setStatus(BusStatus.RUNNING.name());

                    return bus;

                }

            }

        }

        return null;

    }
 
    /**

     * Safely parses time strings into LocalTime objects.

     */

    private LocalTime parseTimeSafely(String timeStr) {

        if (timeStr == null || timeStr.trim().isEmpty()) {

            return null;

        }

        String cleanStr = timeStr.trim();
 
        // Prevents location/text strings from being parsed

        if (!cleanStr.contains(":") && !cleanStr.matches(".*\\d.*")) {

            return null;

        }
 
        try {

            return LocalTime.parse(cleanStr, TIME_FORMATTER);

        } catch (DateTimeParseException e) {

            try {

                return LocalTime.parse(cleanStr);

            } catch (DateTimeParseException ex) {

                return null;

            }

        }

    }

}
 