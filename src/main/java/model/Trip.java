package model;
 
import java.time.LocalTime;
import java.util.List;
 
public class Trip {
    private String tripId;
    private String scheduleId;
    private String routeId;
    private String origin;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Direction direction;
    private List<String> stops;
    
    private String assignedBusId;
    private TripStatus status;
    private double totalDistanceKm;
    private double coveredDistanceKm;
    private int currentStopIndex;
    private int dwellTimeRemainingTicks;
 
    public Trip(String tripId, String scheduleId, String routeId, String origin, String destination,
                LocalTime departureTime, LocalTime arrivalTime, Direction direction,
                List<String> stops, double totalDistanceKm) {
        this.tripId = tripId;
        this.scheduleId = scheduleId;
        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
        this.stops = stops;
        this.totalDistanceKm = totalDistanceKm;
        this.status = TripStatus.SCHEDULED;
        this.coveredDistanceKm = 0.0;
        this.currentStopIndex = 0;
        this.dwellTimeRemainingTicks = 0;
    }
 
    // Getters and Setters
    public String getTripId() { return tripId; }
    public String getScheduleId() { return scheduleId; }
    public String getRouteId() { return routeId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public Direction getDirection() { return direction; }
    public List<String> getStops() { return stops; }
    public String getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(String assignedBusId) { this.assignedBusId = assignedBusId; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
    public double getTotalDistanceKm() { return totalDistanceKm; }
    public double getCoveredDistanceKm() { return coveredDistanceKm; }
    public void setCoveredDistanceKm(double coveredDistanceKm) { this.coveredDistanceKm = coveredDistanceKm; }
    public int getCurrentStopIndex() { return currentStopIndex; }
    public void setCurrentStopIndex(int currentStopIndex) { this.currentStopIndex = currentStopIndex; }
    public int getDwellTimeRemainingTicks() { return dwellTimeRemainingTicks; }
    public void setDwellTimeRemainingTicks(int dwellTimeRemainingTicks) { this.dwellTimeRemainingTicks = dwellTimeRemainingTicks; }
}
 