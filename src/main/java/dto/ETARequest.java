package dto;

public class ETARequest {

    private String boardingStop;
    private String destinationStop;
    private String travelTime;

    public ETARequest() {}

    public ETARequest(String boardingStop, String destinationStop, String travelTime) {
        this.boardingStop = boardingStop;
        this.destinationStop = destinationStop;
        this.travelTime = travelTime;
    }

    public String getBoardingStop() { return boardingStop; }
    public void setBoardingStop(String boardingStop) { this.boardingStop = boardingStop; }

    public String getDestinationStop() { return destinationStop; }
    public void setDestinationStop(String destinationStop) { this.destinationStop = destinationStop; }

    public String getTravelTime() { return travelTime; }
    public void setTravelTime(String travelTime) { this.travelTime = travelTime; }
}
