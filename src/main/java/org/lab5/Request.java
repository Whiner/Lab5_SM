package org.lab5;

public class Request {
    private double serviceTime = 0;
    //private double waitTime = 0;
    private double failureTime = 0;
    private String name;
    //private int channelNumber = -1;

    public Request(double serviceTime, double failureTime, String name) {
        this.serviceTime = serviceTime;
        this.name = name;
        this.failureTime = failureTime;
    }

    public Request() {
    }

    /*public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }*/

    /*public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }*/

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

    public double getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
    }
}
