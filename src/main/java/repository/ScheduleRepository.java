package repository;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.Schedule;
public class ScheduleRepository {
    private final MongoCollection<Schedule> collection;
    public ScheduleRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection("Schedule", Schedule.class);
    }
    public void addSchedule(Schedule schedule) {
        collection.insertOne(schedule);
    }
    public List<Schedule> getAllSchedules() {
        return collection.find().into(new ArrayList<>());
    }
    public Schedule getScheduleById(String scheduleId) {
        return collection.find(eq("_id", scheduleId)).first();
    }
    public List<Schedule> getSchedulesByRouteId(String routeId) {
        return collection.find(eq("routeId", routeId)).into(new ArrayList<>());
    }
    public boolean updateSchedule(Schedule schedule) {
        return collection .replaceOne(eq("_id", schedule.getScheduleId()),schedule).getMatchedCount() > 0;
    }
    public boolean deleteSchedule(String scheduleId) {
        return collection.deleteOne(eq("_id", scheduleId)) .getDeletedCount() > 0;
    }
}