package org.lab5;

public class Request {
    private double serviceTime = 0;
    private double currentWaitTime = 0;
    private double lastFailureTime = 0;
    private double failureTime = 0;
    private String name;
    //private int channelNumber = -1;

    public Request(double serviceTime, double failureTime, String name) {
        this.serviceTime = serviceTime;
        this.name = name;
        setFailureTime(failureTime);
    }

    public double getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
        this.lastFailureTime = failureTime;
    }

    public Request() {
    }

    public double getCurrentWaitTime() {
        return currentWaitTime;
    }

    public void setCurrentWaitTime(double currentWaitTime) {
        this.currentWaitTime = currentWaitTime;
    }

    public void addCurrentWaitTime(double time) {
        this.currentWaitTime += time;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLastFailureTime() {
        return lastFailureTime;
    }

    public void setLastFailureTime(double lastFailureTime) {
        this.lastFailureTime = lastFailureTime;
    }
}
