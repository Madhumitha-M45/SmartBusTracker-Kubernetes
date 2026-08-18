package service;
import model.Trip;
import repository.TripRepository;
import java.util.List;
public class TripReceiverServiceImpl implements TripReceiver {
    private final TripRepository tripRepository;
    public TripReceiverServiceImpl() {
        this.tripRepository = new TripRepository();
    }
    public TripReceiverServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
    @Override
    public void onReceiveLiveData(Trip trip) {
        if (trip != null && tripRepository != null) {
            tripRepository.saveTrip(trip);
        }
    }
    @Override
    public void onReceiveLiveDataBatch(List<Trip> trips) {
        if (trips != null && !trips.isEmpty() && tripRepository != null) {
            tripRepository.saveBatch(trips);
        }
    }
}
