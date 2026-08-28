package dto;

public class DeviceTokenRequest {
    private String deviceToken;
    private String busId;
    private String boardingStop;
    private String destinationStop;
    public DeviceTokenRequest() {
    }
    public String getDeviceToken() {
        return deviceToken;
    }
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
    public String getBusId() {
        return busId;
    }
    public void setBusId(String busId) {
        this.busId = busId;
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
}