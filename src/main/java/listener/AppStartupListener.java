package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import model.Bus;
import model.BusLocation;
import model.Route;
import model.Schedule;
import model.Trip;

import repository.BusRepository;
import repository.RouteRepository;
import repository.RouteStopRepository;
import repository.ScheduleRepository;

import simulator.BusAssignmentManager;
import simulator.ScheduleManager;
import simulator.SimulationManager;

import service.NotificationSchedulerManager;

import config.FirebaseConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private SimulationManager simulationManager;
    private NotificationSchedulerManager notificationSchedulerManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================");
        System.out.println("[LISTENER] Initializing Bus Simulator Systems...");
        System.out.println("=================================");

        BusRepository busRepository = new BusRepository();
        RouteRepository routeRepository = new RouteRepository();
        ScheduleRepository scheduleRepository = new ScheduleRepository();
        RouteStopRepository routeStopRepository = new RouteStopRepository();

        List<Bus> busInventory = busRepository.getAllBuses();
        List<Schedule> schedules = scheduleRepository.getAllSchedules();
        List<Route> routes = routeRepository.getAllRoutes();

        for (Bus bus : busInventory) {
            String location = bus.getAvailableFrom();
            boolean isLegacyTime = location != null && location.matches("\\d{1,2}:\\d{2}");
            if (location == null || location.trim().isEmpty() || isLegacyTime) {
                System.out.println("[MIGRATE] Bus " + bus.getBusId() + ": availableFrom '" + location
                        + "' -> 'Tirunelveli' (default depot location)");
                bus.setAvailableFrom("Tirunelveli");
                busRepository.updateBus(bus);
            }
        }

        Map<String, Route> routeMap = routes.stream()
                .filter(route -> route != null && route.getRouteId() != null && !route.getRouteId().trim().isEmpty())
                .collect(Collectors.toMap(
                        Route::getRouteId,
                        route -> route,
                        (existing, replacement) -> existing
                ));

        ConcurrentHashMap<String, Route> concurrentRouteMap = new ConcurrentHashMap<>(routeMap);

        BusAssignmentManager assignmentManager = new BusAssignmentManager(busInventory);

        ScheduleManager scheduleManager = new ScheduleManager(assignmentManager, routeStopRepository);
        scheduleManager.initializeSchedules(schedules, concurrentRouteMap);

        simulationManager = new SimulationManager(scheduleManager, concurrentRouteMap, busInventory, assignmentManager, routeStopRepository);

        simulationManager.addListener(new BusSimulationListener() {
            @Override
            public void onBusLocationUpdated(BusLocation busLocation) {
            }
            @Override
            public void onTripStatusChanged(Trip trip) {
            }
            @Override
            public void onBusAssigned(Trip trip, String busId) {
            }
        });

        sce.getServletContext().setAttribute("simulationManager", simulationManager);

        simulationManager.startSimulation();
        System.out.println("[LISTENER] Single-threaded SimulationManager started successfully.");

        System.out.println("=================================");
        System.out.println("[LISTENER] Initializing Firebase...");
        FirebaseConfig.initialize();
        System.out.println("[LISTENER] Firebase initialization completed.");
        System.out.println("=================================");

        System.out.println("=================================");
        System.out.println("[LISTENER] Starting Notification Scheduler...");
        notificationSchedulerManager = new NotificationSchedulerManager();
        notificationSchedulerManager.start();
        System.out.println("[LISTENER] Notification Scheduler started successfully.");
        System.out.println("=================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Stopping Bus Tracker...");

        if (notificationSchedulerManager != null) {
            System.out.println("[LISTENER] Stopping Notification Scheduler...");
            notificationSchedulerManager.stop();
            System.out.println("[LISTENER] Notification Scheduler stopped successfully.");
        }

        if (simulationManager != null) {
            simulationManager.stopSimulation();
        }
        System.out.println("[LISTENER] SimulationManager shut down cleanly.");
    }
}
