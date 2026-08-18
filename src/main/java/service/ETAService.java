package service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.ETARequest;
import dto.ETAResponse;
import dto.RouteStopDetailDTO;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Stop;
import repository.BusLocationRepository;
import repository.RouteRepository;
import repository.RouteStopRepository;
import repository.StopRepository;

public class ETAService {

    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final BusLocationRepository busLocationRepository;

    public ETAService() {
        this.stopRepository = new StopRepository();
        this.routeRepository = new RouteRepository();
        this.routeStopRepository = new RouteStopRepository();
        this.busLocationRepository = new BusLocationRepository();
    }

    public List<String> searchStops(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchText = query.trim().toLowerCase();

        return stopRepository
                .getAllStops()
                .stream()
                .map(Stop::getStopName)
                .filter(name -> name != null)
                .filter(name -> name.toLowerCase().contains(searchText))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ETAResponse> searchBus(ETARequest request) {
        return calculateETAForAllBuses(request);
    }

    public ETAResponse calculateETA(ETARequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Stop boardingStop = findStopByName(request.getBoardingStop());
        Stop destinationStop = findStopByName(request.getDestinationStop());

        if (boardingStop == null || destinationStop == null) {
            throw new IllegalArgumentException(
                    "Boarding or Destination stop not found in system.");
        }

        Route route =
                findMatchingRoute(
                        boardingStop.getStopId(),
                        destinationStop.getStopId());

        if (route == null) {
            throw new IllegalStateException(
                    "No route connects "
                            + request.getBoardingStop()
                            + " to "
                            + request.getDestinationStop());
        }

        List<RouteStop> routeStops =
                routeStopRepository.getRouteStopsByRouteId(
                        route.getRouteId());

        if (routeStops == null || routeStops.isEmpty()) {
            throw new IllegalStateException("No route stops found.");
        }

        routeStops.sort(Comparator.comparingInt(RouteStop::getStopOrder));

        RouteStop boardingRouteStop =
                findRouteStop(routeStops, boardingStop.getStopId());

        RouteStop destinationRouteStop =
                findRouteStop(routeStops, destinationStop.getStopId());

        if (boardingRouteStop == null || destinationRouteStop == null) {
            throw new IllegalStateException(
                    "Boarding or destination stop is not part of the route.");
        }

        List<BusLocation> validBuses =
                getValidBuses(
                        route,
                        routeStops,
                        boardingRouteStop.getStopOrder());

        BusLocation selectedBus =
                validBuses
                        .stream()
                        .min(Comparator.comparingDouble(
                                bus -> calculateSegmentDistanceToStop(
                                        routeStops,
                                        bus,
                                        boardingRouteStop.getStopOrder())))
                        .orElse(null);

        return buildETAResponse(
                request,
                route,
                routeStops,
                boardingRouteStop,
                destinationRouteStop,
                selectedBus);
    }

    public List<ETAResponse> calculateETAForAllBuses(
            ETARequest request) {

        List<ETAResponse> responses = new ArrayList<>();

        if (request == null) {
            return responses;
        }

        Stop boardingStop = findStopByName(request.getBoardingStop());
        Stop destinationStop = findStopByName(request.getDestinationStop());

        if (boardingStop == null || destinationStop == null) {
            return responses;
        }

        Route route =
                findMatchingRoute(
                        boardingStop.getStopId(),
                        destinationStop.getStopId());

        if (route == null) {
            return responses;
        }

        List<RouteStop> routeStops =
                routeStopRepository.getRouteStopsByRouteId(
                        route.getRouteId());

        if (routeStops == null || routeStops.isEmpty()) {
            return responses;
        }

        routeStops.sort(Comparator.comparingInt(RouteStop::getStopOrder));

        RouteStop boardingRouteStop =
                findRouteStop(routeStops, boardingStop.getStopId());

        RouteStop destinationRouteStop =
                findRouteStop(routeStops, destinationStop.getStopId());

        if (boardingRouteStop == null || destinationRouteStop == null) {
            return responses;
        }

        List<BusLocation> validBuses =
                getValidBuses(
                        route,
                        routeStops,
                        boardingRouteStop.getStopOrder());

        for (BusLocation bus : validBuses) {
            responses.add(
                    buildETAResponse(
                            request,
                            route,
                            routeStops,
                            boardingRouteStop,
                            destinationRouteStop,
                            bus));
        }

        return responses;
    }

    public ETAResponse calculateETAForBusAndStop(
            String busId,
            String boardingStopId) {

        System.out.println("[ETA-NOTIFICATION] Starting ETA calculation");
        System.out.println("[ETA-NOTIFICATION] Bus ID: " + busId);
        System.out.println("[ETA-NOTIFICATION] Boarding Stop: " + boardingStopId);

        if (busId == null || busId.trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] FAILED: Bus ID is empty");
            return null;
        }

        if (boardingStopId == null || boardingStopId.trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] FAILED: Boarding stop is empty");
            return null;
        }

