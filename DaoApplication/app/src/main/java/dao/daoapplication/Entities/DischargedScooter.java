package dao.daoapplication.Entities;

public class DischargedScooter {
    private String routePackage;
    private String carrierName;
    private boolean finished;
    private boolean notified;
    private double lat;
    private double lng;


    public DischargedScooter() {

    }

    public DischargedScooter(String routePackage, String carrierName, boolean finished,boolean notified , double lat, double lng) {
        this.routePackage = routePackage;
        this.carrierName = carrierName;
        this.finished = finished;
        this.notified = notified;
        this.lat = lat;
        this.lng = lng;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getRoutePackage() {
        return routePackage;
    }

    public void setRoutePackage(String routePackage) {
        this.routePackage = routePackage;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
