package repository;

import java.util.ArrayList;

import java.util.List;

import model.DeviceToken;
 
public class DeviceTokenRepository {

    private static final List<DeviceToken> storage = new ArrayList<>();

    public synchronized void save(DeviceToken token) {

        storage.removeIf(item -> 

            item.getDeviceToken().equalsIgnoreCase(token.getDeviceToken()) &&

            item.getBusId().equalsIgnoreCase(token.getBusId())

        );

        storage.add(token);

    }

    public synchronized List<DeviceToken> findActiveByBus(String busId) {

        List<DeviceToken> result = new ArrayList<>();

        for (DeviceToken token : storage) {

            if (token.isActive() && token.getBusId().equalsIgnoreCase(busId)) {

                result.add(token);

            }

        }

        return result;

    }

    public synchronized void deactivateToken(String deviceToken, String busId) {

        for (DeviceToken token : storage) {

            boolean matchesToken = token.getDeviceToken().equalsIgnoreCase(deviceToken);

            boolean matchesBus = token.getBusId().equalsIgnoreCase(busId);

            if (matchesToken && matchesBus) {

                token.setActive(false);

                token.setCompleted(true);

            }

        }

    }

}
 