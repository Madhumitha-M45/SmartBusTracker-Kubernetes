package repository;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.MongoDBConfig;
import model.Route;

public class RouteRepository {

    private MongoCollection<Route> collection;

    public RouteRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("Route", Route.class);
    }

    // Add Route
    public void addRoute(Route route) {
        collection.insertOne(route);
    }

    // Get all Routes
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        collection.find().into(routes);
        return routes;
    }

    // Get Route by ID
    public Route getRouteById(String routeId) {
        return collection.find(eq("routeId", routeId)).first();
    }

    // Update Route
    public void updateRoute(Route route) {
        collection.replaceOne(
                eq("routeId", route.getRouteId()),
                route
        );
    }

    // Delete Route
    public void deleteRoute(String routeId) {
        collection.deleteOne(eq("routeId", routeId));
    }
}