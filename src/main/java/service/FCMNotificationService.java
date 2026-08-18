package service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public class FCMNotificationService {

    public boolean sendNotification(
            String deviceToken,
            String title,
            String body) {

        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            System.err.println("[FCM] Device token is empty");
            return false;
        }

        try {
            System.out.println("[FCM] Creating notification message...");

            Notification notification =
                    Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build();

            AndroidConfig androidConfig =
                    AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(
                                    AndroidNotification.builder()
                                            .setSound("default")
                                            .setChannelId("bus_eta_alerts")
                                            .build()
                            )
                            .build();

            ApnsConfig apnsConfig =
                    ApnsConfig.builder()
                            .setAps(
                                    Aps.builder()
                                            .setSound("default")
                                            .setContentAvailable(true)
                                            .build()
                            )
                            .build();

            Message message =
                    Message.builder()
                            .setToken(deviceToken)
                            .setNotification(notification)
                            .setAndroidConfig(androidConfig)
                            .setApnsConfig(apnsConfig)
                            .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                            .build();

            System.out.println("[FCM] Sending message to Firebase...");

            String response =
                    FirebaseMessaging.getInstance().send(message);

            System.out.println("[FCM] Message delivered successfully");
            System.out.println("[FCM] Firebase Message ID: " + response);

            return true;

        } catch (Exception e) {
            System.err.println("[FCM] Failed to deliver notification");
            System.err.println("[FCM] Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
