package listener;

import config.FirebaseConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.NotificationSchedulerManager;
import service.TelemetrySubscriber;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private NotificationSchedulerManager notificationSchedulerManager;
    private TelemetrySubscriber telemetrySubscriber;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("[LISTENER] Starting Bus Tracker...");

        FirebaseConfig.initialize();

        notificationSchedulerManager = new NotificationSchedulerManager();
        notificationSchedulerManager.start();

        telemetrySubscriber = new TelemetrySubscriber();
        telemetrySubscriber.start();

        System.out.println("[LISTENER] Bus Tracker started successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("[LISTENER] Stopping Bus Tracker...");

        if (telemetrySubscriber != null) {
            telemetrySubscriber.stop();
        }
        if (notificationSchedulerManager != null) {
            notificationSchedulerManager.stop();
        }

        System.out.println("[LISTENER] Bus Tracker stopped.");
    }
}
 