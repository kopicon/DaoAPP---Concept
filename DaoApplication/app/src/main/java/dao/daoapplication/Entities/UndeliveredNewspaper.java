package dao.daoapplication.Entities;

import java.util.ArrayList;

public class UndeliveredNewspaper {

    private String city;
    private String address;
    private String route;
    private ArrayList<String> newspapers;
    private double lat;
    private double lng;
    private boolean delivered;
    private boolean notified;

    public UndeliveredNewspaper(){

    }

    public UndeliveredNewspaper(String city, String address,ArrayList<String> newspapers,String route,boolean delivered,boolean notified, double lat, double lng) {
        this.city = city;
        this.address = address;
        this.newspapers = newspapers;
        this.route = route;
        this.delivered = delivered;
        this.notified = notified;
        this.lat = lat;
        this.lng = lng;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public ArrayList<String> getNewspapers() {
        return newspapers;
    }

    public void setNewspapers(ArrayList<String> newspapers) {
        this.newspapers = newspapers;
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

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
