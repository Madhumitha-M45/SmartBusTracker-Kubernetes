package repository;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.Bus;
public class BusRepository {
    private final MongoCollection<Bus> collection;
    public BusRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("buses", Bus.class);
    }
    public void addBus(Bus bus) {
        collection.insertOne(bus);
    }
    public List<Bus> getAllBuses() {
        List<Bus> busList = new ArrayList<>();
        collection.find().into(busList);
        return busList;
    }
    public Bus getBusById(String busId) {
        return collection.find(eq("_id", busId)).first();
    }
    public boolean updateBus(Bus bus) {
        return collection.replaceOne(eq("_id", bus.getBusId()), bus).getModifiedCount() > 0;
    }
    public boolean deleteBus(String busId) {
        return collection.deleteOne(eq("_id", busId)).getDeletedCount() > 0;
    }
}
