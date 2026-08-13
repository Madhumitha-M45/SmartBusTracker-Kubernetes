package service;
 
import model.BusLocation;
import repository.BusLocationRepository;
 
public class BusLocationReceiverServiceImpl implements BusLocationReceiver {
    private final BusLocationRepository busLocationRepository;
 
    public BusLocationReceiverServiceImpl() {
        this.busLocationRepository = new BusLocationRepository();
    }
 
    public BusLocationReceiverServiceImpl(BusLocationRepository busLocationRepository) {
        this.busLocationRepository = busLocationRepository;
    }
 
    @Override
    public void onReceiveLiveData(BusLocation busLocation) {
        if (busLocation != null && busLocationRepository != null) {
            // Persist or update the live location record in MongoDB/Repository
            busLocationRepository.updateBusLocation(busLocation);
        }
    }
}
 