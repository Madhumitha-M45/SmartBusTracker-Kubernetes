package service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class ETACalculator {

    private static final double DEFAULT_SPEED = 30.0;

    private static final DateTimeFormatter AM_PM_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private static final DateTimeFormatter TWENTY_FOUR_HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH);

    private static final DateTimeFormatter FLEXIBLE_TIMESTAMP_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-M-d")
                    .optionalStart()
                    .appendPattern("'T'")
                    .optionalEnd()
                    .optionalStart()
                    .appendPattern(" ")
                    .optionalEnd()
                    .appendPattern("H:m")
                    .optionalStart()
                    .appendPattern(":s")
                    .optionalEnd()
                    .toFormatter(Locale.ENGLISH);

    private ETACalculator() {
    }

    public static double roundDistance(double distance) {
        return Math.round(distance * 10.0) / 10.0;
    }

    public static long calculateETAMinutes(
            double distanceKm,
            double speedKmPerHour) {

        if (distanceKm <= 0) {
            return 0;
        }

        double effectiveSpeed = speedKmPerHour;

        if (effectiveSpeed <= 0) {
            effectiveSpeed = DEFAULT_SPEED;
        }

        double timeInMinutes =
                (distanceKm / effectiveSpeed) * 60.0;

        long minutes =
                Math.round(timeInMinutes);

        return Math.max(1, minutes);
    }

    public static String formatETAString(long minutes) {
        if (minutes <= 0) {
            return "Arrived";
        }

        if (minutes < 60) {
            return minutes + " mins";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (remainingMinutes == 0) {
            return hours
                    + (hours == 1 ? " hr" : " hrs");
        }

        return hours
                + (hours == 1 ? " hr " : " hrs ")
                + remainingMinutes
                + " mins";
    }

    public static String format12HourTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return "12:00 PM";
        }

        String cleanTime = timeStr.trim();

        try {
            try {
                LocalTime time =
                        LocalTime.parse(cleanTime, AM_PM_FORMATTER);
                return time.format(AM_PM_FORMATTER);
            } catch (Exception ignored) {
            }

            if (cleanTime.contains("-")) {
                LocalDateTime dateTime =
                        LocalDateTime.parse(cleanTime, FLEXIBLE_TIMESTAMP_FORMATTER);
                return dateTime.toLocalTime().format(AM_PM_FORMATTER);
            }

            if (cleanTime.contains(".")) {
                cleanTime =
                        cleanTime.substring(0, cleanTime.indexOf("."));
            }

            try {
                LocalTime time = LocalTime.parse(cleanTime);
                return time.format(AM_PM_FORMATTER);
            } catch (Exception ignored) {
            }

            LocalTime time =
                    LocalTime.parse(cleanTime, TWENTY_FOUR_HOUR_FORMATTER);
            return time.format(AM_PM_FORMATTER);

        } catch (Exception e) {
            return timeStr;
        }
    }

    public static String calculateArrivalTime(
            String lastUpdatedTimestamp,
            long etaMinutes) {

        if (lastUpdatedTimestamp == null ||
                lastUpdatedTimestamp.trim().isEmpty()) {
            return "12:00 PM";
        }

        String cleanTimestamp = lastUpdatedTimestamp.trim();

        try {
            LocalTime baseTime;

            try {
                baseTime =
                        LocalTime.parse(cleanTimestamp, AM_PM_FORMATTER);
            } catch (Exception ignored) {
                if (cleanTimestamp.contains("-")) {
                    LocalDateTime dateTime =
                            LocalDateTime.parse(
                                    cleanTimestamp,
                                    FLEXIBLE_TIMESTAMP_FORMATTER
                            );
                    baseTime = dateTime.toLocalTime();
                } else {
                    if (cleanTimestamp.contains(".")) {
                        cleanTimestamp =
                                cleanTimestamp.substring(
                                        0,
                                        cleanTimestamp.indexOf(".")
                                );
                    }

                    try {
                        baseTime = LocalTime.parse(cleanTimestamp);
                    } catch (Exception ex) {
                        baseTime =
                                LocalTime.parse(
                                        cleanTimestamp,
                                        TWENTY_FOUR_HOUR_FORMATTER
                                );
                    }
                }
            }

            long safeMinutes = Math.max(0, etaMinutes);

            LocalTime arrivalTime =
                    baseTime.plusMinutes(safeMinutes);

            return arrivalTime.format(AM_PM_FORMATTER);

        } catch (Exception e) {
            return "12:00 PM";
        }
    }
}
