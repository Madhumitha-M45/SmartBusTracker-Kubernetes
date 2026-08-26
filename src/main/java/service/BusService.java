package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Stop;
import query.ETAQuery;
import repository.RouteRepository;
import repository.RouteStopRepository;
import repository.StopRepository;

public class BusService {

	private final StopRepository stopRepository;
	private final RouteRepository routeRepository;
	private final RouteStopRepository routeStopRepository;
	private final ETAQuery etaQuery;

	public BusService() {
		stopRepository = new StopRepository();
		routeRepository = new RouteRepository();
		routeStopRepository = new RouteStopRepository();
		etaQuery = new ETAQuery();
	}

	public List<String> searchStops(String query) {
		List<String> results = new ArrayList<>();

		for (Stop stop : stopRepository.getAllStops()) {
			if (stop.getStopName().toLowerCase().contains(query.trim().toLowerCase())) {
				results.add(stop.getStopName());
			}
		}

		return results;
	}

	public Stop findStop(String name) {
		for (Stop stop : stopRepository.getAllStops()) {
			if (stop.getStopName().equalsIgnoreCase(name.trim())) {
				return stop;
			}
		}

		return null;
	}

	public Stop findStopById(String id) {
		return stopRepository.getStopById(id.trim());
	}

	public Stop findStopByIdOrName(String value) {
		Stop stop = findStopById(value);

		if (stop != null) {
			return stop;
		}

		return findStop(value);
	}

	public List<Route> findRoutes(String boardingStopId, String destinationStopId) {

		List<Route> routes = new ArrayList<>();

		for (Route route : routeRepository.getAllRoutes()) {

			List<RouteStop> stops = getRouteStops(route.getRouteId());

			RouteStop boarding = findRouteStop(stops, boardingStopId);

			RouteStop destination = findRouteStop(stops, destinationStopId);

			if (boarding == null || destination == null) {
				continue;
			}

			if (boarding.getStopOrder() < destination.getStopOrder()) {

				routes.add(route);
			}
		}

		return routes;
	}

	public Route findRoute(String boardingStopId, String destinationStopId) {

		List<Route> routes = findRoutes(boardingStopId, destinationStopId);

		if (routes.isEmpty()) {
			return null;
		}

		return routes.get(0);
	}

	public Route findRouteById(String routeId) {
		return routeRepository.getRouteById(routeId.trim());
	}

	public List<RouteStop> getRouteStops(String routeId) {

		List<RouteStop> stops = routeStopRepository.getRouteStopsByRouteId(routeId.trim());

		stops.sort(Comparator.comparingInt(RouteStop::getStopOrder));

		return stops;
	}

	public RouteStop findRouteStop(List<RouteStop> stops, String stopId) {

		for (RouteStop stop : stops) {

			if (stop.getStopId().equalsIgnoreCase(stopId.trim())) {

				return stop;
			}
		}

		return null;
	}

	public List<BusLocation> getValidBuses(Route route, List<RouteStop> routeStops, int boardingOrder) {

		List<BusLocation> buses = new ArrayList<>();

		List<BusLocation> activeBuses = etaQuery.findActiveBusesByRoute(route.getRouteId());

		for (BusLocation bus : activeBuses) {

			if (!isBusActive(bus.getStatus())) {
				continue;
			}

			RouteStop currentStop = findCurrentStop(routeStops, bus);

			if (currentStop == null) {
				continue;
			}

			if (currentStop.getStopOrder() > boardingOrder) {
				continue;
			}

			buses.add(bus);
		}

		return buses;
	}

	public BusLocation findActiveBus(String busId) {
		return etaQuery.findActiveBusById(busId.trim());
	}

	public List<String> getActiveBusIds() {
		return etaQuery.findActiveBusIds();
	}

	public boolean isBusActive(String status) {
		return "RUNNING".equalsIgnoreCase(status) || "WAITING".equalsIgnoreCase(status)
				|| "AT_STOP".equalsIgnoreCase(status);
	}

	public RouteStop findCurrentStop(List<RouteStop> stops, BusLocation bus) {

		for (RouteStop stop : stops) {

			if (bus.getCurrentStopId().equalsIgnoreCase(stop.getStopId())) {

				return stop;
			}
		}

		for (RouteStop stop : stops) {

			if (bus.getCurrentStopName().equalsIgnoreCase(stop.getStopName())) {

				return stop;
			}
		}

		return null;
	}

	public String getDisplayStatus(String status) {

		if ("WAITING".equalsIgnoreCase(status)) {
			return "WAITING";
		}

		return "RUNNING";
	}
}