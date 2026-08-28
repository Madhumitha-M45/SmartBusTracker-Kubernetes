package notification;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public class FCMNotificationService {
    public boolean sendNotification(
            String deviceToken,
            String title,
            String body) {
        try {
            Notification notification = createNotification(title, body);
            AndroidConfig androidConfig = createAndroidConfig();
            Message message =createMessage(deviceToken,notification, androidConfig);
            FirebaseMessaging.getInstance().send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private Notification createNotification(
            String title,
            String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
    private AndroidConfig createAndroidConfig() {
        AndroidNotification notification =
                AndroidNotification.builder()
                        .setSound("default")
                        .setChannelId("bus_eta_alerts")
                        .build();
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(notification)
                .build();
    }
    private Message createMessage(
            String deviceToken,
            Notification notification,
            AndroidConfig androidConfig) {
        return Message.builder()
                .setToken(deviceToken)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .build();
    }
}