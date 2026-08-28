package dto;

import java.util.List;

public class ETAResponse {

	private String busId;
	private String busNumber;
	private String status;
	private double speed;
	private String currentStop;
	private String nextStop;
	private double distanceToNextStop;
	private String boardingStop;
	private String destinationStop;
	private double remainingDistanceToBoardingStop;
	private long etaToBoardingStop;
	private String busArrivalTimeAtBoardingStop;
	private double remainingDistanceToDestination;
	private long etaToDestinationStop;
	private String busArrivalTimeAtDestinationStop;
	private String startingFrom;
	private String departureTime;
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

	public String getDestinationStop() {
		return destinationStop;
	}

	public void setDestinationStop(String destinationStop) {
		this.destinationStop = destinationStop;
	}

	public double getRemainingDistanceToBoardingStop() {
		return remainingDistanceToBoardingStop;
	}

	public void setRemainingDistanceToBoardingStop(double remainingDistanceToBoardingStop) {
		this.remainingDistanceToBoardingStop = remainingDistanceToBoardingStop;
	}

	public long getEtaToBoardingStop() {
		return etaToBoardingStop;
	}

	public void setEtaToBoardingStop(long etaToBoardingStop) {
		this.etaToBoardingStop = etaToBoardingStop;
	}

	public String getBusArrivalTimeAtBoardingStop() {
		return busArrivalTimeAtBoardingStop;
	}

	public void setBusArrivalTimeAtBoardingStop(String busArrivalTimeAtBoardingStop) {
		this.busArrivalTimeAtBoardingStop = busArrivalTimeAtBoardingStop;
	}

	public double getRemainingDistanceToDestination() {
		return remainingDistanceToDestination;
	}

	public void setRemainingDistanceToDestination(double remainingDistanceToDestination) {
		this.remainingDistanceToDestination = remainingDistanceToDestination;
	}

	public long getEtaToDestinationStop() {
		return etaToDestinationStop;
	}

	public void setEtaToDestinationStop(long etaToDestinationStop) {
		this.etaToDestinationStop = etaToDestinationStop;
	}

	public String getBusArrivalTimeAtDestinationStop() {
		return busArrivalTimeAtDestinationStop;
	}

	public void setBusArrivalTimeAtDestinationStop(String busArrivalTimeAtDestinationStop) {
		this.busArrivalTimeAtDestinationStop = busArrivalTimeAtDestinationStop;
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