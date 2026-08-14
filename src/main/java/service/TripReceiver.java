package service;

import model.Trip;

public interface TripReceiver {
    void onReceiveLiveData(Trip trip);
}
