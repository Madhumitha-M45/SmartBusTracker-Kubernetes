package service;

import java.util.List;

import dto.ETAResponse;
import model.DeviceToken;

public class NotificationScheduler {

    private final ETAService etaService =
            new ETAService();

    private final BusNotificationService busNotificationService =
            new BusNotificationService();

    private final ETANotificationService etaNotificationService =
            new ETANotificationService();

    public void processActiveBusNotifications() {
        System.out.println("[NOTIFICATION-SCHEDULER] Checking active buses...");

        List<String> activeBusIds = etaService.getActiveBusIds();

        if (activeBusIds == null || activeBusIds.isEmpty()) {
            System.out.println("[NOTIFICATION-SCHEDULER] No active buses found.");
            return;
        }

        System.out.println("[NOTIFICATION-SCHEDULER] Active buses: " + activeBusIds);

        for (String busId : activeBusIds) {
            if (busId == null || busId.trim().isEmpty()) {
                continue;
            }

            System.out.println("[NOTIFICATION-SCHEDULER] Processing Bus: " + busId);

            List<DeviceToken> subscribers =
                    busNotificationService.getActiveSubscribers(busId);

            if (subscribers == null || subscribers.isEmpty()) {
                System.out.println(
                        "[NOTIFICATION-SCHEDULER] No subscribers for Bus: " + busId
                );
                continue;
            }

            System.out.println("[NOTIFICATION-SCHEDULER] Subscribers: " + subscribers.size());

            for (DeviceToken subscriber : subscribers) {
                if (subscriber == null) {
                    continue;
                }

                String deviceToken = subscriber.getDeviceToken();
                String boardingStop = subscriber.getBoardingStopId();

                if (deviceToken == null || deviceToken.trim().isEmpty()) {
                    System.out.println(
                            "[NOTIFICATION-SCHEDULER] Empty device token. Skipping."
                    );
                    continue;
                }

                if (boardingStop == null || boardingStop.trim().isEmpty()) {
                    System.out.println(
                            "[NOTIFICATION-SCHEDULER] Empty boarding stop. Skipping."
                    );
                    continue;
                }

                System.out.println(
                        "[NOTIFICATION-SCHEDULER] Calculating ETA"
                                + " | Bus: " + busId
                                + " | Stop: " + boardingStop
                );

                ETAResponse response =
                        etaService.calculateETAForBusAndStop(busId, boardingStop);

                if (response == null) {
                    System.out.println(
                            "[NOTIFICATION-SCHEDULER] No ETA response."
                    );
                    continue;
                }

                System.out.println(
                        "[NOTIFICATION-SCHEDULER] ETA Response"
                                + " | Bus: " + response.getBusId()
                                + " | Status: " + response.getStatus()
                                + " | ETA: " + response.getBoardingEta()
                );

                if ("COMPLETED".equalsIgnoreCase(response.getStatus())) {
                    busNotificationService.unsubscribeFromBus(deviceToken, busId);
                    continue;
                }

                etaNotificationService.checkAndSendNotificationForSubscriber(
                        response,
                        deviceToken
                );
            }
        }
    }
}
