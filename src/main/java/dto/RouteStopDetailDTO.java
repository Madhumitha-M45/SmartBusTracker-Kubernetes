package dto;

public class RouteStopDetailDTO {

    private String stopId;
    private String stopName;
    private int stopOrder;
    private String expectedArrivalText;
    private String distanceAwayText;
    private boolean isCurrentStop;

    public RouteStopDetailDTO() {}

    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public int getStopOrder() { return stopOrder; }
    public void setStopOrder(int stopOrder) { this.stopOrder = stopOrder; }

    public String getExpectedArrivalText() { return expectedArrivalText; }
    public void setExpectedArrivalText(String expectedArrivalText) { this.expectedArrivalText = expectedArrivalText; }

    public String getDistanceAwayText() { return distanceAwayText; }
    public void setDistanceAwayText(String distanceAwayText) { this.distanceAwayText = distanceAwayText; }

    public boolean isCurrentStop() { return isCurrentStop; }
    public void setCurrentStop(boolean currentStop) { isCurrentStop = currentStop; }
}
