package notification;
import java.util.concurrent.ExecutorService;
import dto.ETAResponse;
import model.DeviceToken;
import service.ETAService;
 
public class NotificationScheduler {
    private final ETAService etaService = new ETAService();
    private final BusNotificationService busNotificationService = new BusNotificationService();
    private final ETANotificationService etaNotificationService = new ETANotificationService();
    public void processActiveBusNotifications(ExecutorService workerPool) {
        for (String busId : etaService.getActiveBusIds()) {
            for (DeviceToken subscriber : busNotificationService.getActiveSubscribers(busId)) {
                workerPool.submit(() -> processSubscriber(busId, subscriber));
            }
        }
    }
    private void processSubscriber(String busId, DeviceToken subscriber) {
        ETAResponse response = etaService.calculateETAForBusAndStop(busId, subscriber.getBoardingStop());
        if (response == null) {
            return;       
            }
        String token = subscriber.getDeviceToken();
        etaNotificationService.checkAndSendNotificationForSubscriber(response, token);
        
        if ("COMPLETED".equalsIgnoreCase(response.getStatus())) {
            busNotificationService.unsubscribeFromBus(token, busId);
            etaNotificationService.clearSubscriberState(busId, token);
            return;
        }
    }
}