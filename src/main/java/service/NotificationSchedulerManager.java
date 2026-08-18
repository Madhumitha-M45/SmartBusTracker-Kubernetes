package service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationSchedulerManager {

    private final NotificationScheduler notificationScheduler =
            new NotificationScheduler();

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    public void start() {
        System.out.println("=================================");
        System.out.println("[NOTIFICATION-SCHEDULER] Starting...");
        System.out.println("=================================");

        executor.scheduleAtFixedRate(
                () -> {
                    try {
                        System.out.println(
                                "[NOTIFICATION-SCHEDULER] Running notification check..."
                        );
                        notificationScheduler.processActiveBusNotifications();
                    } catch (Exception e) {
                        System.err.println("[NOTIFICATION-SCHEDULER] Error");
                        e.printStackTrace();
                    }
                },
                0,
                1,
                TimeUnit.MINUTES
        );
    }

    public void stop() {
        System.out.println("[NOTIFICATION-SCHEDULER] Stopping...");
        executor.shutdown();
        System.out.println("[NOTIFICATION-SCHEDULER] Stopped.");
    }
}
