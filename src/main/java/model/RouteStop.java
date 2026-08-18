package model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
public class RouteStop {
    @BsonId
    private String id;
    @BsonProperty("routeId")
    private String routeId;
    @BsonProperty("stopId")
    private String stopId;
    @BsonProperty("stopName")
    private String stopName;
    @BsonProperty("stopOrder")
    private int stopOrder;
    @BsonProperty("distanceFromPrevious")
    private double distanceFromPrevious;
    public RouteStop() {
    }
    public RouteStop(String routeId,String stopId,String stopName,int stopOrder,double distanceFromPrevious) {
        this.routeId = routeId;
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopOrder = stopOrder;
        this.distanceFromPrevious = distanceFromPrevious;
        generateId();
    }
    public void generateId() {
        if (routeId != null && stopId != null) {
            this.id = routeId + "_" + stopId;
        }
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRouteId() {
        return routeId;
    }
    public void setRouteId(String routeId) {
        this.routeId = routeId;
        generateId();
    }
    public String getStopId() {
        return stopId;
    }
    public void setStopId(String stopId) {
        this.stopId = stopId;
        generateId();
    }
    public String getStopName() {
        return stopName;
    }
    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
    public int getStopOrder() {
        return stopOrder;
    }
    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }
    public double getDistanceFromPrevious() {
        return distanceFromPrevious;
    }
    public void setDistanceFromPrevious(
            double distanceFromPrevious) {
        this.distanceFromPrevious = distanceFromPrevious;
    }
    @Override
    public String toString() {
        return "RouteStop{" + "id='" + id + '\'' +  ", routeId='" + routeId + '\'' +", stopId='" + stopId + '\'' +", stopName='" + stopName + '\'' +", stopOrder=" + stopOrder +", distanceFromPrevious=" +distanceFromPrevious +'}';
    }
}