package com.example.intelliport;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryLog {

    private int number_of_finished_deliveries = 0;

    private double alcoholPrice = -1;
    private double dieselPrice = -1;
    private double gasolinePrice = -1;

    private double averageDriverRate = 0;
    private double averageVehicleSpeed = 0;
    private double averageVehicleConsumption = 0;

    private int number_of_vehicles = 0;
    private int number_of_drivers = 0;
    private int fuel_options = 0;

    private List<Delivery> past_deliveries = new ArrayList<>();

    public double getPastDelivriesAverageTime() {
        double average = 0;
        if(past_deliveries.size() == 0)
            return 0;
        for(Delivery delivery : past_deliveries) {
            average += delivery.getDuration();
        }
        return average / (double) past_deliveries.size();
    }

    public boolean deliveryLogReady() {
        if(alcoholPrice == -1)
            return false;
        if(dieselPrice == -1)
            return false;
        if(gasolinePrice == -1)
            return false;
        return true;
    }

    public double deliveryScore(Vehicle vehicle, Driver driver, double payment, double cargoWeight, double distance) {
        double score = 0;
        score += NewDelivery.getProfit(payment, driver, vehicle, distance, this) * 60.0;
        if(score < 0)
            return -1000000;
        score -= 35.0*(vehicle.getMaxCargo() - cargoWeight);
        if(averageDriverRate != 0)
            score += 20.0 * (driver.getRate() - getAverageDriverRate());
        if(averageVehicleSpeed != 0)
            score += 25.0 * (vehicle.getAverageSpeed() - getAverageVehicleSpeed());
        return score;
    }

    public void recalculate() {
        setDriverInfo();
        setVehicleInfo();
    }

    private void setVehicleInfo() {
        averageVehicleConsumption = 0;
        averageVehicleSpeed = 0;
        number_of_vehicles = 0;
        fuel_options = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/vehicles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot newVehicle : dataSnapshot.getChildren()) {
                    Vehicle vehicle = newVehicle.getValue(Vehicle.class);
                    number_of_vehicles++;
                    if (vehicle.getUsesAlcohol()) {
                        averageVehicleConsumption += vehicle.getDistancePerLiterAlcohol();
                        fuel_options++;
                    }
                    if (vehicle.getUsesDiesel()) {
                        averageVehicleConsumption += vehicle.getDistancePerLiterDiesel();
                        fuel_options++;
                    }
                    if(vehicle.getUsesGasoline()) {
                        averageVehicleConsumption += vehicle.getDistancePerLiterGasoline();
                        fuel_options++;
                    }
                    averageVehicleSpeed += vehicle.getAverageSpeed();
                }
                if(number_of_vehicles != 0)
                    averageVehicleSpeed /= number_of_vehicles;
                if(fuel_options != 0)
                    averageVehicleConsumption /= fuel_options;

                Log.i("CALCULATIONS", "VC:" + averageVehicleConsumption + "VS:" + averageVehicleSpeed);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setDriverInfo() {
        averageDriverRate = 0.0;
        number_of_drivers = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/drivers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot newDriver : dataSnapshot.getChildren()) {
                    Driver driver = newDriver.getValue(Driver.class);
                    number_of_drivers++;
                    averageDriverRate += driver.getRate();
                }
                if(number_of_drivers != 0)
                    averageDriverRate /= number_of_drivers;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public double getAverageVehicleConsumption() {
        return averageVehicleConsumption;
    }

    public double getAverageVehicleSpeed() {
        return averageVehicleSpeed;
    }


    public double getAlcoholPrice() {
        return alcoholPrice;
    }

    public double getDieselPrice() {
        return dieselPrice;
    }

    public double getGasolinePrice() {
        return gasolinePrice;
    }

    public void setAlcoholPrice(double alcoholPrice) {
        this.alcoholPrice = alcoholPrice;
    }

    public void setDieselPrice(double dieselPrice) {
        this.dieselPrice = dieselPrice;
    }

    public void setGasolinePrice(double gasolinePrice) {
        this.gasolinePrice = gasolinePrice;
    }

    public void setAverageVehicleConsumption(double averageVehicleConsumption) {
        this.averageVehicleConsumption = averageVehicleConsumption;
    }

    public void setAverageDriverRate(double averageDriverRate) {
        this.averageDriverRate = averageDriverRate;
    }

    public void setAverageVehicleSpeed(double averageVehicleSpeed) {
        this.averageVehicleSpeed = averageVehicleSpeed;
    }

    public void setFuel_options(int fuel_options) {
        this.fuel_options = fuel_options;
    }

    public void setNumber_of_drivers(int number_of_drivers) {
        this.number_of_drivers = number_of_drivers;
    }

    public void setNumber_of_finished_deliveries(int number_of_finished_deliveries) {
        this.number_of_finished_deliveries = number_of_finished_deliveries;
    }

    public void setNumber_of_vehicles(int number_of_vehicles) {
        this.number_of_vehicles = number_of_vehicles;
    }

    public void setPast_deliveries(List<Delivery> past_deliveries) {
        this.past_deliveries = past_deliveries;
    }

    public double getAverageDriverRate() {
        return averageDriverRate;
    }

    public int getFuel_options() {
        return fuel_options;
    }

    public int getNumber_of_drivers() {
        return number_of_drivers;
    }

    public int getNumber_of_finished_deliveries() {
        return number_of_finished_deliveries;
    }

    public int getNumber_of_vehicles() {
        return number_of_vehicles;
    }

    public List<Delivery> getPast_deliveries() {
        return past_deliveries;
    }
}
