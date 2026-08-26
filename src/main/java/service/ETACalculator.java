package service;

import model.BusLocation;
import model.RouteStop;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

public class ETACalculator {

	private static final double DEFAULT_SPEED = 30.0;

	private static final DateTimeFormatter AM_PM_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

	private static final DateTimeFormatter TWENTY_FOUR_HOUR_FORMATTER = DateTimeFormatter.ofPattern("H:mm",
			Locale.ENGLISH);

	private static final DateTimeFormatter FLEXIBLE_TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-M-d").optionalStart().appendPattern("'T'").optionalEnd().optionalStart()
			.appendPattern(" ").optionalEnd().appendPattern("H:m").optionalStart().appendPattern(":s").optionalEnd()
			.toFormatter(Locale.ENGLISH);

	private ETACalculator() {
	}

	public static double roundDistance(double distance) {

		if (Double.isNaN(distance) || Double.isInfinite(distance) || distance < 0) {

			return 0.0;
		}

		return Math.round(distance * 10.0) / 10.0;
	}

	/*
	 * Calculate ETA in minutes.
	 */
	public static long calculateETAMinutes(double distanceKm, double speedKmPerHour) {

		if (Double.isNaN(distanceKm) || Double.isInfinite(distanceKm) || distanceKm <= 0) {

			return 0;
		}

		double effectiveSpeed = (Double.isNaN(speedKmPerHour) || Double.isInfinite(speedKmPerHour)
				|| speedKmPerHour <= 0) ? DEFAULT_SPEED : speedKmPerHour;

		double hours = distanceKm / effectiveSpeed;

		long minutes = Math.round(hours * 60.0);

		return Math.max(1, minutes);
	}

	/*
	 * Calculate distance from the current bus position to the target stop.
	 *
	 * The calculation starts from the bus's next stop.
	 */
	public static double calculateDistanceToStop(List<RouteStop> routeStops, BusLocation bus, int targetStopOrder) {

		if (routeStops == null || routeStops.isEmpty() || bus == null) {

			return 0.0;
		}

		RouteStop nextStop = null;

		String nextStopId = bus.getNextStopId();

		/*
		 * First try next stop ID.
		 */
		if (nextStopId != null && !nextStopId.trim().isEmpty()) {

			String cleanNextStopId = nextStopId.trim();

			for (RouteStop rs : routeStops) {

				if (rs == null || rs.getStopId() == null) {

					continue;
				}

				if (rs.getStopId().trim().equalsIgnoreCase(cleanNextStopId)) {

					nextStop = rs;
					break;
				}
			}
		}

		/*
		 * If next stop ID is unavailable, try next stop name.
		 */
		if (nextStop == null) {

			String nextStopName = bus.getNextStopName();

			if (nextStopName != null && !nextStopName.trim().isEmpty()) {

				String cleanNextStopName = nextStopName.trim();

				for (RouteStop rs : routeStops) {

					if (rs == null || rs.getStopName() == null) {

						continue;
					}

					if (rs.getStopName().trim().equalsIgnoreCase(cleanNextStopName)) {

						nextStop = rs;
						break;
					}
				}
			}
		}

		/*
		 * If next stop cannot be resolved, ETA cannot be calculated reliably.
		 */
		if (nextStop == null) {
			return 0.0;
		}

		/*
		 * Bus is already beyond target stop.
		 */
		if (nextStop.getStopOrder() > targetStopOrder) {
			return 0.0;
		}

		double distanceRemaining = bus.getDistanceRemaining();

		if (Double.isNaN(distanceRemaining) || Double.isInfinite(distanceRemaining) || distanceRemaining < 0) {

			distanceRemaining = 0.0;
		}

		double totalDistance = distanceRemaining;

		/*
		 * Add distances from the next stop until the target stop.
		 */
		for (RouteStop rs : routeStops) {

			if (rs == null) {
				continue;
			}

			int order = rs.getStopOrder();

			if (order > nextStop.getStopOrder() && order <= targetStopOrder) {

				double distance = rs.getDistanceFromPrevious();

				if (!Double.isNaN(distance) && !Double.isInfinite(distance) && distance >= 0) {

					totalDistance += distance;
				}
			}
		}

		return roundDistance(totalDistance);
	}

