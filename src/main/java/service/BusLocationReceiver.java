package service;
import model.BusLocation;
import java.util.List;
public interface BusLocationReceiver {
    void onReceiveLiveData(BusLocation busLocation);
    void onReceiveLiveDataBatch(List<BusLocation> busLocations);
}
