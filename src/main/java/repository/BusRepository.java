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

    // ADD BUS
    public void addBus(Bus bus) {

        collection.insertOne(bus);
    }

    // GET ALL BUSES
    public List<Bus> getAllBuses() {

        List<Bus> busList = new ArrayList<>();

        collection.find().into(busList);

        return busList;
    }

    // GET BUS BY ID
    public Bus getBusById(String busId) {

        return collection.find(
                eq("_id", busId)
        ).first();
    }

    // UPDATE BUS
    public boolean updateBus(Bus bus) {

        return collection.replaceOne(
                eq("_id", bus.getBusId()),
                bus
        ).getModifiedCount() > 0;
    }

    // DELETE BUS
    public boolean deleteBus(String busId) {

        return collection.deleteOne(
                eq("_id", busId)
        ).getDeletedCount() > 0;
    }
}