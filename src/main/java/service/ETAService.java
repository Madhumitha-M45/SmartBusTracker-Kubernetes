package service;

import java.util.ArrayList;
import java.util.List;

import builder.ETAResponseBuilder;
import dto.ETARequest;
import dto.ETAResponse;
import dto.RouteStopDetailDTO;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Stop;

public class ETAService {

    private final BusService busService;
    private final ScheduleService scheduleService;
    private final ETAResponseBuilder responseBuilder;

    public ETAService() {
        busService = new BusService();
        scheduleService = new ScheduleService();
        responseBuilder = new ETAResponseBuilder();
    }

    public List<String> searchStops(String query) {
        return busService.searchStops(query);
    }

    public List<ETAResponse> searchBus(ETARequest request) {
        return calculateETAForAllBuses(request);
    }

    public ETAResponse calculateETA(ETARequest request) {
        List<ETAResponse> buses = calculateETAForAllBuses(request);

        if (buses.isEmpty()) {
            return null;
        }

        ETAResponse bestBus = buses.get(0);

        for (ETAResponse bus : buses) {
            if (bus.getBoardingEta().compareTo(bestBus.getBoardingEta()) < 0) {
                bestBus = bus;
            }
        }

        return bestBus;
    }

    public List<ETAResponse> calculateETAForAllBuses(ETARequest request) {
        List<ETAResponse> responses = new ArrayList<>();

        Stop source = busService.findStop(request.getBoardingStop());
        Stop destination = busService.findStop(request.getDestinationStop());

        if (source == null || destination == null) {
            return responses;
        }

        List<Route> routes = busService.findRoutes(
                source.getStopId(),
                destination.getStopId());

        if (routes == null || routes.isEmpty()) {
            return responses;
        }

        for (Route route : routes) {

            List<RouteStop> routeStops =
                    busService.getRouteStops(route.getRouteId());

            RouteStop sourceStop =
                    busService.findRouteStop(
                            routeStops,
                            source.getStopId());

            RouteStop destinationStop =
                    busService.findRouteStop(
                            routeStops,
                            destination.getStopId());

            if (sourceStop == null || destinationStop == null) {
                continue;
            }

            List<BusLocation> buses =
                    busService.getValidBuses(
                            route,
                            routeStops,
                            sourceStop.getStopOrder());

            for (BusLocation bus : buses) {
                responses.add(
                        buildETAResponse(
                                route,
                                routeStops,
                                sourceStop,
                                destinationStop,
                                bus));
            }

            List<ETAResponse> scheduledResponses =
                    scheduleService.getScheduledResponses(
                            request,
                            route,
                            routeStops,
                            sourceStop,
                            destinationStop);

            if (scheduledResponses != null) {
                responses.addAll(scheduledResponses);
            }
        }

        return responses;
    }

    public ETAResponse calculateETAForBusAndStop(
            String busId,
            String boardingStopId) {

        BusLocation bus =
                busService.findActiveBus(busId);

        Stop boardingStop =
                busService.findStopByIdOrName(boardingStopId);

        Route route =
                busService.findRouteById(bus.getRouteId());

        List<RouteStop> routeStops =
                busService.getRouteStops(route.getRouteId());

        RouteStop sourceStop =
                busService.findRouteStop(
                        routeStops,
                        boardingStop.getStopId());

        return buildNotificationResponse(
                route,
                routeStops,
                sourceStop,
                boardingStop,
                bus);
    }

    public List<String> getActiveBusIds() {
        return busService.getActiveBusIds();
    }

    private ETAResponse buildETAResponse(
            Route route,
            List<RouteStop> routeStops,
            RouteStop source,
            RouteStop destination,
            BusLocation bus) {

        double speed = bus.getSpeed();

        double distanceToSource =
                ETACalculator.calculateDistanceToStop(
                        routeStops,
                        bus,
                        source.getStopOrder());

        double distanceToDestination =
                ETACalculator.calculateDistanceBetweenStops(
                        routeStops,
                        source.getStopOrder(),
                        destination.getStopOrder());

        long sourceMinutes =
                ETACalculator.calculateETAMinutes(
                        distanceToSource,
                        speed);

        long destinationMinutes =
                ETACalculator.calculateETAMinutes(
                        distanceToDestination,
                        speed);

        String timestamp = bus.getLastUpdated();

        String boardingArrivalTime =
                ETACalculator.calculateArrivalTime(
                        timestamp,
                        sourceMinutes);

        String destinationArrivalTime =
                ETACalculator.calculateArrivalTime(
                        timestamp,
                        sourceMinutes + destinationMinutes);

        List<RouteStopDetailDTO> timeline =
                new ArrayList<>();

        for (RouteStop routeStop : routeStops) {

            boolean currentStop =
                    routeStop.getStopId()
                            .equalsIgnoreCase(
                                    bus.getCurrentStopId());

            timeline.add(
                    responseBuilder.buildRouteStopDetail(
                            routeStop,
                            "N/A",
                            0.0,
                            currentStop));
        }

        return responseBuilder.buildRunningBusResponse(
                bus,
                route,
                source,
                destination,
                speed,
                distanceToSource,
                distanceToDestination,
                sourceMinutes,
                sourceMinutes + destinationMinutes,
                boardingArrivalTime,
                destinationArrivalTime,
                timeline);
    }

    private ETAResponse buildNotificationResponse(
            Route route,
            List<RouteStop> routeStops,
            RouteStop source,
            Stop boardingStop,
            BusLocation bus) {

        double speed = bus.getSpeed();

        double distance =
                ETACalculator.calculateDistanceToStop(
                        routeStops,
                        bus,
                        source.getStopOrder());

        long minutes =
                ETACalculator.calculateETAMinutes(
                        distance,
                        speed);

        String arrivalTime =
                ETACalculator.calculateArrivalTime(
                        bus.getLastUpdated(),
                        minutes);

        List<RouteStopDetailDTO> timeline =
                new ArrayList<>();

        for (RouteStop routeStop : routeStops) {

            boolean currentStop =
                    routeStop.getStopId()
                            .equalsIgnoreCase(
                                    bus.getCurrentStopId());

            timeline.add(
                    responseBuilder.buildRouteStopDetail(
                            routeStop,
                            "N/A",
                            0.0,
                            currentStop));
        }

        return responseBuilder.buildNotificationResponse(
                bus,
                route,
                boardingStop,
                distance,
                minutes,
                arrivalTime,
                timeline);
    }
}