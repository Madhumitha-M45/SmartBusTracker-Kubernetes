package service;
import java.util.ArrayList;
import java.util.List;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;
import query.ETAQuery;

public class BusService {
	private final ETAQuery etaQuery = new ETAQuery();
	public List<String> searchStops(String query) {
		return etaQuery.searchStopsByName(query);
	}
	public Stop findStop(String stopName) {
		return etaQuery.findStopByName(stopName);
	}
	public Stop findStopByIdOrName(String stopIdOrName) {
		Stop stop = etaQuery.findStopById(stopIdOrName);
		if (stop.getStopId().isEmpty()) {
			stop = etaQuery.findStopByName(stopIdOrName);
		}
		return stop;
	}
	public Route findRouteById(String routeId) {
		return etaQuery.findRouteById(routeId);
	}
	public List<RouteStop> getRouteStops(String routeId) {
		return etaQuery.getRouteStops(routeId);
	}
	public List<Schedule> getSchedulesForRoute(String routeId) {
		return etaQuery.findSchedulesByRouteId(routeId);
	}
	public RouteStop findRouteStop(List<RouteStop> routeStops, String stopId) {
		for (RouteStop rs : routeStops) {
			if (stopId.equalsIgnoreCase(rs.getStopId())) {
				return rs;
			}
		}
		return new RouteStop();
	}
	public List<BusLocation> getValidBuses(Route route, List<RouteStop> routeStops, int sourceStopOrder) {
		List<BusLocation> buses = etaQuery.findBusesByRouteId(route.getRouteId());
		List<BusLocation> validBuses = new ArrayList<>();
		for (BusLocation bus : buses) {
			String status = bus.getStatus();
			boolean isActiveStatus = "RUNNING".equalsIgnoreCase(status) || "WAITING".equalsIgnoreCase(status);
			if (isActiveStatus) {
				RouteStop currentBusStop = findRouteStop(routeStops, bus.getCurrentStopId());
				if (currentBusStop.getStopOrder() <= sourceStopOrder) {
					validBuses.add(bus);
				}
			}
		}
		return validBuses;
	}
	public BusLocation findActiveBus(String busId) {
		return etaQuery.findBusById(busId);
	}
	public List<String> getActiveBusIds() {
		List<String> busIds = new ArrayList<>();
		for (BusLocation bus : etaQuery.findAllBuses()) {
			String status = bus.getStatus();
			if ("RUNNING".equalsIgnoreCase(status) || "WAITING".equalsIgnoreCase(status)) {
				busIds.add(bus.getBusId());
			}
		}
		return busIds;
	}
}