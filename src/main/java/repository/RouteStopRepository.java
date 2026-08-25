package repository;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.RouteStop;
public class RouteStopRepository {
    private final MongoCollection<RouteStop> collection;
    public RouteStopRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("RouteStop",RouteStop.class);
    }
    public void addRouteStop(RouteStop routeStop) {
        collection.insertOne(routeStop);
    }
    public List<RouteStop> getAllRouteStops() {
        return collection.find().into(new ArrayList<>());
    }
    public List<RouteStop> getRouteStopsByRouteId(String routeId) {
        return collection.find(eq("routeId", routeId)).into(new ArrayList<>());
    }
    public RouteStop getRouteStopById(String stopId) {
        return collection.find(eq("_id", stopId)).first();
    }
    public boolean updateRouteStop(RouteStop routeStop) {
        return collection.replaceOne(eq("_id", routeStop.getStopId()),routeStop).getMatchedCount() > 0;
    }
    public boolean deleteRouteStop(String stopId) {
        return collection.deleteOne(eq("_id", stopId)) .getDeletedCount() > 0;
    }
    public boolean deleteRouteStopsByRouteId(String routeId) {
        return collection.deleteMany(eq("routeId", routeId)).getDeletedCount() > 0;
    }
}