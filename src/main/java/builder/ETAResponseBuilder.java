package builder;

import java.util.ArrayList;
import java.util.List;

import dto.ETAResponse;
import dto.RouteStopDetailDTO;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;

public class ETAResponseBuilder {

	public ETAResponse buildRunningBusResponse(BusLocation bus, Route route, RouteStop boardingRS,
			RouteStop destinationRS, double speed, double boardingDistance, double tripDistance, long boardingEta,
			long destinationEta, String boardingArrivalTime, String destinationArrivalTime,
			List<RouteStopDetailDTO> timeline) {

		ETAResponse response = new ETAResponse();

		response.setBusId(bus.getBusId());
		response.setBusNumber(getBusNumber(bus));
		response.setStatus(bus.getStatus());
		response.setSpeed(speed);

		response.setCurrentStop(bus.getCurrentStopName());
		response.setNextStop(bus.getNextStopName());
		response.setDistanceToNextStop(bus.getDistanceRemaining());

		response.setBoardingStop(boardingRS.getStopName());
		response.setDestinationStop(destinationRS.getStopName());

		response.setRemainingDistanceToBoardingStop(boardingDistance);

		response.setEtaToBoardingStop(boardingEta);

		response.setBusArrivalTimeAtBoardingStop(boardingArrivalTime);

		response.setRemainingDistanceToDestination(boardingDistance + tripDistance);

		response.setEtaToDestinationStop(destinationEta);

		response.setBusArrivalTimeAtDestinationStop(destinationArrivalTime);

		response.setStartingFrom(route.getSource());

		response.setDepartureTime("N/A");

		response.setLastUpdated(bus.getLastUpdated());

		response.setRouteStops(timeline);

		return response;
	}

	public ETAResponse buildScheduledBusResponse(Schedule schedule, Route route, RouteStop boardingRS,
			RouteStop destinationRS, String departureTime, String arrivalTime, List<RouteStop> routeStops) {

		ETAResponse response = new ETAResponse();

		response.setBusId(schedule.getScheduleId());

		response.setBusNumber(getScheduleBusNumber(schedule));

		response.setStatus("SCHEDULED");

		response.setSpeed(0.0);

		response.setCurrentStop(route.getSource());

		response.setNextStop(getNextStop(routeStops, boardingRS));

		response.setDistanceToNextStop(getDistanceToNextStop(routeStops, boardingRS));

		response.setBoardingStop(boardingRS.getStopName());

		response.setDestinationStop(destinationRS.getStopName());

		response.setRemainingDistanceToBoardingStop(0.0);

		/*
		 * Scheduled bus does not have a running ETA. -1 means
		 * "not available/applicable". The mobile app can use status = SCHEDULED to
		 * display the scheduled departure/arrival time.
		 */
		response.setEtaToBoardingStop(-1);

		response.setBusArrivalTimeAtBoardingStop(departureTime);

		response.setRemainingDistanceToDestination(0.0);

		response.setEtaToDestinationStop(-1);

		response.setBusArrivalTimeAtDestinationStop(arrivalTime);

		response.setStartingFrom(route.getSource());

		response.setDepartureTime(departureTime);

		response.setLastUpdated("N/A");

		response.setRouteStops(new ArrayList<>());

		return response;
	}

	public ETAResponse buildNotificationResponse(BusLocation bus, Route route, Stop boardingStop, double distance,
			long eta, String arrivalTime, List<RouteStopDetailDTO> timeline) {

		ETAResponse response = new ETAResponse();

		response.setBusId(bus.getBusId());

		response.setBusNumber(getBusNumber(bus));

		response.setStatus(bus.getStatus());

		response.setSpeed(bus.getSpeed());

		response.setCurrentStop(bus.getCurrentStopName());

		response.setNextStop(bus.getNextStopName());

		response.setDistanceToNextStop(bus.getDistanceRemaining());

		response.setBoardingStop(boardingStop.getStopName());

		response.setDestinationStop(boardingStop.getStopName());

		response.setRemainingDistanceToBoardingStop(distance);

		// ETA is returned as total minutes
		response.setEtaToBoardingStop(eta);

		response.setBusArrivalTimeAtBoardingStop(arrivalTime);

		response.setRemainingDistanceToDestination(0.0);

		response.setEtaToDestinationStop(-1);

		response.setBusArrivalTimeAtDestinationStop("N/A");

		response.setStartingFrom(route.getSource());

		response.setDepartureTime("N/A");

		response.setLastUpdated(bus.getLastUpdated());

		response.setRouteStops(timeline);

		return response;
	}

	private String getBusNumber(BusLocation bus) {

		try {

			return (String) bus.getClass().getMethod("getBusNumber").invoke(bus);

		} catch (Exception ignored) {

			return bus.getBusId();
		}
	}

	private String getScheduleBusNumber(Schedule schedule) {

		try {

			return (String) schedule.getClass().getMethod("getBusNumber").invoke(schedule);

		} catch (Exception ignored) {

			return schedule.getScheduleId();
		}
	}

	private String getNextStop(List<RouteStop> routeStops, RouteStop boardingRS) {

		int boardingOrder = boardingRS.getStopOrder();

		for (RouteStop rs : routeStops) {

			if (rs.getStopOrder() == boardingOrder + 1) {

				return rs.getStopName();
			}
		}

		return "N/A";
	}

	private double getDistanceToNextStop(List<RouteStop> routeStops, RouteStop boardingRS) {

		int boardingOrder = boardingRS.getStopOrder();

		for (RouteStop rs : routeStops) {

			if (rs.getStopOrder() == boardingOrder + 1) {

				return rs.getDistanceFromPrevious();
			}
		}

		return 0.0;
	}

	public RouteStopDetailDTO buildRouteStopDetail(RouteStop routeStop, double distance, long eta) {

		return new RouteStopDetailDTO(routeStop.getStopName(), distance, eta);
	}
}