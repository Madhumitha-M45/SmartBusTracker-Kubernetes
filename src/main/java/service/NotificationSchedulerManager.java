package service;
 
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;
 
public class NotificationSchedulerManager {
 
    private final NotificationScheduler notificationScheduler = new NotificationScheduler();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService workerPool = Executors.newFixedThreadPool(10);
 
    public void start() {

        scheduler.scheduleAtFixedRate(

                () -> notificationScheduler.processActiveBusNotifications(workerPool),

                0,

                1,

                TimeUnit.MINUTES

        );

    }
 
    public void stop() {

        scheduler.shutdown();

        workerPool.shutdown();

    }

}
 