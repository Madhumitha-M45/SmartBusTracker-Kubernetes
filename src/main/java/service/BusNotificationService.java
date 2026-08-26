package service;

import java.util.List;

import model.DeviceToken;

import repository.DeviceTokenRepository;
 
public class BusNotificationService {

    private final DeviceTokenRepository repository = new DeviceTokenRepository();

    private final FCMNotificationService fcmService = new FCMNotificationService();

    public List<DeviceToken> getActiveSubscribers(String busId) {

        return repository.findActiveByBus(busId);

    }

    public void notifyBusPassengers(String busId, String title, String message) {

        List<DeviceToken> subscribers = getActiveSubscribers(busId);

        if (subscribers.isEmpty()) {

            System.out.println("[FCM] No active subscribers for bus: " + busId);

            return;

        }

        System.out.println("[FCM] Sending to " + subscribers.size() + " subscribers.");

        for (DeviceToken subscriber : subscribers) {

            sendSingleNotification(subscriber.getDeviceToken(), title, message, busId);

        }

    }

    public boolean sendSingleNotification(String deviceToken, String title, String message, String busId) {

        System.out.println("[FCM] Sending to Bus: " + busId + " | Token: " + deviceToken);   

        boolean sent = fcmService.sendNotification(deviceToken, title, message);

        if (sent) {

            System.out.println("[FCM] Notification sent successfully.");

        } else {

            System.out.println("[FCM] Notification failed to send.");

        }

        return sent;

    }

    public void unsubscribeFromBus(String deviceToken, String busId) {

        repository.deactivateToken(deviceToken, busId);

        System.out.println("[FCM] Subscription deactivated for Bus: " + busId);

    }

}
 