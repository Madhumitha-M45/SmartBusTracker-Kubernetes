package service;

import model.Trip;
import repository.TripRepository;

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
}
