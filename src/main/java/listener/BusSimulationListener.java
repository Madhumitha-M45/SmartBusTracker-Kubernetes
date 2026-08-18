package listener;
import model.BusLocation;
import model.Trip;
public interface BusSimulationListener {
    void onBusLocationUpdated(BusLocation busLocation);
    void onTripStatusChanged(Trip trip);
    void onBusAssigned(Trip trip, String busId);
}
