package service;
import model.BusLocation;
import repository.BusLocationRepository;
import java.util.List;
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
            busLocationRepository.updateBusLocation(busLocation);
        }
    }
    @Override
    public void onReceiveLiveDataBatch(List<BusLocation> busLocations) {
        if (busLocations != null && !busLocations.isEmpty() && busLocationRepository != null) {
            for (BusLocation bl : busLocations) {
                busLocationRepository.saveOrUpdateBusLocation(bl);
            }
        }
    }
}
