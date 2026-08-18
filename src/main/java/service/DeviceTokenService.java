package service;

import java.util.List;

import model.DeviceToken;
import repository.DeviceTokenRepository;

public class DeviceTokenService {

    private final DeviceTokenRepository repository =
            new DeviceTokenRepository();

    public void register(DeviceToken deviceToken) {
        repository.save(deviceToken);
    }

    public List<DeviceToken> findActiveByBusId(String busId) {
        return repository.findActiveByBus(busId);
    }

    public void deactivate(String deviceToken) {
        repository.deactivate(deviceToken);
    }

    public void complete(String deviceToken) {
        repository.markCompleted(deviceToken);
    }
}
