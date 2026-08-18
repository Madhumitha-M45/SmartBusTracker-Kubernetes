package dto;

import java.util.List;

public class ETAResponse {

    private String busId;
    private String busNumber;
    private String status;
    private String statusBanner;

    private double speed;

    private String currentStop;
    private String nextStop;

    private double distanceToNextStop;

    private String boardingStop;
    private double remainingDistanceToBoardingStop;
    private String boardingEta;
    private String boardingArrival;

    private String destinationStop;
    private double destinationRemainingDistance;
    private String destinationEta;
    private String destinationArrival;

    private String startingFrom;
    private String departureTime;

    private double latitude;
    private double longitude;

    private String lastUpdated;

    private List<RouteStopDetailDTO> routeStops;

    public ETAResponse() {
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusBanner() {
        return statusBanner;
    }

    public void setStatusBanner(String statusBanner) {
        this.statusBanner = statusBanner;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getCurrentStop() {
        return currentStop;
    }

    public void setCurrentStop(String currentStop) {
        this.currentStop = currentStop;
    }

    public String getNextStop() {
        return nextStop;
    }

    public void setNextStop(String nextStop) {
        this.nextStop = nextStop;
    }

    public double getDistanceToNextStop() {
        return distanceToNextStop;
    }

    public void setDistanceToNextStop(double distanceToNextStop) {
        this.distanceToNextStop = distanceToNextStop;
    }

    public String getBoardingStop() {
        return boardingStop;
    }

    public void setBoardingStop(String boardingStop) {
        this.boardingStop = boardingStop;
    }

    public double getRemainingDistanceToBoardingStop() {
        return remainingDistanceToBoardingStop;
    }

    public void setRemainingDistanceToBoardingStop(
            double remainingDistanceToBoardingStop) {

        this.remainingDistanceToBoardingStop =
                remainingDistanceToBoardingStop;
    }

    public String getBoardingEta() {
        return boardingEta;
    }

    public void setBoardingEta(String boardingEta) {
        this.boardingEta = boardingEta;
    }

    public String getBoardingArrival() {
        return boardingArrival;
    }

    public void setBoardingArrival(String boardingArrival) {
        this.boardingArrival = boardingArrival;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(String destinationStop) {
        this.destinationStop = destinationStop;
    }

    public double getDestinationRemainingDistance() {
        return destinationRemainingDistance;
    }

    public void setDestinationRemainingDistance(
            double destinationRemainingDistance) {

        this.destinationRemainingDistance =
                destinationRemainingDistance;
    }

    public String getDestinationEta() {
        return destinationEta;
    }

    public void setDestinationEta(String destinationEta) {
        this.destinationEta = destinationEta;
    }

    public String getDestinationArrival() {
        return destinationArrival;
    }

    public void setDestinationArrival(String destinationArrival) {
        this.destinationArrival = destinationArrival;
    }

    public String getStartingFrom() {
        return startingFrom;
    }

    public void setStartingFrom(String startingFrom) {
        this.startingFrom = startingFrom;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<RouteStopDetailDTO> getRouteStops() {
        return routeStops;
    }

    public void setRouteStops(List<RouteStopDetailDTO> routeStops) {
        this.routeStops = routeStops;
    }
}
