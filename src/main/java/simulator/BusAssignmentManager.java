package simulator;
import model.Bus;
import model.BusStatus;
import model.Trip;
import model.TripStatus;
import java.util.List;
import java.util.Map;
public class BusAssignmentManager {
    private final List<Bus> availableBuses;
    public BusAssignmentManager(List<Bus> availableBuses) {
        this.availableBuses = availableBuses;
    }
    public Bus assignBusForTrip(Trip trip) {
        if (trip == null || trip.getStatus() != TripStatus.SCHEDULED) {
            return null;
        }
        String tripOrigin = trip.getOrigin();
        for (Bus bus : availableBuses) {
            if (bus.getStatus() != null && bus.getStatus().equalsIgnoreCase(BusStatus.AVAILABLE.name())) {
                if (isSameLocation(bus.getAvailableFrom(), tripOrigin)) {
                    trip.setAssignedBusId(bus.getBusId());
                    bus.setStatus(BusStatus.RUNNING.name());
                    System.out.println("[ASSIGN] Bus " + bus.getBusId() + " assigned to Trip " + trip.getTripId()
                            + " (" + tripOrigin + " -> " + trip.getDestination() + ")"
                            + " | available-from list at '" + bus.getAvailableFrom() + "' reduced by 1.");
                    printAvailability();
                    return bus;
                }
            }
        }
        return null;
    }
    public void printAvailability() {
        Map<String, Integer> availabilityByLocation = new java.util.TreeMap<>();
        for (Bus bus : availableBuses) {
            if (bus.getStatus() != null && bus.getStatus().equalsIgnoreCase(BusStatus.AVAILABLE.name())) {
                String location = (bus.getAvailableFrom() == null || bus.getAvailableFrom().trim().isEmpty())
                        ? "UNKNOWN" : bus.getAvailableFrom().trim();
                availabilityByLocation.put(location, availabilityByLocation.getOrDefault(location, 0) + 1);
            }
        }
        System.out.println("[AVAILABILITY] Available buses by location: " + availabilityByLocation);
    }
    private boolean isSameLocation(String locationA, String locationB) {
        if (locationA == null || locationB == null) {
            return false;
        }
        String a = locationA.trim().toLowerCase();
        String b = locationB.trim().toLowerCase();
        return !a.isEmpty() && (a.equals(b) || a.contains(b) || b.contains(a));
    }
}
