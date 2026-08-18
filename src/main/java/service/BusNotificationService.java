package service;

import java.util.List;

import model.DeviceToken;
import repository.DeviceTokenRepository;

public class BusNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final FCMNotificationService fcmNotificationService;

    public BusNotificationService() {
        this.deviceTokenRepository = new DeviceTokenRepository();
        this.fcmNotificationService = new FCMNotificationService();
    }

    public List<DeviceToken> getActiveSubscribers(String busId) {
        if (busId == null || busId.trim().isEmpty()) {
            return List.of();
        }

        return deviceTokenRepository.findByBusIdAndActive(
                busId.trim(),
                true
        );
    }

    public void notifyBusPassengers(
            String busId,
            String title,
            String message) {

        List<DeviceToken> subscribers = getActiveSubscribers(busId);

        if (subscribers == null || subscribers.isEmpty()) {
            System.out.println("[FCM] No active subscribers found for bus: " + busId);
            return;
        }

        System.out.println("[FCM] Active subscribers found: " + subscribers.size());

        for (DeviceToken subscriber : subscribers) {
            if (subscriber == null) {
                continue;
            }

            String deviceToken = subscriber.getDeviceToken();

            if (deviceToken == null || deviceToken.trim().isEmpty()) {
                System.out.println("[FCM] Skipping subscriber with empty token");
                continue;
            }

            sendSingleNotification(deviceToken, title, message, busId);
        }
    }

    public boolean sendSingleNotification(
            String deviceToken,
            String title,
            String message,
            String busId) {

        System.out.println("---------------------------------");
        System.out.println("[FCM] Preparing notification");
        System.out.println("[FCM] Bus ID       : " + busId);
        System.out.println("[FCM] Device Token : " + deviceToken);
        System.out.println("[FCM] Title        : " + title);
        System.out.println("[FCM] Message      : " + message);

        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            System.err.println("[FCM] Device token is empty");
            return false;
        }

        boolean sent =
                fcmNotificationService.sendNotification(
                        deviceToken.trim(),
                        title,
                        message
                );

        if (sent) {
            System.out.println("[FCM] Notification sent successfully");
        } else {
            System.err.println("[FCM] Notification failed");
        }

        System.out.println("---------------------------------");

        return sent;
    }

    public void unsubscribeFromBus(
            String deviceToken,
            String busId) {

        if (deviceToken == null || busId == null) {
            return;
        }

        System.out.println(
                "[FCM] Deactivating subscription"
                        + " | Token: " + deviceToken
                        + " | Bus: " + busId
        );

        deviceTokenRepository.deactivateToken(deviceToken, busId);
    }
}
