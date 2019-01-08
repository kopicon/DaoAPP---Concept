package dao.daoapplication.Entities;

public class CarrierToday {

    private String workerNumber;
    private String name;
    private String routePackage;
    private double lat;
    private double lng;

    public CarrierToday(){

    }

    public CarrierToday(String workerNumber, String name, String routePackage, double lat, double lng){
        this.workerNumber = workerNumber;
        this.name = name;
        this.routePackage = routePackage;
        this.lat = lat;
        this.lng = lng;
    }
    public String getWorkerNumber() {
        return workerNumber;
    }

    public void setWorkerNumber(String workerNumber) {
        this.workerNumber = workerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoutePackage() {
        return routePackage;
    }

    public void setRoutePackage(String routePackage) {
        this.routePackage = routePackage;
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
