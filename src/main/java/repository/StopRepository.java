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
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("Stop", Stop.class);
    }

    public void addStop(Stop stop) {
        collection.replaceOne(
            eq("stopId", stop.getStopId()),
            stop,
            new ReplaceOptions().upsert(true)
        );
    }

    public List<Stop> getAllStops() {
        return collection.find().into(new ArrayList<>());
    }

    public Stop getStopById(String stopId) {
        return collection.find(eq("stopId", stopId)).first();
    }

    public void updateStop(Stop stop) {
        collection.replaceOne(
            eq("stopId", stop.getStopId()),
            stop
        );
    }

    public void deleteStop(String stopId) {
        collection.deleteOne(eq("stopId", stopId));
    }
}