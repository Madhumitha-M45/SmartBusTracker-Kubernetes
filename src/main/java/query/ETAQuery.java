package query;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.MongoDBConfig;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;

public class ETAQuery {

	private final MongoCollection<Route> routeRepository;
	private final MongoCollection<RouteStop> routeStopCollection;
	private final MongoCollection<Stop> stopCollection;
	private final MongoCollection<BusLocation> busLocationCollection;
	private final MongoCollection<Schedule> scheduleCollection;

	public ETAQuery() {
		MongoDatabase database = MongoDBConfig.getDatabase();
		routeRepository = database.getCollection("Route", Route.class);
		routeStopCollection = database.getCollection("RouteStop", RouteStop.class);
		stopCollection = database.getCollection("Stop", Stop.class);
		busLocationCollection = database.getCollection("BusLocation", BusLocation.class);
		scheduleCollection = database.getCollection("Schedule", Schedule.class);
	}

	public List<String> searchStopsByName(String query) {
		List<String> names = new ArrayList<>();
		if (query == null || query.trim().isEmpty()) {

			return names;

		}
		Pattern p = Pattern.compile(Pattern.quote(query.trim()),Pattern.CASE_INSENSITIVE);
		for (Stop s : stopCollection.find(eq("stopName", p))) {

			names.add(s.getStopName());

		}
		return names;
	}

	public Stop findStopByName(String name) {
		return stopCollection.find(eq("stopName", name)).first();
	}

	public Stop findStopById(String id) {
		return stopCollection.find(eq("stopId", id)).first();
	}

	public Route findRouteById(String routeId) {
		if (routeId == null || routeId.trim().isEmpty())
			return null;
		return routeRepository
				.find(eq("routeId", Pattern.compile("^" + routeId.trim() + "$", Pattern.CASE_INSENSITIVE))).first();
	}

	public List<RouteStop> getRouteStops(String routeId) {
		return routeStopCollection.find(eq("routeId", routeId)).into(new ArrayList<>());
	}

	public List<Schedule> findSchedulesByRouteId(String routeId) {
		return scheduleCollection.find(eq("routeId", routeId)).into(new ArrayList<>());
	}

	public List<String> findMatchingRouteIds(String sourceId, String destId) {
		List<String> routeIds = new ArrayList<>();
		List<RouteStop> sources = routeStopCollection.find(eq("stopId", sourceId)).into(new ArrayList<>());

		for (RouteStop src : sources) {
			RouteStop dest = routeStopCollection.find(and(eq("routeId", src.getRouteId()), eq("stopId", destId)))
					.first();
			if (dest != null && src.getStopOrder() < dest.getStopOrder()) {
				routeIds.add(src.getRouteId());
			}
		}
		return routeIds;
	}

	public List<RouteStop> getRouteStopsForRoutes(List<String> routeIds) {
		List<RouteStop> result = new ArrayList<>();
		for (String id : routeIds) {
			result.addAll(getRouteStops(id));
		}
		return result;
	}

	public List<BusLocation> findBusesByRouteId(String routeId) {
		return busLocationCollection.find(eq("routeId", routeId)).into(new ArrayList<>());
	}

	public BusLocation findBusById(String busId) {
		return busLocationCollection.find(eq("busId", busId)).first();
	}

	public List<BusLocation> findAllBuses() {
		return busLocationCollection.find().into(new ArrayList<>());
	}
}