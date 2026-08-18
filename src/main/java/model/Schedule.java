package model;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
public class Schedule {
    @BsonId
    private String scheduleId;
    @BsonProperty("routeId")
    private String routeId;
    @BsonProperty("sourceName")
    private String sourceName;
    @BsonProperty("destinationName")
    private String destinationName;
    @BsonProperty("departureTimes")
    private List<String> departureTimes;
    @BsonProperty("arrivalTimes")
    private List<String> arrivalTimes;
    @BsonProperty("operatingDays")
    private List<String> operatingDays;
    public Schedule() {
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
    public String getSourceName() {
        return sourceName;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getDestinationName() {
        return destinationName;
    }
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    public List<String> getDepartureTimes() {
        return departureTimes;
    }
    public void setDepartureTimes(List<String> departureTimes) {
        this.departureTimes = departureTimes;
    }
    public List<String> getArrivalTimes() {
        return arrivalTimes;
    }
    public void setArrivalTimes(List<String> arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }
    public List<String> getOperatingDays() {
        return operatingDays;
    }
    public void setOperatingDays(List<String> operatingDays) {
        this.operatingDays = operatingDays;
    }
}