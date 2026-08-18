package model;

public class DeviceToken {
    private String deviceToken;
    private String busId;
    private String boardingStop;
    private String destinationStop;
    private boolean active;
    private boolean completed;

    public DeviceToken() {}

    public DeviceToken(String deviceToken, String busId, String boardingStop, String destinationStop, boolean active, boolean completed) {
        this.deviceToken = deviceToken;
        this.busId = busId;
        this.boardingStop = boardingStop;
        this.destinationStop = destinationStop;
        this.active = active;
        this.completed = completed;
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

    public String getBoardingStopId() {
        return boardingStop;
    }

    public void setBoardingStopId(String boardingStopId) {
        this.boardingStop = boardingStopId;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(String destinationStop) {
        this.destinationStop = destinationStop;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
