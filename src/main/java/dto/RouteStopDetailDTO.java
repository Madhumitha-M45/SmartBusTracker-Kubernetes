package dto;

public class RouteStopDetailDTO {

    private String stopName;
    private double remainingDistance;
    private long eta;

    public RouteStopDetailDTO() {
    }

    public RouteStopDetailDTO(String stopName, double remainingDistance, long eta) {
        this.stopName = stopName;
        this.remainingDistance = remainingDistance;
        this.eta = eta;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public double getRemainingDistance() {
        return remainingDistance;
    }

    public void setRemainingDistance(double remainingDistance) {
        this.remainingDistance = remainingDistance;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }
}