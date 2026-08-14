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

import repository.ScheduleRepository;
 
import simulator.BusAssignmentManager;

import simulator.ScheduleManager;

import simulator.SimulationManager;
 
import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;
 
@WebListener

public class AppStartupListener implements ServletContextListener {
 
    private SimulationManager simulationManager;
 
    @Override

    public void contextInitialized(ServletContextEvent sce) {
 
        System.out.println("[LISTENER] Initializing Bus Simulator Systems...");
 
        BusRepository busRepository = new BusRepository();

        RouteRepository routeRepository = new RouteRepository();

        ScheduleRepository scheduleRepository = new ScheduleRepository();
 
        List<Bus> busInventory = busRepository.getAllBuses();

        List<Schedule> schedules = scheduleRepository.getAllSchedules();

        List<Route> routes = routeRepository.getAllRoutes();

        // Task 1: migrate legacy "availableFrom" values. It now stores the LOCATION where
        // the bus is parked (e.g. "Tirunelveli"). Old time values (e.g. "02:27") or blanks
        // default to the depot location "Tirunelveli" so buses can be assigned.

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
 
        // Safe Stream Mapping: Filters null/empty routeIds and handles duplicates gracefully

        Map<String, Route> routeMap = routes.stream()

                .filter(route -> route != null && route.getRouteId() != null && !route.getRouteId().trim().isEmpty())

                .collect(Collectors.toMap(

                        Route::getRouteId,

                        route -> route,

                        (existing, replacement) -> existing // Keeps the first entry if duplicate routeIds are found

                ));
 
        BusAssignmentManager assignmentManager = new BusAssignmentManager(busInventory);

        ScheduleManager scheduleManager = new ScheduleManager(assignmentManager);

        scheduleManager.initializeSchedules(schedules, routeMap);
 
        simulationManager = new SimulationManager(scheduleManager, routeMap, busInventory);
 
        // Register listener implementation

        simulationManager.addListener(new BusSimulationListener() {
 
            @Override

            public void onBusLocationUpdated(BusLocation busLocation) {

                // Live location updates logic

            }
 
            @Override

            public void onTripStatusChanged(Trip trip) {

                // Trip status change logic

            }
 
            @Override

            public void onBusAssigned(Trip trip, String busId) {

                // Bus assignment logic

            }

        });
 
        sce.getServletContext().setAttribute("simulationManager", simulationManager);

        simulationManager.startSimulation();
 
        System.out.println("[LISTENER] Single-threaded SimulationManager started successfully.");

    }
 
    @Override

    public void contextDestroyed(ServletContextEvent sce) {
 
        System.out.println("[LISTENER] Stopping Bus Simulator...");
 
        if (simulationManager != null) {

            simulationManager.stopSimulation();

        }
 
        System.out.println("[LISTENER] SimulationManager shut down cleanly.");

    }

}
 