package com.example.intelliport;

public class Driver {

    private String name;
    private double rate;
    private boolean onRide;
    private String id;

    Driver() {}

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public void setOnRide(boolean onRide) {
        this.onRide = onRide;
    }

    public boolean getOnRide() {
        return onRide;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}
