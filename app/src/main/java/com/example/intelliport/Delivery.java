package com.example.intelliport;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;

public class Delivery {

    private double distance;
    private double weight;
    private double duration;
    private double profit;
    private String cargo;

    Vehicle vehicle;
    Driver driver;

    private long startTime;
    private long endTime;

    private String id;

    public Delivery() {}

    public void commitDelivery() {
        currentTime();
        setDuration(vehicle.getDuration(getDistance()));
        endTime();
        driverOnRide(true);
        vehicleOnRide(true);
        saveInDatabase();
    }

    public void destroyDelivery() {
        driverOnRide(false);
        vehicleOnRide(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveries/" + getId());
        ref.removeValue();
        ref = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/oldDeliveries").push();
        setId(ref.getKey());
        ref.setValue(this);
        addToBalance(getProfit());
    }

    private void addToBalance(final double amount) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/balance");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.setValue(Math.round(Double.parseDouble(dataSnapshot.getValue().toString()) + amount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void driverOnRide(boolean value) {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/drivers/" + driver.getId() + "/onRide").setValue(value);
    }

    private void vehicleOnRide(boolean value) {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/vehicles/" + vehicle.getId() + "/onRide").setValue(value);
    }

    private void saveInDatabase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveries").push();
        setId(ref.getKey());
        ref.setValue(this);
    }

    private void currentTime() {
        Calendar begin;
        begin = GregorianCalendar.getInstance();
        startTime = begin.getTimeInMillis();
    }

    private void endTime() {
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(begin.getTime());
        end.add(Calendar.MINUTE, (int) (getDuration() * 60.0));
        endTime = end.getTimeInMillis();
    }

    public String timeUntilEnd() {
        Calendar timeNow = GregorianCalendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endTime);
        Date ending = end.getTime();
        Date now = timeNow.getTime();
        long diffInMillies = ending.getTime() - now.getTime();
        if(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) >= 14)
            return ((int) (TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 7)) + " Weeks";
        if(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) > 1)
            return (TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)) + " Days";
        if(TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS) > 1)
            return (TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS)) + " Hours";
        if(TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) > 1)
            return (TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS)) + " Minutes";
        return "Ending shortly";
    }

    public String vehicleDescriptor() {
        return vehicle.getVehicle_type() + (vehicle.getModelName().equals("Generic")  ? "" : (" " + vehicle.getModelName()));
    }

    public String driverName() {
        return driver.getName();
    }

    public boolean finished() {
        Calendar timeNow = GregorianCalendar.getInstance();
        Calendar end = GregorianCalendar.getInstance();
        end.setTimeInMillis(endTime);
        return timeNow.getTimeInMillis() > end.getTimeInMillis();
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public double getWeight() {
        return weight;
    }

    public double getDistance() {
        return distance;
    }

    public Driver getDriver() {
        return driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getProfit() {
        return profit;
    }
}
