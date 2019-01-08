package dao.daoapplication.Entities;

import java.util.ArrayList;

public class Addresses {

    private String postCode;
    private String city;
    private String address;
    private String massege;
    private String route;
    private ArrayList<String> newspapers;
    private double lat;
    private double lng;
    private int id;

    public Addresses(){

    }

    public Addresses(String postCode, String city, String address, ArrayList<String> newspapers, double lat, double lng, String massege, int id) {
        this.postCode = postCode;
        this.city = city;
        this.address = address;
        this.newspapers = newspapers;
        this.massege = massege;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
    }


    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getMassege() {
        return massege;
    }

    public void setMassege(String massege) {
        this.massege = massege;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostCode() {

        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
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



}