        BusLocation busLocation =
                busLocationRepository
                        .getAllBusLocations()
                        .stream()
                        .filter(bus -> bus != null)
                        .filter(bus -> bus.getBusId() != null)
                        .filter(bus -> busId.trim()
                                .equalsIgnoreCase(bus.getBusId().trim()))
                        .filter(bus -> isBusActive(bus.getStatus()))
                        .findFirst()
                        .orElse(null);

        if (busLocation == null) {
            System.out.println(
                    "[ETA-NOTIFICATION] FAILED: Active bus not found | Bus: " + busId
            );
            return null;
        }

        System.out.println(
                "[ETA-NOTIFICATION] Active bus found"
                        + " | Bus: " + busLocation.getBusId()
                        + " | Status: " + busLocation.getStatus()
                        + " | Route: " + busLocation.getRouteId()
                        + " | Current Stop: " + busLocation.getCurrentStopName()
                        + " | Next Stop: " + busLocation.getNextStopName()
        );

        Stop boardingStop = findStopByIdOrName(boardingStopId);

        if (boardingStop == null) {
            System.out.println(
                    "[ETA-NOTIFICATION] FAILED: Boarding stop not found | Value: " + boardingStopId
            );
            return null;
        }

        System.out.println(
                "[ETA-NOTIFICATION] Boarding stop found"
                        + " | ID: " + boardingStop.getStopId()
                        + " | Name: " + boardingStop.getStopName()
        );

