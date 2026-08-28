package service;

import java.util.List;

import model.BusLocation;
import model.RouteStop;

public class ETACalculator {

	public static RouteStop findRouteStop(List<RouteStop> routeStops, String stopId) {

		for (RouteStop rs : routeStops) {
			if (stopId.equalsIgnoreCase(rs.getStopId())) {
				return rs;
			}
		}
		return new RouteStop();
	}

	public static double calculateDistanceToStop(List<RouteStop> routeStops, BusLocation bus, int targetStopOrder) {

		String currentStopId = bus.getCurrentStopId();
		RouteStop currentStop = findRouteStop(routeStops, currentStopId);
		int currentOrder = currentStop.getStopOrder();
		if (targetStopOrder <= currentOrder) {
			return 0.0;
		}
		double totalDistance = 0.0;
		for (RouteStop rs : routeStops) {
			if (rs.getStopOrder() > currentOrder && rs.getStopOrder() <= targetStopOrder) {
				totalDistance += rs.getDistanceFromPrevious();
			}
		}
		return totalDistance;
	}

	public static double calculateDistanceBetweenStops(List<RouteStop> routeStops, int startOrder, int endOrder) {
		if (startOrder >= endOrder) {
			return 0.0;
		}
		double totalDistance = 0.0;
		for (RouteStop rs : routeStops) {
			if (rs.getStopOrder() > startOrder && rs.getStopOrder() <= endOrder) {
				totalDistance += rs.getDistanceFromPrevious();
			}
		}
		return totalDistance;
	}

	public static long calculateETAMinutes(double distanceKm, double speedKmh) {
		if (speedKmh <= 0) {
			speedKmh = 40.0;
		}
		double hours = distanceKm / speedKmh;
		return Math.round(hours * 60.0);
	}
}