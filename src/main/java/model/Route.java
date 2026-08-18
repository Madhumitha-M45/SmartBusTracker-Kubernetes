package model;
import org.bson.codecs.pojo.annotations.BsonProperty;
public class Route {
    @BsonProperty("routeId")
    private String routeId;
    @BsonProperty("routeName")
    private String routeName;
    @BsonProperty("source")
    private String source;
    @BsonProperty("destination")
    private String destination;
    @BsonProperty("distance")
    private double distance;
    public Route() {
    }
    public Route(String routeId, String routeName, String source, String destination, double distance) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }
    public String getRouteId() { 
    	return routeId; }
    public void setRouteId(String routeId) { 
    	this.routeId = routeId; }
    public String getRouteName() { 
    	return routeName; }
    public void setRouteName(String routeName) { 
    	this.routeName = routeName; }
    public String getSource() { 
    	return source; }
    public void setSource(String source) { 
    	this.source = source; }
    public String getDestination() { 
    	return destination; }
    public void setDestination(String destination) { 
    	this.destination = destination; }
    public double getDistance() { 
    	return distance; }
    public void setDistance(double distance) { 
    	this.distance = distance; }
}
