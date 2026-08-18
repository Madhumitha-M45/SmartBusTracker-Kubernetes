package repository;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.Schedule;
public class ScheduleRepository {
    private final MongoCollection<Schedule> collection;
    public ScheduleRepository() {
        MongoDatabase database = MongoDBConfig.getDatabase();
        collection = database.getCollection(MongoDBConfig.COLLECTION_SCHEDULES, Schedule.class);
    }
    public void addSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        if (schedule.getScheduleId() == null ||
            schedule.getScheduleId().trim().isEmpty()) {
            throw new IllegalArgumentException("Schedule ID is required");
        }
        collection.insertOne(schedule);
    }
    public List<Schedule> getAllSchedules() {
        return collection.find().into(new ArrayList<>());
    }
    public Schedule getScheduleById(String scheduleId) {
        if (scheduleId == null ||scheduleId.trim().isEmpty()) {
            return null;
        }
        return collection.find(eq("_id", scheduleId)).first();
    }
    public List<Schedule> getSchedulesByRouteId(String routeId) {
        if (routeId == null ||routeId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return collection.find(eq("routeId", routeId)).into(new ArrayList<>());
    }
    public boolean updateSchedule(Schedule schedule) {
        if (schedule == null || schedule.getScheduleId() == null ||schedule.getScheduleId().trim().isEmpty()) {
            return false;
        }
        return collection.replaceOne(eq("_id", schedule.getScheduleId()),schedule).getMatchedCount() > 0;
    }
    public boolean deleteSchedule(String scheduleId) {
        if (scheduleId == null ||scheduleId.trim().isEmpty()) {
            return false;
        }
        return collection.deleteOne(eq("_id", scheduleId)).getDeletedCount() > 0;
    }
}