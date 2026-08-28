package service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import builder.ETAResponseBuilder;
import dto.ETARequest;
import dto.ETAResponse;
import dto.RouteStopDetailDTO;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;
import query.ETAQuery;
public class ETAService {

	private final BusService busService = new BusService();
	private final ScheduleService scheduleService = new ScheduleService();
	private final ETAResponseBuilder responseBuilder = new ETAResponseBuilder();
	private final ETAQuery etaQuery = new ETAQuery();
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
	public List<String> searchStops(String query) {
		return busService.searchStops(query);
	}
	public List<ETAResponse> searchBus(ETARequest request) {

		List<ETAResponse> responses = new ArrayList<>();
		Stop source = busService.findStop(request.getBoardingStop());
		Stop destination = busService.findStop(request.getDestinationStop());
		List<String> routeIds = etaQuery.findMatchingRouteIds(source.getStopId(), destination.getStopId());
		for (String routeId : routeIds) {
			Route route = busService.findRouteById(routeId);
			List<RouteStop> routeStops = busService.getRouteStops(route.getRouteId());
			RouteStop sourceStop = busService.findRouteStop(routeStops, source.getStopId());
			RouteStop destinationStop = busService.findRouteStop(routeStops, destination.getStopId());
			List<BusLocation> liveBuses = busService.getValidBuses(route, routeStops, sourceStop.getStopOrder());
			if (liveBuses.isEmpty()) {
				List<Schedule> schedules = busService.getSchedulesForRoute(route.getRouteId());
				responses.addAll(scheduleService.getScheduledResponses(request, route, schedules, routeStops,sourceStop, destinationStop));
			} else {
				for (BusLocation bus : liveBuses) {
					responses.add(buildLiveBusResponse(route, routeStops, sourceStop, destinationStop, bus));
				}
			}
		}
		return responses;
	}

	private ETAResponse buildLiveBusResponse(Route route, List<RouteStop> routeStops, RouteStop sourceStop,RouteStop destinationStop, BusLocation bus) {
		double distanceToSource = ETACalculator.calculateDistanceToStop(routeStops, bus, sourceStop.getStopOrder());
		long etaToSource = ETACalculator.calculateETAMinutes(distanceToSource, bus.getSpeed());
		double distanceToDestination = ETACalculator.calculateDistanceBetweenStops(routeStops,
				sourceStop.getStopOrder(), destinationStop.getStopOrder());
		long destinationTravelTime = ETACalculator.calculateETAMinutes(distanceToDestination, bus.getSpeed());
		long etaToDestination = etaToSource + destinationTravelTime;
		String boardingArrivalTime = calculateArrivalTime(etaToSource);
		String destinationArrivalTime = calculateArrivalTime(etaToDestination);
		List<RouteStopDetailDTO> stopDetails = new ArrayList<>();
		for (RouteStop rs : routeStops) {
			if (rs.getStopOrder() >= sourceStop.getStopOrder()) {
				double stopDistance = ETACalculator.calculateDistanceToStop(routeStops, bus, rs.getStopOrder());
				long stopEta = ETACalculator.calculateETAMinutes(stopDistance, bus.getSpeed());
				Stop stopEntity = busService.findStopByIdOrName(rs.getStopId());
				RouteStopDetailDTO dto = responseBuilder.buildRouteStopDetail(rs, stopDistance, stopEta);
				dto.setStopName(stopEntity.getStopName());
				stopDetails.add(dto);
			}
		}
		return responseBuilder.buildRunningBusResponse(bus, route, sourceStop, destinationStop, bus.getSpeed(),
				distanceToSource, distanceToDestination, etaToSource, etaToDestination, boardingArrivalTime,
				destinationArrivalTime, stopDetails);
	}
	private String calculateArrivalTime(long etaMinutes) {
		LocalTime currentTime = LocalTime.now();
		LocalTime arrivalTime = currentTime.plusMinutes(etaMinutes);
		return arrivalTime.format(timeFormatter);
	}
	public List<ETAResponse> calculateETAForAllBuses(ETARequest request) {

		return searchBus(request);
	}
	public ETAResponse calculateETAForBusAndStop(String busId, String boardingStopId) {
		BusLocation bus = busService.findActiveBus(busId);
		Stop boardingStop = busService.findStopByIdOrName(boardingStopId);
		Route route = busService.findRouteById(bus.getRouteId());
		List<RouteStop> routeStops = busService.getRouteStops(route.getRouteId());
		RouteStop sourceStop = busService.findRouteStop(routeStops, boardingStop.getStopId());
		double distance = ETACalculator.calculateDistanceToStop(routeStops, bus, sourceStop.getStopOrder());
		long eta = ETACalculator.calculateETAMinutes(distance, bus.getSpeed());
		String arrivalTime = calculateArrivalTime(eta);
		return responseBuilder.buildNotificationResponse(bus, route, boardingStop, distance, eta, arrivalTime,
				new ArrayList<>());
	}
	public List<String> getActiveBusIds() {
		return busService.getActiveBusIds();
	}
} 