package repository;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import config.MongoDBConfig;
import model.BusLocation;
public class BusLocationRepository {
    private final MongoCollection<BusLocation> collection;
    public BusLocationRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("BusLocation",BusLocation.class);
    }
    public void saveOrUpdateBusLocation(BusLocation location) {
        if (location == null) {
            throw new IllegalArgumentException( "BusLocation cannot be null");
        }
        if (location.getBusId() == null ||location.getBusId().trim().isEmpty()) {
            throw new IllegalArgumentException( "Bus ID is required");
        }
        collection.replaceOne(
                eq("busId", location.getBusId()),
                location,new ReplaceOptions().upsert(true));
    }
}