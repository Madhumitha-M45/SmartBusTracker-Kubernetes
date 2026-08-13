package service;

import java.util.List;

import model.Bus;
import model.Route;
import model.RouteStop;
import model.Stop;
import model.Schedule;
import model.BusLocation;

import repository.BusRepository;
import repository.RouteRepository;
import repository.RouteStopRepository;
import repository.StopRepository;
import repository.ScheduleRepository;
import repository.BusLocationRepository;

public class AdminService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;
    private final ScheduleRepository scheduleRepository;
    private final BusLocationRepository busLocationRepository;

    public AdminService() {

        busRepository = new BusRepository();
        routeRepository = new RouteRepository();
        routeStopRepository = new RouteStopRepository();
        stopRepository = new StopRepository();
        scheduleRepository = new ScheduleRepository();
        busLocationRepository = new BusLocationRepository();
    }

    // =========================================================
    // BUS
    // =========================================================

    public void addBus(Bus bus) {
        busRepository.addBus(bus);
    }

    public List<Bus> getAllBuses() {
        return busRepository.getAllBuses();
    }

    public Bus getBusById(String busId) {
        return busRepository.getBusById(busId);
    }

    public void updateBus(Bus bus) {
        busRepository.updateBus(bus);
    }

    public void deleteBus(String busId) {
        busRepository.deleteBus(busId);
    }


    // =========================================================
    // ROUTE
    // =========================================================

    public void addRoute(Route route) {
        routeRepository.addRoute(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.getAllRoutes();
    }

    public Route getRouteById(String routeId) {
        return routeRepository.getRouteById(routeId);
    }

    public void updateRoute(Route route) {
        routeRepository.updateRoute(route);
    }

    public void deleteRoute(String routeId) {
        routeRepository.deleteRoute(routeId);
    }


    // =========================================================
    // ROUTE STOP
    // =========================================================

    public void addRouteStop(RouteStop routeStop) {
        routeStopRepository.addRouteStop(routeStop);
    }

    public List<RouteStop> getAllRouteStops() {
        return routeStopRepository.getAllRouteStops();
    }

    public List<RouteStop> getRouteStopsByRouteId(String routeId) {
        return routeStopRepository.getRouteStopsByRouteId(routeId);
    }

    public void updateRouteStop(RouteStop routeStop) {
        routeStopRepository.updateRouteStop(routeStop);
    }

    public void deleteRouteStopsByRouteId(String routeId) {
        routeStopRepository.deleteRouteStopsByRouteId(routeId);
    }


    // =========================================================
    // STOP
    // =========================================================

    public void addStop(Stop stop) {
        stopRepository.addStop(stop);
    }

    public List<Stop> getAllStops() {
        return stopRepository.getAllStops();
    }

    public Stop getStopById(String stopId) {
        return stopRepository.getStopById(stopId);
    }

    public void updateStop(Stop stop) {
        stopRepository.updateStop(stop);
    }

    public void deleteStop(String stopId) {
        stopRepository.deleteStop(stopId);
    }


    // =========================================================
    // SCHEDULE
    // =========================================================

    public void addSchedule(Schedule schedule) {
        scheduleRepository.addSchedule(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.getAllSchedules();
    }

    public Schedule getScheduleById(String scheduleId) {
        return scheduleRepository.getScheduleById(scheduleId);
    }

    public void updateSchedule(Schedule schedule) {
        scheduleRepository.updateSchedule(schedule);
    }

    public void deleteSchedule(String scheduleId) {
        scheduleRepository.deleteSchedule(scheduleId);
    }


    // =========================================================
    // BUS LOCATION
    // =========================================================

    public void addBusLocation(BusLocation location) {
        busLocationRepository.addBusLocation(location);
    }

    public List<BusLocation> getAllBusLocations() {
        return busLocationRepository.getAllBusLocations();
    }

    public BusLocation getBusLocationByBusId(String busId) {
        return busLocationRepository.getBusLocationByBusId(busId);
    }

    public void updateBusLocation(BusLocation location) {
        busLocationRepository.updateBusLocation(location);
    }

    public void deleteBusLocation(String busId) {
        busLocationRepository.deleteBusLocation(busId);
    }
}