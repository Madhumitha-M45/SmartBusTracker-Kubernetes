package service;

import java.util.List;
import model.Bus;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;
import repository.BusRepository;
import repository.RouteRepository;
import repository.RouteStopRepository;
import repository.ScheduleRepository;
import repository.StopRepository;

public class AdminService {
	private final BusRepository busRepository;
	private final RouteRepository routeRepository;
	private final RouteStopRepository routeStopRepository;
	private final StopRepository stopRepository;
	private final ScheduleRepository scheduleRepository;

	public AdminService() {
		busRepository = new BusRepository();
		routeRepository = new RouteRepository();
		routeStopRepository = new RouteStopRepository();
		stopRepository = new StopRepository();
		scheduleRepository = new ScheduleRepository();
	}

	public void addBus(Bus bus) {
		if (bus == null)
			throw new IllegalArgumentException("Bus cannot be null");
		if (bus.getBusId() == null || bus.getBusId().trim().isEmpty())
			throw new IllegalArgumentException("Bus ID is required");
		if (bus.getBusNumber() == null || bus.getBusNumber().trim().isEmpty())
			throw new IllegalArgumentException("Bus Number is required");
		busRepository.addBus(bus);
	}

	public List<Bus> getAllBuses() {
		return busRepository.getAllBuses();
	}

	public Bus getBusById(String busId) {
		if (busId == null || busId.trim().isEmpty())
			throw new IllegalArgumentException("Bus ID is required");
		return busRepository.getBusById(busId);
	}

	public void updateBus(Bus bus) {
		if (bus == null)
			throw new IllegalArgumentException("Bus cannot be null");
		if (bus.getBusId() == null || bus.getBusId().trim().isEmpty())
			throw new IllegalArgumentException("Bus ID is required");
		busRepository.updateBus(bus);
	}

	public void deleteBus(String busId) {
		if (busId == null || busId.trim().isEmpty())
			throw new IllegalArgumentException("Bus ID is required");
		busRepository.deleteBus(busId);
	}

	public void addStop(Stop stop) {
		if (stop == null)
			throw new IllegalArgumentException("Stop cannot be null");
		if (stop.getStopId() == null || stop.getStopId().trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		if (stop.getStopName() == null || stop.getStopName().trim().isEmpty())
			throw new IllegalArgumentException("Stop Name is required");
		stopRepository.addStop(stop);
	}

	public List<Stop> getAllStops() {
		return stopRepository.getAllStops();
	}

	public Stop getStopById(String stopId) {
		if (stopId == null || stopId.trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		return stopRepository.getStopById(stopId);
	}

	public void updateStop(Stop stop) {
		if (stop == null)
			throw new IllegalArgumentException("Stop cannot be null");
		if (stop.getStopId() == null || stop.getStopId().trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		stopRepository.updateStop(stop);
	}

	public void deleteStop(String stopId) {
		if (stopId == null || stopId.trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		stopRepository.deleteStop(stopId);
	}

	public void addRoute(Route route) {
		if (route == null)
			throw new IllegalArgumentException("Route cannot be null");
		if (route.getRouteId() == null || route.getRouteId().trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		if (route.getRouteName() == null || route.getRouteName().trim().isEmpty())
			throw new IllegalArgumentException("Route Name is required");
		if (route.getSource() == null || route.getSource().trim().isEmpty())
			throw new IllegalArgumentException("Source is required");
		if (route.getDestination() == null || route.getDestination().trim().isEmpty())
			throw new IllegalArgumentException("Destination is required");
		if (route.getDistance() <= 0)
			throw new IllegalArgumentException("Distance must be greater than 0");
		routeRepository.addRoute(route);
	}

	public List<Route> getAllRoutes() {
		return routeRepository.getAllRoutes();
	}

	public Route getRouteById(String routeId) {
		if (routeId == null || routeId.trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		return routeRepository.getRouteById(routeId);
	}

	public void updateRoute(Route route) {
		if (route == null)
			throw new IllegalArgumentException("Route cannot be null");
		if (route.getRouteId() == null || route.getRouteId().trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		routeRepository.updateRoute(route);
	}

	public void deleteRoute(String routeId) {
		if (routeId == null || routeId.trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		routeRepository.deleteRoute(routeId);
		routeStopRepository.deleteRouteStopsByRouteId(routeId);
	}

	public void addRouteStop(RouteStop routeStop) {
		if (routeStop == null)
			throw new IllegalArgumentException("RouteStop cannot be null");
		if (routeStop.getRouteId() == null || routeStop.getRouteId().trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		if (routeStop.getStopId() == null || routeStop.getStopId().trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		if (routeStop.getStopName() == null || routeStop.getStopName().trim().isEmpty())
			throw new IllegalArgumentException("Stop Name is required");
		if (routeStop.getStopOrder() <= 0)
			throw new IllegalArgumentException("Stop Order must be greater than 0");
		if (routeStop.getDistanceFromPrevious() < 0)
			throw new IllegalArgumentException("Distance cannot be negative");
		routeStop.generateId();
		routeStopRepository.addRouteStop(routeStop);
	}

	public List<RouteStop> getAllRouteStops() {
		return routeStopRepository.getAllRouteStops();
	}

	public List<RouteStop> getRouteStopsByRouteId(String routeId) {
		if (routeId == null || routeId.trim().isEmpty()) {
			throw new IllegalArgumentException("Route ID is required");
		}
		return routeStopRepository.getRouteStopsByRouteId(routeId);
	}

	public void updateRouteStop(RouteStop routeStop) {
		if (routeStop == null)
			throw new IllegalArgumentException("RouteStop cannot be null");
		if (routeStop.getRouteId() == null || routeStop.getRouteId().trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		if (routeStop.getStopId() == null || routeStop.getStopId().trim().isEmpty())
			throw new IllegalArgumentException("Stop ID is required");
		routeStopRepository.updateRouteStop(routeStop);
	}

	public void deleteRouteStopsByRouteId(String routeId) {
		if (routeId == null || routeId.trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		routeStopRepository.deleteRouteStopsByRouteId(routeId);
	}

	public void addSchedule(Schedule schedule) {
		if (schedule == null)
			throw new IllegalArgumentException("Schedule cannot be null");
		if (schedule.getScheduleId() == null || schedule.getScheduleId().trim().isEmpty())
			throw new IllegalArgumentException("Schedule ID is required");
		if (schedule.getRouteId() == null || schedule.getRouteId().trim().isEmpty())
			throw new IllegalArgumentException("Route ID is required");
		scheduleRepository.addSchedule(schedule);
	}

	public List<Schedule> getAllSchedules() {
		return scheduleRepository.getAllSchedules();
	}

	public Schedule getScheduleById(String scheduleId) {
		if (scheduleId == null || scheduleId.trim().isEmpty())
			throw new IllegalArgumentException("Schedule ID is required");
		return scheduleRepository.getScheduleById(scheduleId);
	}

	public void updateSchedule(Schedule schedule) {
		if (schedule == null)
			throw new IllegalArgumentException("Schedule cannot be null");
		if (schedule.getScheduleId() == null || schedule.getScheduleId().trim().isEmpty())
			throw new IllegalArgumentException("Schedule ID is required");
		scheduleRepository.updateSchedule(schedule);
	}

	public void deleteSchedule(String scheduleId) {
		if (scheduleId == null || scheduleId.trim().isEmpty())
			throw new IllegalArgumentException("Schedule ID is required");
		scheduleRepository.deleteSchedule(scheduleId);
	}
}