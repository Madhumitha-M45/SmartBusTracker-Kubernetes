package builder;

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

		return new ETAResponse(bus.getBusId(), null, bus.getStatus(), null, speed, bus.getCurrentStopName(),
				bus.getNextStopName(), bus.getDistanceRemaining(), boardingRS.getStopName(), boardingDistance,
				String.valueOf(boardingEta), boardingArrivalTime, destinationRS.getStopName(), tripDistance,
				String.valueOf(destinationEta), destinationArrivalTime, route.getSource(), "N/A", 0.0, 0.0,
				bus.getLastUpdated(), timeline);
	}

	public ETAResponse buildScheduledBusResponse(Schedule schedule, Route route, RouteStop boardingRS,
			RouteStop destinationRS, String departureTime, String arrivalTime) {

		return new ETAResponse(schedule.getScheduleId(), null, "SCHEDULED", "Scheduled", 0.0, "N/A",
				boardingRS.getStopName(), 0.0, boardingRS.getStopName(), 0.0, "Scheduled", departureTime,
				destinationRS.getStopName(), 0.0, "Scheduled", arrivalTime, route.getSource(), departureTime, 0.0, 0.0,
				"N/A", null);
	}

	public ETAResponse buildNotificationResponse(BusLocation bus, Route route, Stop boardingStop, double distance,
			long etaMinutes, String arrivalTime, List<RouteStopDetailDTO> timeline) {

		return new ETAResponse(bus.getBusId(), null, bus.getStatus(), null, bus.getSpeed(), bus.getCurrentStopName(),
				bus.getNextStopName(), bus.getDistanceRemaining(), boardingStop.getStopName(), distance,
				String.valueOf(etaMinutes), arrivalTime, "N/A", 0.0, "N/A", "N/A", route.getSource(), "N/A", 0.0, 0.0,
				bus.getLastUpdated(), timeline);
	}

	public RouteStopDetailDTO buildRouteStopDetail(RouteStop routeStop, String expectedArrivalText, double distanceAway,
			boolean isCurrentStop) {

		RouteStopDetailDTO dto = new RouteStopDetailDTO();

		dto.setStopId(routeStop.getStopId());

		dto.setStopName(routeStop.getStopName());

		dto.setStopOrder(routeStop.getStopOrder());

		dto.setExpectedArrivalText(expectedArrivalText);

		dto.setDistanceAwayText(String.format("%.2f km", distanceAway));

		dto.setCurrentStop(isCurrentStop);

		return dto;
	}
}