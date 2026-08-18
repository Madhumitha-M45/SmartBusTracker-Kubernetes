package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.DeviceToken;

public class DeviceTokenRepository {

    private static final List<DeviceToken> storage = new ArrayList<>();

    public synchronized void save(DeviceToken token) {
        if (token == null || token.getDeviceToken() == null) return;

        storage.removeIf(t -> t != null
                && token.getDeviceToken().equalsIgnoreCase(t.getDeviceToken())
                && token.getBusId().equalsIgnoreCase(t.getBusId()));

        storage.add(token);
    }

    public synchronized List<DeviceToken> findByBusIdAndActive(String busId, boolean active) {
        if (busId == null) return new ArrayList<>();

        return storage.stream()
                .filter(t -> t != null && t.getBusId() != null && t.getBusId().equalsIgnoreCase(busId.trim()))
                .filter(t -> t.isActive() == active)
                .collect(Collectors.toList());
    }

    public synchronized List<DeviceToken> findActiveByBus(String busId) {
        return findByBusIdAndActive(busId, true);
    }

    public synchronized void deactivate(String deviceToken) {
        if (deviceToken == null) return;

        storage.stream()
                .filter(t -> t != null && t.getDeviceToken() != null && t.getDeviceToken().equalsIgnoreCase(deviceToken.trim()))
                .forEach(t -> t.setActive(false));
    }

    public synchronized void markCompleted(String deviceToken) {
        if (deviceToken == null) return;

        storage.stream()
                .filter(t -> t != null && t.getDeviceToken() != null && t.getDeviceToken().equalsIgnoreCase(deviceToken.trim()))
                .forEach(t -> {
                    t.setActive(false);
                    t.setCompleted(true);
                });
    }

    public synchronized void deactivateToken(String deviceToken, String busId) {
        if (deviceToken == null || busId == null) return;

        storage.stream()
                .filter(t -> t != null
                        && t.getDeviceToken() != null && t.getDeviceToken().equalsIgnoreCase(deviceToken.trim())
                        && t.getBusId() != null && t.getBusId().equalsIgnoreCase(busId.trim()))
                .forEach(t -> {
                    t.setActive(false);
                    t.setCompleted(true);
                });
    }

    public synchronized List<DeviceToken> getAllTokens() {
        return new ArrayList<>(storage);
    }
}
