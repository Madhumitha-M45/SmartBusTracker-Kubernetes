package model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
public class Bus {
    @BsonId
    private String busId;
    @BsonProperty("busNumber")
    private String busNumber;
    @BsonProperty("busName")
    private String busName;
    @BsonProperty("busType")
    private String busType;
    @BsonProperty("status")
    private String status;
    @BsonProperty("availableFrom")
    private String availableFrom;
    public Bus() {
    }
    public String getBusId() { 
    	return busId; }
    public void setBusId(String busId) { 
    	this.busId = busId; }
    public String getBusNumber() { 
    	return busNumber; }
    public void setBusNumber(String busNumber) { 
    	this.busNumber = busNumber; }
    public String getBusName() { 
    	return busName; }
    public void setBusName(String busName) { 
    	this.busName = busName; }
    public String getBusType() { 
    	return busType; }
    public void setBusType(String busType) { 
    	this.busType = busType; }
    public String getStatus() { 
    	return status; }
    public void setStatus(String status) { 
    	this.status = status; }
    public String getAvailableFrom() { 
    	return availableFrom; }
    public void setAvailableFrom(String availableFrom) { 
    	this.availableFrom = availableFrom; }
}
