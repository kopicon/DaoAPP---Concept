package dao.daoapplicationcarrier.Entities;

import java.util.ArrayList;

public class Scooter {
    private String carrierName;
    private String scooterNumber;
    private ArrayList<String> faliures;
    private boolean notified;

    public Scooter() {

    }


    public Scooter(String carrierName, String scooterNumber, ArrayList<String> faliures, boolean notified) {
        this.carrierName = carrierName;
        this.scooterNumber = scooterNumber;
        this.faliures = faliures;
        this.notified = notified;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getScooterNumber() {
        return scooterNumber;
    }

    public void setScooterNumber(String scooterNumber) {
        this.scooterNumber = scooterNumber;
    }

    public ArrayList<String> getFaliures() {
        return faliures;
    }

    public void setFaliures(ArrayList<String> faliures) {
        this.faliures = faliures;
    }
}
