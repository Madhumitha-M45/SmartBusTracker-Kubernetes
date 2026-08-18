package repository;
import java.util.ArrayList;
import java.util.List;
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
        this.collection = database.getCollection("BusLocation", BusLocation.class);
    }
    public void saveOrUpdateBusLocation(BusLocation busLocation) {
        if (busLocation == null || busLocation.getBusId() == null || busLocation.getBusId().trim().isEmpty()) {
            return;
        }
        collection.replaceOne(eq("busId", busLocation.getBusId()),busLocation,new ReplaceOptions().upsert(true));
    }
    public void addBusLocation(BusLocation busLocation) {
        saveOrUpdateBusLocation(busLocation);
    }
    public List<BusLocation> getAllBusLocations() {
        List<BusLocation> locationList = new ArrayList<>();
        collection.find().into(locationList);
        return locationList;
    }
    public BusLocation getBusLocationByBusId(String busId) {
        if (busId == null || busId.trim().isEmpty()) {
            return null;
        }
        return collection.find(eq("busId", busId)).first();
    }
    public void updateBusLocation(BusLocation busLocation) {
        saveOrUpdateBusLocation(busLocation);
    }
    public void deleteBusLocation(String busId) {
        if (busId == null || busId.trim().isEmpty()) {
            return;
        }
        collection.deleteOne(eq("busId", busId));
    }
}