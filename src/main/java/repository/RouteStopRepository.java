package repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.MongoDBConfig;
import model.RouteStop;

import static com.mongodb.client.model.Filters.eq;

public class RouteStopRepository {

    private final MongoCollection<RouteStop> collection;

    public RouteStopRepository() {

        MongoDatabase database = MongoDBConfig.getDatabase();

        collection = database.getCollection(
                "RouteStop",
                RouteStop.class
        );
    }

    // =========================================================
    // ADD ROUTE STOP
    // =========================================================

    public void addRouteStop(RouteStop routeStop) {

        if (routeStop == null) {
            throw new IllegalArgumentException(
                    "RouteStop cannot be null"
            );
        }

        routeStop.generateId();

        collection.insertOne(routeStop);
    }

    // =========================================================
    // GET ALL ROUTE STOPS
    // =========================================================

    public List<RouteStop> getAllRouteStops() {

        List<RouteStop> routeStops = new ArrayList<>();

        for (RouteStop routeStop : collection.find()) {
            routeStops.add(routeStop);
        }

        return routeStops;
    }

    // =========================================================
    // GET ROUTE STOPS BY ROUTE ID
    // =========================================================

    public List<RouteStop> getRouteStopsByRouteId(
            String routeId) {

        List<RouteStop> routeStops = new ArrayList<>();

        for (RouteStop routeStop :
                collection.find(eq("routeId", routeId))) {

            routeStops.add(routeStop);
        }

        return routeStops;
    }

    // =========================================================
    // UPDATE ROUTE STOP
    // =========================================================

    public void updateRouteStop(RouteStop routeStop) {

        if (routeStop == null) {
            throw new IllegalArgumentException(
                    "RouteStop cannot be null"
            );
        }

        routeStop.generateId();

        collection.updateOne(
                eq("_id", routeStop.getId()),

                new Document(
                        "$set",
                        new Document(
                                "routeId",
                                routeStop.getRouteId()
                        )
                        .append(
                                "stopId",
                                routeStop.getStopId()
                        )
                        .append(
                                "stopName",
                                routeStop.getStopName()
                        )
                        .append(
                                "stopOrder",
                                routeStop.getStopOrder()
                        )
                        .append(
                                "distanceFromPrevious",
                                routeStop.getDistanceFromPrevious()
                        )
                )
        );
    }

    // =========================================================
    // DELETE ALL ROUTE STOPS BY ROUTE ID
    // =========================================================

    public void deleteRouteStopsByRouteId(
            String routeId) {

        collection.deleteMany(
                eq("routeId", routeId)
        );
    }

    // =========================================================
    // DELETE ROUTE STOP BY ID
    // =========================================================

    public void deleteRouteStopById(String id) {

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        collection.deleteOne(
                eq("_id", id)
        );
    }
}