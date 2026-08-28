package service;

import java.util.ArrayList;
import java.util.List;

import builder.ETAResponseBuilder;
import dto.ETARequest;
import dto.ETAResponse;
import model.Route;
import model.RouteStop;
import model.Schedule;

public class ScheduleService {

	private final ETAResponseBuilder responseBuilder = new ETAResponseBuilder();

	public List<ETAResponse> getScheduledResponses(ETARequest request, Route route, List<Schedule> schedules,
			List<RouteStop> routeStops, RouteStop sourceStop, RouteStop destinationStop) {

		List<ETAResponse> responses = new ArrayList<>();

		String requestedTime = request.getTravelTime();

		for (Schedule schedule : schedules) {

			List<String> departureTimes = schedule.getDepartureTimes();

			List<String> arrivalTimes = schedule.getArrivalTimes();

			for (int i = 0; i < departureTimes.size(); i++) {

				String departureTime = departureTimes.get(i);

				if (departureTime.compareTo(requestedTime) >= 0) {

					String arrivalTime = arrivalTimes.get(i);

					ETAResponse response = responseBuilder.buildScheduledBusResponse(schedule, route, sourceStop,
							destinationStop, departureTime, arrivalTime, routeStops);

					responses.add(response);
				}
			}
		}

		return responses;
	}
}