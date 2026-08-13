package model;
 
import java.util.List;

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
 
    @BsonProperty("routeStops")

    private List<RouteStop> routeStops;
 
    // Default Constructor (Mandatory for MongoDB POJO Codec)

    public Route() {

    }
 
    public Route(String routeId, String routeName, String source, String destination, double distance, List<RouteStop> routeStops) {

        this.routeId = routeId;

        this.routeName = routeName;

        this.source = source;

        this.destination = destination;

        this.distance = distance;

        this.routeStops = routeStops;

    }
 
    public String getRouteId() { return routeId; }

    public void setRouteId(String routeId) { this.routeId = routeId; }
 
    public String getRouteName() { return routeName; }

    public void setRouteName(String routeName) { this.routeName = routeName; }
 
    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }
 
    public String getDestination() { return destination; }

    public void setDestination(String destination) { this.destination = destination; }
 
    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }
 
    public List<RouteStop> getRouteStops() { return routeStops; }

    public void setRouteStops(List<RouteStop> routeStops) { this.routeStops = routeStops; }

}
 