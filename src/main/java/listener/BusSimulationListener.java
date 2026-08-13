package listener;
 
import model.BusLocation;

import model.Trip;
 
public interface BusSimulationListener {
 
    /**

     * Triggered when a bus location or progress updates on a simulation tick.

     */

    void onBusLocationUpdated(BusLocation busLocation);
 
    /**

     * Triggered when a trip changes state (SCHEDULED -> RUNNING -> AT_STOP -> COMPLETED).

     */

    void onTripStatusChanged(Trip trip);
 
    /**

     * Triggered when a bus is assigned to a trip.

     */

    void onBusAssigned(Trip trip, String busId);

}
 