package com.example.intelliport;

public class Vehicle {

    private boolean usesAlcohol = false;
    private boolean usesDiesel = false;
    private boolean usesGasoline = false;

    private double maxCargo = 0.0;
    private double averageSpeed = 0.0;

    private double distancePerLiterGasoline = 0.0;
    private double distancePerLiterDiesel = 0.0;
    private double distancePerLiterAlcohol= 0.0;

    private boolean onRide;

    private String id;

    private String modelName = "";
    private String vehicle_type = "";

    public Vehicle() {
        onRide = false;
    }

    private String cheapestFuel;

    public double getCheapestVehicleCost(DeliveryLog deliveryLog, double distance) {

        double money = 1000000;

        if (usesAlcohol) {
            money = deliveryLog.getAlcoholPrice() * distance / distancePerLiterAlcohol;
            cheapestFuel = "Alcohol";
        }
        if(usesDiesel && deliveryLog.getDieselPrice() * distance / distancePerLiterDiesel < money) {
            money = deliveryLog.getDieselPrice() * distance / distancePerLiterDiesel;
            cheapestFuel = "Diesel";
        }
        if(usesGasoline && deliveryLog.getGasolinePrice() * distance / distancePerLiterGasoline < money) {
            money = deliveryLog.getGasolinePrice() * distance / distancePerLiterGasoline;
            cheapestFuel = "Gasoline";
        }

        return money;
    }

    public double getDuration(double distance) {
        return distance / getAverageSpeed();
    }

    public String getCheapestFuel() {
        return cheapestFuel;
    }

    public void setMaxCargo(double maxCargo) {
        this.maxCargo = maxCargo;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getMaxCargo() {
        return maxCargo;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public String getModelName() {
        return modelName;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setDistancePerLiterAlcohol(double distancePerLiterAlcohol) {
        this.distancePerLiterAlcohol = distancePerLiterAlcohol;
    }

    public void setDistancePerLiterDiesel(double distancePerLiterDiesel) {
        this.distancePerLiterDiesel = distancePerLiterDiesel;
    }

    public void setDistancePerLiterGasoline(double distancePerLiterGasoline) {
        this.distancePerLiterGasoline = distancePerLiterGasoline;
    }

    public double getDistancePerLiterAlcohol() {
        return distancePerLiterAlcohol;
    }

    public double getDistancePerLiterDiesel() {
        return distancePerLiterDiesel;
    }

    public double getDistancePerLiterGasoline() {
        return distancePerLiterGasoline;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsesGasoline(boolean usesGasoline) {
        this.usesGasoline = usesGasoline;
    }

    public void setUsesDiesel(boolean usesDiesel) {
        this.usesDiesel = usesDiesel;
    }

    public void setUsesAlcohol(boolean usesAlcohol) {
        this.usesAlcohol = usesAlcohol;
    }

    public boolean getUsesAlcohol() {
        return usesAlcohol;
    }

    public boolean getUsesGasoline() {
        return usesGasoline;
    }

    public boolean getUsesDiesel() {
        return usesDiesel;
    }

    public void setOnRide(boolean onRide) {
        this.onRide = onRide;
    }

    public String getId() {
        return id;
    }

    public boolean getOnRide() {
        return onRide;
    }

    public boolean weightFits(double cargoWeight) {
        return cargoWeight <= getMaxCargo();
    }
}
