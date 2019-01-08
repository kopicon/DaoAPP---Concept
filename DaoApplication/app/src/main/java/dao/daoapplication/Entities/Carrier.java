package dao.daoapplication.Entities;

public class Carrier {

    private String workerNumber;
    private String name;
    private String email;
    private String phone;
    private String routePackage;
    private String city;
    private String address;
    private String pin;
    private double lat;
    private double lng;

    public Carrier(){

    }

    public Carrier(String workerNumber, String name, String email, String phone, String city, String address, String routePackage, String pin){
        this.workerNumber = workerNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.routePackage = routePackage;
        this.pin = pin;

    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoutePackage() {
        return routePackage;
    }

    public void setRoutePackage(String routePackage) {
        this.routePackage = routePackage;
    }


}