        if (busLocation.getRouteId() == null ||
                busLocation.getRouteId().trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] FAILED: Bus has no route ID");
            return null;
        }

        Route route = findRouteById(busLocation.getRouteId());

        if (route == null) {
            System.out.println(
                    "[ETA-NOTIFICATION] FAILED: Route not found | Route ID: " + busLocation.getRouteId()
            );
            return null;
        }

        System.out.println("[ETA-NOTIFICATION] Route found | Route ID: " + route.getRouteId());

        List<RouteStop> routeStops =
                routeStopRepository.getRouteStopsByRouteId(route.getRouteId());

        if (routeStops == null || routeStops.isEmpty()) {
            System.out.println(
                    "[ETA-NOTIFICATION] FAILED: No route stops found | Route ID: " + route.getRouteId()
            );
            return null;
        }

        routeStops.sort(Comparator.comparingInt(RouteStop::getStopOrder));

        RouteStop boardingRouteStop =
                findRouteStop(routeStops, boardingStop.getStopId());

        if (boardingRouteStop == null) {
            System.out.println(
                    "[ETA-NOTIFICATION] FAILED: Boarding stop is not part of this bus route"
                            + " | Stop ID: " + boardingStop.getStopId()
                            + " | Stop Name: " + boardingStop.getStopName()
            );

            System.out.println("[ETA-NOTIFICATION] Available route stops:");

            for (RouteStop stop : routeStops) {
                if (stop != null) {
                    System.out.println(
                            "    ID: " + stop.getStopId()
                                    + " | Name: " + stop.getStopName()
                                    + " | Order: " + stop.getStopOrder()
                    );
                }
            }

            return null;
        }

        System.out.println(
                "[ETA-NOTIFICATION] Boarding stop belongs to route"
                        + " | Stop: " + boardingRouteStop.getStopName()
                        + " | Order: " + boardingRouteStop.getStopOrder()
        );

        String timestamp;

        if (busLocation.getLastUpdated() != null &&
                !busLocation.getLastUpdated().trim().isEmpty()) {
            timestamp = busLocation.getLastUpdated();
        } else {
            timestamp = LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        double speed = busLocation.getSpeed();

        double distanceToBoarding =
                calculateSegmentDistanceToStop(
                        routeStops,
                        busLocation,
                        boardingRouteStop.getStopOrder());

        distanceToBoarding = ETACalculator.roundDistance(distanceToBoarding);

        long boardingEtaMinutes =
                ETACalculator.calculateETAMinutes(distanceToBoarding, speed);

        System.out.println(
                "[ETA-NOTIFICATION] ETA calculated"
                        + " | Bus: " + busLocation.getBusId()
                        + " | Boarding Stop: " + boardingRouteStop.getStopName()
                        + " | Distance: " + distanceToBoarding + " km"
                        + " | ETA: " + boardingEtaMinutes + " minutes"
        );

        ETAResponse response = new ETAResponse();

        response.setBusId(busLocation.getBusId());
        response.setBusNumber(null);
        response.setStatus(busLocation.getStatus());

        if ("WAITING".equalsIgnoreCase(busLocation.getStatus()) ||
                "AT_STOP".equalsIgnoreCase(busLocation.getStatus())) {
            response.setStatusBanner(
                    "Bus is waiting at " + busLocation.getCurrentStopName());
        } else {
            response.setStatusBanner(
                    "Bus is running towards " + busLocation.getNextStopName());
        }

        response.setCurrentStop(busLocation.getCurrentStopName());
        response.setNextStop(busLocation.getNextStopName());
        response.setSpeed(ETACalculator.roundDistance(speed));
        response.setDistanceToNextStop(
                ETACalculator.roundDistance(busLocation.getDistanceRemaining()));
        response.setBoardingStop(boardingRouteStop.getStopName());
        response.setRemainingDistanceToBoardingStop(distanceToBoarding);
        response.setBoardingEta(ETACalculator.formatETAString(boardingEtaMinutes));
        response.setBoardingArrival(
                ETACalculator.calculateArrivalTime(timestamp, boardingEtaMinutes));
        response.setStartingFrom(route.getSource());
        response.setDestinationStop(null);
        response.setDestinationRemainingDistance(0.0);
        response.setDestinationEta("N/A");
        response.setDestinationArrival("N/A");
        response.setDepartureTime("N/A");
        response.setLatitude(0.0);
        response.setLongitude(0.0);
        response.setLastUpdated(ETACalculator.format12HourTime(timestamp));
        response.setRouteStops(buildTimeline(routeStops, busLocation, speed));

        System.out.println(
                "[ETA-NOTIFICATION] SUCCESS"
                        + " | Bus: " + response.getBusId()
                        + " | Boarding Stop: " + response.getBoardingStop()
                        + " | Distance: " + response.getRemainingDistanceToBoardingStop() + " km"
                        + " | ETA: " + response.getBoardingEta()
                        + " | Arrival: " + response.getBoardingArrival()
        );

        return response;
    }

    private ETAResponse buildETAResponse(
            ETARequest request,
            Route route,
            List<RouteStop> routeStops,
            RouteStop boardingRouteStop,
            RouteStop destinationRouteStop,
            BusLocation busLocation) {

        ETAResponse response = new ETAResponse();

        String timestamp;

        if (busLocation != null &&
                busLocation.getLastUpdated() != null &&
                !busLocation.getLastUpdated().trim().isEmpty()) {
            timestamp = busLocation.getLastUpdated();
        } else if (request.getTravelTime() != null &&
                !request.getTravelTime().trim().isEmpty()) {
            timestamp = request.getTravelTime();
        } else {
            timestamp = LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        if (busLocation != null) {
            double speed = busLocation.getSpeed();

            response.setBusId(busLocation.getBusId());
            response.setBusNumber(null);
            response.setStatus(busLocation.getStatus());

            if ("WAITING".equalsIgnoreCase(busLocation.getStatus()) ||
                    "AT_STOP".equalsIgnoreCase(busLocation.getStatus())) {
                response.setStatusBanner(
                        "Bus is waiting at " + busLocation.getCurrentStopName());
            } else {
                response.setStatusBanner(
                        "Bus is running towards " + busLocation.getNextStopName());
            }

            response.setCurrentStop(busLocation.getCurrentStopName());
            response.setNextStop(busLocation.getNextStopName());
            response.setSpeed(ETACalculator.roundDistance(speed));
            response.setDistanceToNextStop(
                    ETACalculator.roundDistance(busLocation.getDistanceRemaining()));

            double distanceToBoarding =
                    calculateSegmentDistanceToStop(
                            routeStops,
                            busLocation,
                            boardingRouteStop.getStopOrder());
            distanceToBoarding = ETACalculator.roundDistance(distanceToBoarding);

            double distanceBoardingToDestination =
                    calculateDistanceBetweenStops(
                            routeStops,
                            boardingRouteStop.getStopOrder(),
                            destinationRouteStop.getStopOrder());
            distanceBoardingToDestination = ETACalculator.roundDistance(distanceBoardingToDestination);

            long boardingEtaMinutes =
                    ETACalculator.calculateETAMinutes(distanceToBoarding, speed);

            long destinationTravelMinutes =
                    ETACalculator.calculateETAMinutes(distanceBoardingToDestination, speed);

            response.setBoardingStop(boardingRouteStop.getStopName());
            response.setRemainingDistanceToBoardingStop(distanceToBoarding);
            response.setBoardingEta(ETACalculator.formatETAString(boardingEtaMinutes));
            response.setBoardingArrival(
                    ETACalculator.calculateArrivalTime(timestamp, boardingEtaMinutes));

            response.setDestinationStop(destinationRouteStop.getStopName());
            response.setDestinationRemainingDistance(distanceBoardingToDestination);
            response.setDestinationEta(ETACalculator.formatETAString(destinationTravelMinutes));

            long totalDestinationMinutes =
                    boardingEtaMinutes + destinationTravelMinutes;

            response.setDestinationArrival(
                    ETACalculator.calculateArrivalTime(timestamp, totalDestinationMinutes));

            response.setStartingFrom(route.getSource());
            response.setDepartureTime("N/A");
            response.setLatitude(0.0);
            response.setLongitude(0.0);
            response.setLastUpdated(ETACalculator.format12HourTime(timestamp));
            response.setRouteStops(buildTimeline(routeStops, busLocation, speed));

        } else {
            response.setBusId(null);
            response.setBusNumber(null);
            response.setStatus("NO_BUS");
            response.setStatusBanner("No active bus available on this route");
            response.setSpeed(0.0);
            response.setCurrentStop("N/A");
            response.setNextStop("N/A");
            response.setDistanceToNextStop(0.0);
            response.setBoardingStop(boardingRouteStop.getStopName());
            response.setRemainingDistanceToBoardingStop(0.0);
            response.setBoardingEta("No live bus");
            response.setBoardingArrival("N/A");
            response.setDestinationStop(destinationRouteStop.getStopName());

            double passengerDistance =
                    calculateDistanceBetweenStops(
                            routeStops,
                            boardingRouteStop.getStopOrder(),
                            destinationRouteStop.getStopOrder());

            response.setDestinationRemainingDistance(
                    ETACalculator.roundDistance(passengerDistance));
            response.setDestinationEta("N/A");
            response.setDestinationArrival("N/A");
            response.setStartingFrom(route.getSource());
            response.setDepartureTime("N/A");
            response.setLatitude(0.0);
            response.setLongitude(0.0);
            response.setLastUpdated(ETACalculator.format12HourTime(timestamp));
            response.setRouteStops(buildEmptyTimeline(routeStops));
        }

        return response;
    }

    private List<BusLocation> getValidBuses(
            Route route,
            List<RouteStop> routeStops,
            int boardingStopOrder) {

        return busLocationRepository
                .getAllBusLocations()
                .stream()
                .filter(loc -> loc != null
                        && loc.getRouteId() != null
                        && route.getRouteId().equalsIgnoreCase(loc.getRouteId()))
                .filter(loc -> isBusActive(loc.getStatus()))
                .filter(loc -> !hasBusPassedStop(routeStops, loc, boardingStopOrder))
                .collect(Collectors.toList());
    }

    private RouteStop findCurrentRouteStop(
            List<RouteStop> routeStops,
            BusLocation busLocation) {

        if (routeStops == null || busLocation == null) {
            return null;
        }

        if (busLocation.getCurrentStopId() != null) {
            RouteStop stopById =
                    findRouteStop(routeStops, busLocation.getCurrentStopId());
            if (stopById != null) {
                return stopById;
            }
        }

        if (busLocation.getCurrentStopName() != null) {
            String currentName = busLocation.getCurrentStopName().trim();

            return routeStops
                    .stream()
                    .filter(stop -> stop != null && stop.getStopName() != null)
                    .filter(stop -> stop.getStopName().trim()
                            .equalsIgnoreCase(currentName))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private double calculateSegmentDistanceToStop(
            List<RouteStop> routeStops,
            BusLocation busLocation,
            int targetStopOrder) {

        if (busLocation == null || routeStops == null) {
            return 0.0;
        }

        RouteStop currentStop = findCurrentRouteStop(routeStops, busLocation);

        if (currentStop == null) {
            return 0.0;
        }

        int currentOrder = currentStop.getStopOrder();

        if (targetStopOrder <= currentOrder) {
            return 0.0;
        }

        double distanceToNextStop =
                Math.max(0.0, busLocation.getDistanceRemaining());

        int nextStopOrder = currentOrder + 1;

        if (targetStopOrder == nextStopOrder) {
            return distanceToNextStop;
        }

        double totalDistance = distanceToNextStop;

        for (RouteStop routeStop : routeStops) {
            if (routeStop == null) {
                continue;
            }

            int stopOrder = routeStop.getStopOrder();

            if (stopOrder > nextStopOrder && stopOrder <= targetStopOrder) {
                totalDistance +=
                        Math.max(0.0, routeStop.getDistanceFromPrevious());
            }
        }

        return totalDistance;
    }

    private double calculateDistanceBetweenStops(
            List<RouteStop> routeStops,
            int boardingOrder,
            int destinationOrder) {

        if (routeStops == null) {
            return 0.0;
        }

        if (destinationOrder <= boardingOrder) {
            return 0.0;
        }

        double totalDistance = 0.0;

        for (RouteStop routeStop : routeStops) {
            if (routeStop == null) {
                continue;
            }

            int stopOrder = routeStop.getStopOrder();

            if (stopOrder > boardingOrder && stopOrder <= destinationOrder) {
                totalDistance +=
                        Math.max(0.0, routeStop.getDistanceFromPrevious());
            }
        }

        return totalDistance;
    }

    private boolean hasBusPassedStop(
            List<RouteStop> routeStops,
            BusLocation busLocation,
            int targetStopOrder) {

        if (busLocation == null) {
            return false;
        }

        RouteStop currentStop = findCurrentRouteStop(routeStops, busLocation);

        if (currentStop == null) {
            return false;
        }

        return currentStop.getStopOrder() > targetStopOrder;
    }

    private List<RouteStopDetailDTO> buildTimeline(
            List<RouteStop> routeStops,
            BusLocation busLocation,
            double speed) {

        List<RouteStopDetailDTO> timeline = new ArrayList<>();

        RouteStop busCurrentStop =
                findCurrentRouteStop(routeStops, busLocation);

        int currentOrder =
                busCurrentStop != null ? busCurrentStop.getStopOrder() : 0;

        for (RouteStop routeStop : routeStops) {

            RouteStopDetailDTO item = new RouteStopDetailDTO();

            item.setStopId(routeStop.getStopId());
            item.setStopName(routeStop.getStopName());
            item.setStopOrder(routeStop.getStopOrder());

            int stopOrder = routeStop.getStopOrder();
            boolean isCurrentStop = false;

            if (busCurrentStop != null) {
                if (routeStop.getStopId() != null &&
                        busCurrentStop.getStopId() != null &&
                        routeStop.getStopId()
                                .equalsIgnoreCase(busCurrentStop.getStopId())) {
                    isCurrentStop = true;
                } else if (
                        routeStop.getStopName() != null &&
                                busCurrentStop.getStopName() != null &&
                                routeStop.getStopName().trim()
                                        .equalsIgnoreCase(
                                                busCurrentStop.getStopName().trim())) {
                    isCurrentStop = true;
                }
            }

            if (isCurrentStop) {
                item.setCurrentStop(true);
                item.setExpectedArrivalText("Bus is here");
                item.setDistanceAwayText("");
            } else if (stopOrder < currentOrder) {
                item.setCurrentStop(false);
                item.setExpectedArrivalText("Passed");
                item.setDistanceAwayText("");
            } else {
                item.setCurrentStop(false);

                double segmentDistance;

                if (stopOrder == currentOrder + 1) {
                    segmentDistance =
                            Math.max(0.0, busLocation.getDistanceRemaining());
                } else {
                    segmentDistance =
                            Math.max(0.0, routeStop.getDistanceFromPrevious());
                }

                long segmentEtaMinutes =
                        ETACalculator.calculateETAMinutes(segmentDistance, speed);

                item.setDistanceAwayText(
                        ETACalculator.roundDistance(segmentDistance) + " km away");

                item.setExpectedArrivalText(
                        ETACalculator.formatETAString(segmentEtaMinutes));
            }

            timeline.add(item);
        }

        return timeline;
    }

    private List<RouteStopDetailDTO> buildEmptyTimeline(
            List<RouteStop> routeStops) {

        List<RouteStopDetailDTO> timeline = new ArrayList<>();

        for (RouteStop routeStop : routeStops) {

            RouteStopDetailDTO item = new RouteStopDetailDTO();

            item.setStopId(routeStop.getStopId());
            item.setStopName(routeStop.getStopName());
            item.setStopOrder(routeStop.getStopOrder());
            item.setCurrentStop(false);
            item.setExpectedArrivalText("N/A");

            double distanceFromPrevious =
                    Math.max(0.0, routeStop.getDistanceFromPrevious());

            item.setDistanceAwayText(
                    ETACalculator.roundDistance(distanceFromPrevious) + " km away");

            timeline.add(item);
        }

        return timeline;
    }

    private Stop findStopByName(String stopName) {
        if (stopName == null || stopName.trim().isEmpty()) {
            return null;
        }

        return stopRepository
                .getAllStops()
                .stream()
                .filter(stop -> stop != null && stop.getStopName() != null)
                .filter(stop -> stopName.trim()
                        .equalsIgnoreCase(stop.getStopName().trim()))
                .findFirst()
                .orElse(null);
    }

    private Stop findStopById(String stopId) {
        if (stopId == null || stopId.trim().isEmpty()) {
            return null;
        }

        return stopRepository
                .getAllStops()
                .stream()
                .filter(stop -> stop != null && stop.getStopId() != null)
                .filter(stop -> stopId.trim()
                        .equalsIgnoreCase(stop.getStopId().trim()))
                .findFirst()
                .orElse(null);
    }

    private Stop findStopByIdOrName(String stopValue) {
        if (stopValue == null || stopValue.trim().isEmpty()) {
            return null;
        }

        String value = stopValue.trim();

        Stop stopById = findStopById(value);

        if (stopById != null) {
            return stopById;
        }

        Stop stopByName = findStopByName(value);

        return stopByName;
    }

    private Route findMatchingRoute(
            String boardingStopId,
            String destinationStopId) {

        if (boardingStopId == null || destinationStopId == null) {
            return null;
        }

        for (Route route : routeRepository.getAllRoutes()) {

            if (route == null || route.getRouteId() == null) {
                continue;
            }

            List<RouteStop> stops =
                    routeStopRepository.getRouteStopsByRouteId(
                            route.getRouteId());

            RouteStop boardingStop = findRouteStop(stops, boardingStopId);
            RouteStop destinationStop = findRouteStop(stops, destinationStopId);

            if (boardingStop != null &&
                    destinationStop != null &&
                    boardingStop.getStopOrder() < destinationStop.getStopOrder()) {
                return route;
            }
        }

        return null;
    }

    private Route findRouteById(String routeId) {
        if (routeId == null || routeId.trim().isEmpty()) {
            return null;
        }

        return routeRepository
                .getAllRoutes()
                .stream()
                .filter(route -> route != null && route.getRouteId() != null)
                .filter(route -> routeId.trim()
                        .equalsIgnoreCase(route.getRouteId().trim()))
                .findFirst()
                .orElse(null);
    }

    private RouteStop findRouteStop(
            List<RouteStop> stops,
            String stopId) {

        if (stops == null || stopId == null) {
            return null;
        }

        return stops
                .stream()
                .filter(stop -> stop != null && stop.getStopId() != null)
                .filter(stop -> stopId.trim()
                        .equalsIgnoreCase(stop.getStopId().trim()))
                .findFirst()
                .orElse(null);
    }

    private boolean isBusActive(String status) {
        if (status == null) {
            return false;
        }

        String value = status.trim().toUpperCase();

        return "RUNNING".equals(value)
                || "WAITING".equals(value)
                || "AT_STOP".equals(value)
                || "ACTIVE".equals(value);
    }

    public List<String> getActiveBusIds() {
        return busLocationRepository
                .getAllBusLocations()
                .stream()
                .filter(bus -> bus != null)
                .filter(bus -> bus.getBusId() != null)
                .filter(bus -> isBusActive(bus.getStatus()))
                .map(BusLocation::getBusId)
                .distinct()
                .collect(Collectors.toList());
    }
}
