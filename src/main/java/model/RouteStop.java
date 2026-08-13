package model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class RouteStop {

    // =========================================================
    // FIELDS
    // =========================================================

    @BsonId
    private String id;

    @BsonProperty("routeId")
    private String routeId;

    @BsonProperty("stopId")
    private String stopId;

    @BsonProperty("stopName")
    private String stopName;

    @BsonProperty("stopOrder")
    private int stopOrder;

    @BsonProperty("distanceFromPrevious")
    private double distanceFromPrevious;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public RouteStop() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RouteStop(
            String routeId,
            String stopId,
            String stopName,
            int stopOrder,
            double distanceFromPrevious) {

        this.routeId = routeId;
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopOrder = stopOrder;
        this.distanceFromPrevious = distanceFromPrevious;

        generateId();
    }


    // =========================================================
    // GENERATE ID
    // =========================================================

    public void generateId() {

        if (routeId != null && stopId != null) {
            this.id = routeId + "_" + stopId;
        }
    }


    // =========================================================
    // GET ID
    // =========================================================

    public String getId() {
        return id;
    }


    // =========================================================
    // SET ID
    // =========================================================

    public void setId(String id) {
        this.id = id;
    }


    // =========================================================
    // GET ROUTE ID
    // =========================================================

    public String getRouteId() {
        return routeId;
    }


    // =========================================================
    // SET ROUTE ID
    // =========================================================

    public void setRouteId(String routeId) {

        this.routeId = routeId;

        generateId();
    }


    // =========================================================
    // GET STOP ID
    // =========================================================

    public String getStopId() {
        return stopId;
    }


    // =========================================================
    // SET STOP ID
    // =========================================================

    public void setStopId(String stopId) {

        this.stopId = stopId;

        generateId();
    }


    // =========================================================
    // GET STOP NAME
    // =========================================================

    public String getStopName() {
        return stopName;
    }


    // =========================================================
    // SET STOP NAME
    // =========================================================

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }


    // =========================================================
    // GET STOP ORDER
    // =========================================================

    public int getStopOrder() {
        return stopOrder;
    }


    // =========================================================
    // SET STOP ORDER
    // =========================================================

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }


    // =========================================================
    // GET DISTANCE FROM PREVIOUS
    // =========================================================

    public double getDistanceFromPrevious() {
        return distanceFromPrevious;
    }


    // =========================================================
    // SET DISTANCE FROM PREVIOUS
    // =========================================================

    public void setDistanceFromPrevious(
            double distanceFromPrevious) {

        this.distanceFromPrevious = distanceFromPrevious;
    }


    // =========================================================
    // TOSTRING
    // =========================================================

    @Override
    public String toString() {

        return "RouteStop{" +
                "id='" + id + '\'' +
                ", routeId='" + routeId + '\'' +
                ", stopId='" + stopId + '\'' +
                ", stopName='" + stopName + '\'' +
                ", stopOrder=" + stopOrder +
                ", distanceFromPrevious=" +
                distanceFromPrevious +
                '}';
    }
}