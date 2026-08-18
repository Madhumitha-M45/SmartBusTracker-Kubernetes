package repository;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.RouteStop;
import static com.mongodb.client.model.Filters.eq;
public class RouteStopRepository {
    private final MongoCollection<RouteStop> collection;
    public RouteStopRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection(MongoDBConfig.COLLECTION_ROUTE_STOPS, RouteStop.class);
    }
    public void addRouteStop(RouteStop routeStop) {
        if (routeStop == null) {
            throw new IllegalArgumentException("RouteStop cannot be null");
        }
        routeStop.generateId();
        collection.insertOne(routeStop);
    }
    public List<RouteStop> getAllRouteStops() {
        List<RouteStop> routeStops = new ArrayList<>();
        for (RouteStop routeStop : collection.find()) {
            routeStops.add(routeStop);
        }
        return routeStops;
    }
    public List<RouteStop> getRouteStopsByRouteId(String routeId) {
        List<RouteStop> routeStops = new ArrayList<>();
        for (RouteStop routeStop :collection.find(eq("routeId", routeId))) {
            routeStops.add(routeStop);
        }
        return routeStops;
    }
    public void updateRouteStop(RouteStop routeStop) {
        if (routeStop == null) {
            throw new IllegalArgumentException("RouteStop cannot be null");
        }
        routeStop.generateId();
        collection.replaceOne(eq("_id", routeStop.getId()),routeStop);
    }
    public void deleteRouteStopsByRouteId(String routeId) {
        collection.deleteMany(eq("routeId", routeId));
    }
    public void deleteRouteStopById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        collection.deleteOne(eq("_id", id));
    }
}