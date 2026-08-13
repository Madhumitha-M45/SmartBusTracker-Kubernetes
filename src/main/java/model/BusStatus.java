package model;
 
public enum BusStatus {
    AVAILABLE,      // Bus is free and parked at a location (e.g., TVL)
    RUNNING,        // Currently actively driving on a trip
    AT_STOP,        // Currently dwelling at a bus stop
    OUT_OF_SERVICE  // Under maintenance or unavailable
}
 