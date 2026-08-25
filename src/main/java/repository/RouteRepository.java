package repository;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.Route;
public class RouteRepository {
    private final MongoCollection<Route> collection;
    public RouteRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("Route", Route.class);
    }
    public void addRoute(Route route) {
        collection.insertOne(route);
    }
    public List<Route> getAllRoutes() {
        return collection.find().into(new ArrayList<>());
    }
    public Route getRouteById(String routeId) {
        return collection.find(eq("_id", routeId)).first();
    }
    public boolean updateRoute(Route route) {
        return collection.replaceOne(eq("_id", route.getRouteId()), route).getMatchedCount() > 0;
    }
    public boolean deleteRoute(String routeId) {
        return collection.deleteOne(eq("_id", routeId)).getDeletedCount() > 0;
    }
}