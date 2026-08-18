package service;
import model.Trip;
import java.util.List;
public interface TripReceiver {
    void onReceiveLiveData(Trip trip);
    void onReceiveLiveDataBatch(List<Trip> trips);
}
