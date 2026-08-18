package repository;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.empty;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import config.MongoDBConfig;
import model.Trip;
public class TripRepository {
    private final MongoCollection<Trip> collection;
    public TripRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection(MongoDBConfig.COLLECTION_TRIPS, Trip.class);
    }
    public void saveTrip(Trip trip) {
        if (trip == null || trip.getTripId() == null || trip.getTripId().trim().isEmpty()) {
            return;
        }
        collection.replaceOne(
                eq("_id", trip.getTripId()),
                trip,
                new ReplaceOptions().upsert(true)
        );
    }
    public List<Trip> getAllTrips() {
        return collection.find().into(new ArrayList<>());
    }
    public void deleteTrip(String tripId) {
        if (tripId == null || tripId.trim().isEmpty()) {
            return;
        }
        collection.deleteOne(eq("_id", tripId));
    }
    public void clearAll() {
        collection.deleteMany(empty());
    }
    public void saveBatch(List<Trip> trips) {
        if (trips == null || trips.isEmpty()) return;
        List<ReplaceOneModel<Trip>> models = new ArrayList<>(trips.size());
        for (Trip trip : trips) {
            if (trip != null && trip.getTripId() != null && !trip.getTripId().trim().isEmpty()) {
                models.add(new ReplaceOneModel<>(
                        eq("_id", trip.getTripId()),
                        trip,
                        new ReplaceOptions().upsert(true)
                ));
            }
        }
        if (!models.isEmpty()) {
            collection.bulkWrite(models, new BulkWriteOptions().ordered(false));
        }
    }
    public void deleteBatch(List<String> tripIds) {
        if (tripIds == null || tripIds.isEmpty()) return;
        List<DeleteOneModel<Trip>> models = new ArrayList<>(tripIds.size());
        for (String tripId : tripIds) {
            if (tripId != null && !tripId.trim().isEmpty()) {
                models.add(new DeleteOneModel<>(eq("_id", tripId)));
            }
        }
        if (!models.isEmpty()) {
            collection.bulkWrite(models, new BulkWriteOptions().ordered(false));
        }
    }
}
