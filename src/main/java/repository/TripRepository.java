
package repository;
 
import static com.mongodb.client.model.Filters.eq;
 
import java.util.ArrayList;

import java.util.List;
 
import org.bson.Document;
 
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.ReplaceOptions;
 
import config.MongoDBConfig;

import model.Trip;
 
public class TripRepository {
 
    private final MongoCollection<Trip> collection;
 
    public TripRepository() {

        MongoDatabase database = MongoDBConfig.getDatabase();

        collection = database.getCollection("TripState", Trip.class);

    }
 
    // SAVE OR UPDATE ACTIVE TRIP STATE

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
 
    // GET ALL PERSISTED TRIP STATES

    public List<Trip> getAllTrips() {

        return collection.find().into(new ArrayList<>());

    }
 
    // DELETE TRIP STATE

    public void deleteTrip(String tripId) {

        if (tripId == null || tripId.trim().isEmpty()) {

            return;

        }

        collection.deleteOne(eq("_id", tripId));

    }
 
    // CLEAR ALL TRIP STATES

    public void clearAll() {

        collection.deleteMany(new Document());

    }

	public void updateTripProgressFromLocation(String scheduleId, String busId, double distanceCovered, int stopIndex,
			String status) {
		// TODO Auto-generated method stub
		
	}

}
 