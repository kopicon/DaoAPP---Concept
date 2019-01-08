package dao.daoapplicationcarrier.Entities;

public class RoutePackage {

    private String name;
    private String workerName;
    private String workerNumber;
    private boolean finished;
    private boolean notified;

    public RoutePackage(){

    }

    public RoutePackage(String name, String workerNumber, String workerName, boolean finished, boolean notified) {

        this.name = name;
        this.workerName = workerName;
        this.workerNumber = workerNumber;
        this.finished = finished;
        this.notified = notified;
    }
    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
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

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


}
