package dao.daoapplicationcarrier.Entities;

import java.util.ArrayList;

public class Package {
    private String routePackageNumber;
    private String name;
    private String Route;
    private String packagNumber;
    private ArrayList<String> reasons;
    private boolean notified;
    private boolean delivered;

    public Package() {

    }

    public Package(String name, String routePackageNumber, String route, String packagNumber, ArrayList<String> reasons, boolean delivered, boolean notified) {
        this.name = name;
        this.routePackageNumber = routePackageNumber;
        this.Route = route;
        this.packagNumber = packagNumber;
        this.reasons = reasons;
        this.delivered = delivered;
        this.notified = notified;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getRoutePackageNumber() {
        return routePackageNumber;
    }

    public void setRoutePackageNumber(String routePackageNumber) {
        this.routePackageNumber = routePackageNumber;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public String getPackagNumber() {
        return packagNumber;
    }

    public void setPackagNumber(String packagNumber) {
        this.packagNumber = packagNumber;
    }

    public ArrayList<String> getReasons() {
        return reasons;
    }

    public void setReasons(ArrayList<String> reasons) {
        this.reasons = reasons;
    }

}