	/*
	 * Calculate distance between two route stops.
	 */
	public static double calculateDistanceBetweenStops(List<RouteStop> routeStops, int sourceStopOrder,
			int destinationStopOrder) {

		if (routeStops == null || routeStops.isEmpty() || sourceStopOrder >= destinationStopOrder) {

			return 0.0;
		}

		double totalDistance = 0.0;

		for (RouteStop rs : routeStops) {

			if (rs == null) {
				continue;
			}

			int order = rs.getStopOrder();

			if (order > sourceStopOrder && order <= destinationStopOrder) {

				double distance = rs.getDistanceFromPrevious();

				if (!Double.isNaN(distance) && !Double.isInfinite(distance) && distance >= 0) {

					totalDistance += distance;
				}
			}
		}

		return roundDistance(totalDistance);
	}

	/*
	 * Calculate arrival time from a timestamp.
	 */
	public static String calculateArrivalTime(String lastUpdatedTimestamp, long etaMinutes) {

		if (lastUpdatedTimestamp == null || lastUpdatedTimestamp.trim().isEmpty()) {

			return null;
		}

		String cleanTimestamp = lastUpdatedTimestamp.trim();

		LocalTime baseTime;

		try {

			baseTime = LocalTime.parse(cleanTimestamp, AM_PM_FORMATTER);

		} catch (Exception ignored) {

			try {

				if (cleanTimestamp.contains("-")) {

					baseTime = LocalDateTime.parse(cleanTimestamp, FLEXIBLE_TIMESTAMP_FORMATTER).toLocalTime();

				} else {

					if (cleanTimestamp.contains(".")) {

						cleanTimestamp = cleanTimestamp.substring(0, cleanTimestamp.indexOf("."));
					}

					try {

						baseTime = LocalTime.parse(cleanTimestamp);

					} catch (Exception ex) {

						baseTime = LocalTime.parse(cleanTimestamp, TWENTY_FOUR_HOUR_FORMATTER);
					}
				}

			} catch (Exception ex) {

				return null;
			}
		}

		return baseTime.plusMinutes(Math.max(0, etaMinutes)).format(AM_PM_FORMATTER);
	}

	/*
	 * Add minutes to a time string.
	 */
	public static String addMinutesToTime(String timeStr, long minutesToAdd) {

		if (timeStr == null || timeStr.trim().isEmpty()) {

			return null;
		}

		LocalTime parsed;

		try {

			parsed = parseTimeString(timeStr);

		} catch (Exception ex) {

			return null;
		}

		return parsed.plusMinutes(Math.max(0, minutesToAdd)).format(AM_PM_FORMATTER);
	}

	/*
	 * Parse supported time formats.
	 */
	public static LocalTime parseTimeString(String timeStr) {

		if (timeStr == null || timeStr.trim().isEmpty()) {

			throw new IllegalArgumentException("Time cannot be null or blank");
		}

		String clean = timeStr.trim();

		try {

			return LocalTime.parse(clean, AM_PM_FORMATTER);

		} catch (Exception ignored) {
		}

		try {

			return LocalTime.parse(clean, TWENTY_FOUR_HOUR_FORMATTER);

		} catch (Exception ignored) {
		}

		try {

			return LocalTime.parse(clean);

		} catch (Exception ignored) {
		}

		/*
		 * Handle timestamp values such as: 2026-08-26T17:30:00
		 */
		try {

			if (clean.contains("-")) {

				return LocalDateTime.parse(clean, FLEXIBLE_TIMESTAMP_FORMATTER).toLocalTime();
			}

		} catch (Exception ignored) {
		}

		throw new IllegalArgumentException("Invalid time format: " + timeStr);
	}
}