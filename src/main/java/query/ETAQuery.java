package query;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import config.MongoDBConfig;

import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Stop;

import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ETAQuery {

	private final MongoDatabase database;

	public ETAQuery() {
		this.database = MongoDBConfig.getDatabase();
	}

	/*
	 * Search stops beginning with the text entered by the user.
	 */
	public List<String> searchStops(String searchText) {

		List<String> results = new ArrayList<>();

		if (searchText == null || searchText.trim().isEmpty()) {
			return results;
		}

		MongoCollection<Stop> collection = database.getCollection(MongoDBConfig.COLLECTION_STOPS, Stop.class);

		String value = searchText.trim();

		Bson filter = Filters.regex("stopName", Pattern.compile("^" + Pattern.quote(value), Pattern.CASE_INSENSITIVE));

		collection.find(filter).limit(10).forEach(stop -> {

			if (stop != null && stop.getStopName() != null && !stop.getStopName().trim().isEmpty()) {

				results.add(stop.getStopName());
			}
		});

		return results;
	}

	/*
	 * Find stop using stopId.
	 */
	public Stop findStopById(String stopId) {

		if (stopId == null || stopId.trim().isEmpty()) {
			return null;
		}

		MongoCollection<Stop> collection = database.getCollection(MongoDBConfig.COLLECTION_STOPS, Stop.class);

		return collection.find(Filters.eq("stopId", stopId.trim())).first();
	}

	/*
	 * Find stop using exact stop name.
	 */
	public Stop findStopByName(String stopName) {

		if (stopName == null || stopName.trim().isEmpty()) {
			return null;
		}

		MongoCollection<Stop> collection = database.getCollection(MongoDBConfig.COLLECTION_STOPS, Stop.class);

		String value = stopName.trim();

		Bson filter = Filters.regex("stopName",
				Pattern.compile("^" + Pattern.quote(value) + "$", Pattern.CASE_INSENSITIVE));

		return collection.find(filter).first();
	}

	/*
	 * Find an active bus.
	 */
	public BusLocation findActiveBusById(String busId) {

		if (busId == null || busId.trim().isEmpty()) {
			return null;
		}

		MongoCollection<BusLocation> collection = database.getCollection(MongoDBConfig.COLLECTION_BUS_LOCATIONS,
				BusLocation.class);

		Bson filter = Filters.and(Filters.eq("busId", busId.trim()),
				Filters.in("status", "RUNNING", "WAITING", "AT_STOP"));

		return collection.find(filter).first();
	}

	/*
	 * Get all active bus IDs.
	 */
	public List<String> findActiveBusIds() {

		List<String> busIds = new ArrayList<>();

		MongoCollection<BusLocation> collection = database.getCollection(MongoDBConfig.COLLECTION_BUS_LOCATIONS,
				BusLocation.class);

		Bson filter = Filters.in("status", "RUNNING", "WAITING", "AT_STOP");

		collection.find(filter).projection(Projections.include("busId")).forEach(bus -> {

			if (bus != null && bus.getBusId() != null && !bus.getBusId().trim().isEmpty()) {

				busIds.add(bus.getBusId());
			}
		});

		return busIds;
	}

	/*
	 * Get active buses belonging to a route.
	 */
	public List<BusLocation> findActiveBusesByRoute(String routeId) {

		if (routeId == null || routeId.trim().isEmpty()) {
			return new ArrayList<>();
		}

		MongoCollection<BusLocation> collection = database.getCollection(MongoDBConfig.COLLECTION_BUS_LOCATIONS,
				BusLocation.class);

		Bson filter = Filters.and(Filters.eq("routeId", routeId.trim()),
				Filters.in("status", "RUNNING", "WAITING", "AT_STOP"));

		List<BusLocation> buses = collection.find(filter).into(new ArrayList<>());

		buses.removeIf(bus -> bus == null);

		return buses;
	}

	/*
	 * Find route using routeId.
	 */
	public Route findRouteById(String routeId) {

		if (routeId == null || routeId.trim().isEmpty()) {
			return null;
		}

		MongoCollection<Route> collection = database.getCollection(MongoDBConfig.COLLECTION_ROUTES, Route.class);

		return collection.find(Filters.eq("routeId", routeId.trim())).first();
	}

	/*
	 * Find the route where:
	 *
	 * boarding stop comes before destination stop.
	 */
	public String findMatchingRouteId(String boardingStopId, String destinationStopId) {

		if (boardingStopId == null || boardingStopId.trim().isEmpty() || destinationStopId == null
				|| destinationStopId.trim().isEmpty()) {

			return null;
		}

		String boardingId = boardingStopId.trim();
		String destinationId = destinationStopId.trim();

		/*
		 * Same stop cannot be a valid trip.
		 */
		if (boardingId.equalsIgnoreCase(destinationId)) {
			return null;
		}

		MongoCollection<RouteStop> collection = database.getCollection(MongoDBConfig.COLLECTION_ROUTE_STOPS,
				RouteStop.class);

		List<RouteStop> boardingStops = collection.find(Filters.eq("stopId", boardingId)).into(new ArrayList<>());

		if (boardingStops == null || boardingStops.isEmpty()) {
			return null;
		}

		/*
		 * Remove invalid RouteStop records.
		 */
		boardingStops.removeIf(rs -> rs == null || rs.getRouteId() == null || rs.getRouteId().trim().isEmpty());

		if (boardingStops.isEmpty()) {
			return null;
		}

		Set<String> candidateRouteIds = boardingStops.stream().map(RouteStop::getRouteId)
				.filter(id -> id != null && !id.trim().isEmpty()).map(String::trim).collect(Collectors.toSet());

		if (candidateRouteIds.isEmpty()) {
			return null;
		}

		List<RouteStop> destinationStops = collection
				.find(Filters.and(Filters.in("routeId", candidateRouteIds), Filters.eq("stopId", destinationId)))
				.into(new ArrayList<>());

		if (destinationStops == null || destinationStops.isEmpty()) {

			return null;
		}

		destinationStops.removeIf(rs -> rs == null || rs.getRouteId() == null || rs.getRouteId().trim().isEmpty());

		/*
		 * Find a route where destination comes after boarding.
		 */
		for (RouteStop boardingStop : boardingStops) {

			if (boardingStop == null || boardingStop.getRouteId() == null) {

				continue;
			}

			String boardingRouteId = boardingStop.getRouteId().trim();

			for (RouteStop destinationStop : destinationStops) {

				if (destinationStop == null || destinationStop.getRouteId() == null) {

					continue;
				}

				String destinationRouteId = destinationStop.getRouteId().trim();

				if (boardingRouteId.equals(destinationRouteId)
						&& destinationStop.getStopOrder() > boardingStop.getStopOrder()) {

					return boardingRouteId;
				}
			}
		}

		return null;
	}

	/*
	 * Get all RouteStop records for a route, ordered by stopOrder.
	 */
	public List<RouteStop> getRouteStopsByRouteId(String routeId) {

		if (routeId == null || routeId.trim().isEmpty()) {
			return new ArrayList<>();
		}

		MongoCollection<RouteStop> collection = database.getCollection(MongoDBConfig.COLLECTION_ROUTE_STOPS,
				RouteStop.class);

		List<RouteStop> routeStops = collection.find(Filters.eq("routeId", routeId.trim()))
				.sort(Sorts.ascending("stopOrder")).into(new ArrayList<>());

		if (routeStops == null || routeStops.isEmpty()) {
			return new ArrayList<>();
		}

		/*
		 * Do not allow null RouteStop objects to enter ETA calculation.
		 */
		routeStops.removeIf(rs -> rs == null);

		return routeStops;
	}
}