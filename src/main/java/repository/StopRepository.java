package repository;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import config.MongoDBConfig;
import model.Stop;

public class StopRepository {

    private final MongoCollection<Stop> collection;

    public StopRepository() {

        MongoDatabase database =
                MongoDBConfig.getDatabase();

        collection = database.getCollection(
                "Stop",
                Stop.class
        );
    }

    // =====================================================
    // ADD STOP
    // =====================================================

    public void addStop(Stop stop) {

        if (stop == null) {
            throw new IllegalArgumentException(
                    "Stop cannot be null"
            );
        }

        if (stop.getStopId() == null ||
            stop.getStopId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Stop ID cannot be empty"
            );
        }

        if (stop.getStopName() == null ||
            stop.getStopName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Stop Name cannot be empty"
            );
        }

        // Check application-level stopId
        Stop existing =
                collection.find(
                        eq("stopId", stop.getStopId())
                ).first();

        if (existing != null) {

            throw new IllegalArgumentException(
                    "Stop ID already exists: "
                    + stop.getStopId()
            );
        }

        collection.insertOne(stop);
    }

    // =====================================================
    // GET ALL STOPS
    // =====================================================

    public List<Stop> getAllStops() {

        List<Stop> stops =
                new ArrayList<>();

        collection.find().into(stops);

        return stops;
    }

    // =====================================================
    // GET STOP BY ID
    // =====================================================

    public Stop getStopById(String stopId) {

        if (stopId == null ||
            stopId.trim().isEmpty()) {

            return null;
        }

        return collection.find(
                eq("stopId", stopId)
        ).first();
    }

    // =====================================================
    // UPDATE STOP
    // =====================================================

    public void updateStop(Stop stop) {

        if (stop == null) {

            throw new IllegalArgumentException(
                    "Stop cannot be null"
            );
        }

        if (stop.getStopId() == null ||
            stop.getStopId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Stop ID cannot be empty"
            );
        }

        if (stop.getStopName() == null ||
            stop.getStopName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Stop Name cannot be empty"
            );
        }

        collection.replaceOne(
                eq("stopId", stop.getStopId()),
                stop,
                new ReplaceOptions().upsert(false)
        );
    }

    // =====================================================
    // DELETE STOP
    // =====================================================

    public void deleteStop(String stopId) {

        if (stopId == null ||
            stopId.trim().isEmpty()) {

            return;
        }

        collection.deleteOne(
                eq("stopId", stopId)
        );
    }
}