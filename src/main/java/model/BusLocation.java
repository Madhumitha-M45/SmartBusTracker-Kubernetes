package model;
 
import org.bson.codecs.pojo.annotations.BsonProperty;
 
public class BusLocation {
 
    @BsonProperty("busId")
    private String busId;
 
    @BsonProperty("speed")
    private double speed;
 
    @BsonProperty("currentStopId")
    private String currentStopId;
 
    @BsonProperty("currentStopName")
    private String currentStopName;
 
    @BsonProperty("nextStopId")
    private String nextStopId;
 
    @BsonProperty("nextStopName")
    private String nextStopName;
 
    @BsonProperty("lastUpdated")
    private String lastUpdated;
 
    @BsonProperty("scheduleId")
    private String scheduleId;
 
    @BsonProperty("routeId")
    private String routeId;
 
    @BsonProperty("status")
    private String status;
 
    @BsonProperty("distanceCovered")
    private double distanceCovered;
 
    @BsonProperty("distanceRemaining")
    private double distanceRemaining;
 
    @BsonProperty("progress")
    private double progress;
 
    @BsonProperty("delayMinutes")
    private double delayMinutes;
 
    @BsonProperty("eta")
    private String eta;
 
    // 1. Mandatory Default Constructor for MongoDB POJO Codec
    public BusLocation() {
    }
 
    // 2. Partial Constructor
    public BusLocation(String busId, double speed, String currentStopId, String currentStopName,
                       String nextStopId, String nextStopName, String lastUpdated) {
        this.busId = busId;
        this.speed = speed;
        this.currentStopId = currentStopId;
        this.currentStopName = currentStopName;
        this.nextStopId = nextStopId;
        this.nextStopName = nextStopName;
        this.lastUpdated = lastUpdated;
    }
 
    // 3. Full All-Arguments Constructor
    public BusLocation(String busId, double speed, String currentStopId, String currentStopName,
                       String nextStopId, String nextStopName, String lastUpdated,
                       String scheduleId, String routeId, String status,
                       double distanceCovered, double distanceRemaining,
                       double progress, double delayMinutes, String eta) {
        this.busId = busId;
        this.speed = speed;
        this.currentStopId = currentStopId;
        this.currentStopName = currentStopName;
        this.nextStopId = nextStopId;
        this.nextStopName = nextStopName;
        this.lastUpdated = lastUpdated;
        this.scheduleId = scheduleId;
        this.routeId = routeId;
        this.status = status;
        this.distanceCovered = distanceCovered;
        this.distanceRemaining = distanceRemaining;
        this.progress = progress;
        this.delayMinutes = delayMinutes;
        this.eta = eta;
    }
 
    public String getBusId() {
        return busId;
    }
 
    public void setBusId(String busId) {
        this.busId = busId;
    }
 
    public double getSpeed() {
        return speed;
    }
 
    public void setSpeed(double speed) {
        this.speed = speed;
    }
 
    public String getCurrentStopId() {
        return currentStopId;
    }
 
    public void setCurrentStopId(String currentStopId) {
        this.currentStopId = currentStopId;
    }
 
    public String getCurrentStopName() {
        return currentStopName;
    }
 
    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }
 
    public String getNextStopId() {
        return nextStopId;
    }
 
    public void setNextStopId(String nextStopId) {
        this.nextStopId = nextStopId;
    }
 
    public String getNextStopName() {
        return nextStopName;
    }
 
    public void setNextStopName(String nextStopName) {
        this.nextStopName = nextStopName;
    }
 
    public String getLastUpdated() {
        return lastUpdated;
    }
 
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
 
    public String getScheduleId() {
        return scheduleId;
    }
 
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
 
    public String getRouteId() {
        return routeId;
    }
 
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public double getDistanceCovered() {
        return distanceCovered;
    }
 
    public void setDistanceCovered(double distanceCovered) {
        this.distanceCovered = distanceCovered;
    }
 
    public double getDistanceRemaining() {
        return distanceRemaining;
    }
 
    public void setDistanceRemaining(double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }
 
    public double getProgress() {
        return progress;
    }
 
    public void setProgress(double progress) {
        this.progress = progress;
    }
 
    public double getDelayMinutes() {
        return delayMinutes;
    }
 
    public void setDelayMinutes(double delayMinutes) {
        this.delayMinutes = delayMinutes;
    }
 
    public String getEta() {
        return eta;
    }
 
    public void setEta(String eta) {
        this.eta = eta;
    }
 
    @Override
    public String toString() {
        return "BusLocation{" +
                "busId='" + busId + '\'' +
                ", scheduleId='" + scheduleId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", status='" + status + '\'' +
                ", currentStopId='" + currentStopId + '\'' +
                ", currentStopName='" + currentStopName + '\'' +
                ", nextStopId='" + nextStopId + '\'' +
                ", nextStopName='" + nextStopName + '\'' +
                ", speed=" + speed +
                ", distanceCovered=" + distanceCovered +
                ", distanceRemaining=" + distanceRemaining +
                ", progress=" + progress +
                ", delayMinutes=" + delayMinutes +
                ", eta='" + eta + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
 