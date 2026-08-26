package service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import builder.ETAResponseBuilder;
import dto.ETARequest;
import dto.ETAResponse;
import model.Route;
import model.RouteStop;
import model.Schedule;
import repository.ScheduleRepository;

public class ScheduleService {

	private final ScheduleRepository scheduleRepository;

	private final ETAResponseBuilder responseBuilder;

	private static final DateTimeFormatter AM_PM_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

	private static final DateTimeFormatter TWENTY_FOUR_HOUR_FORMATTER = DateTimeFormatter.ofPattern("H:mm",
			Locale.ENGLISH);

	public ScheduleService() {

		this.scheduleRepository = new ScheduleRepository();

		this.responseBuilder = new ETAResponseBuilder();
	}

	public List<ETAResponse> getScheduledResponses(ETARequest request, Route route, List<RouteStop> routeStops,
			RouteStop boardingStop, RouteStop destinationStop) {

		List<ETAResponse> responses = new ArrayList<>();

		/*
		 * Validate required objects.
		 */
		if (request == null || route == null || routeStops == null || routeStops.isEmpty() || boardingStop == null
				|| destinationStop == null) {

			return responses;
		}

		if (route.getRouteId() == null || route.getRouteId().trim().isEmpty()) {

			return responses;
		}

		if (boardingStop.getStopName() == null || destinationStop.getStopName() == null) {

			return responses;
		}

		/*
		 * Parse requested travel time safely.
		 */
		LocalTime requestedTime;

		try {

			requestedTime = parseTime(request.getTravelTime());

		} catch (Exception ex) {

			return responses;
		}

		/*
		 * Get schedules.
		 */
		List<Schedule> schedules;

		try {

			schedules = scheduleRepository.getSchedulesByRouteId(route.getRouteId());

		} catch (Exception ex) {

			return responses;
		}

		if (schedules == null || schedules.isEmpty()) {

			return responses;
		}

		String boardingName = boardingStop.getStopName().trim();

		String destinationName = destinationStop.getStopName().trim();

		/*
		 * Process every valid schedule.
		 */
		for (Schedule schedule : schedules) {

			if (schedule == null) {
				continue;
			}

			if (schedule.getSourceName() == null || schedule.getDestinationName() == null) {

				continue;
			}

			if (!schedule.getSourceName().trim().equalsIgnoreCase(boardingName)) {

				continue;
			}

			if (!schedule.getDestinationName().trim().equalsIgnoreCase(destinationName)) {

				continue;
			}

			List<String> departures = schedule.getDepartureTimes();

			List<String> arrivals = schedule.getArrivalTimes();

			if (departures == null || departures.isEmpty() || arrivals == null || arrivals.isEmpty()) {

				continue;
			}

			/*
			 * Never assume departure and arrival lists have the same size.
			 */
			int count = Math.min(departures.size(), arrivals.size());

			for (int i = 0; i < count; i++) {

				String departure = departures.get(i);

				String arrival = arrivals.get(i);

				if (departure == null || departure.trim().isEmpty() || arrival == null || arrival.trim().isEmpty()) {

					continue;
				}

				LocalTime departureTime;

				try {

					departureTime = parseTime(departure);

				} catch (Exception ex) {

					continue;
				}

				/*
				 * User requested time means:
				 *
				 * requested time OR later.
				 *
				 * Example: request = 5:00 PM
				 *
				 * 4:55 PM -> excluded 5:00 PM -> included 5:15 PM -> included
				 */
				if (departureTime.isBefore(requestedTime)) {

					continue;
				}

				try {

					responses.add(buildResponse(schedule, route, boardingStop, destinationStop, departure, arrival));

				} catch (Exception ex) {

					/*
					 * One invalid schedule must not break all other schedules.
					 */
					continue;
				}
			}
		}

		return responses;
	}

	private ETAResponse buildResponse(Schedule schedule, Route route, RouteStop boardingStop, RouteStop destinationStop,
			String departure, String arrival) {

		return responseBuilder.buildScheduledBusResponse(schedule, route, boardingStop, destinationStop, departure,
				arrival);
	}

	private LocalTime parseTime(String time) {

		if (time == null || time.trim().isEmpty()) {

			throw new IllegalArgumentException("Time cannot be null or blank");
		}

		String cleanTime = time.trim();

		try {

			return LocalTime.parse(cleanTime, AM_PM_FORMATTER);

		} catch (Exception ignored) {
		}

		try {

			return LocalTime.parse(cleanTime, TWENTY_FOUR_HOUR_FORMATTER);

		} catch (Exception ignored) {
		}

		try {

			return LocalTime.parse(cleanTime);

		} catch (Exception ex) {

			throw new IllegalArgumentException("Invalid time format: " + time);
		}
	}
}