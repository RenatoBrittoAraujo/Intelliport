package com.example.intelliport;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewDelivery extends AppCompatActivity {

    private List<Driver> drivers;
    private List<Vehicle> vehicles;
    private DeliveryLog deliveryLog;

    private boolean deliveryLogLoaded = false;
    private boolean vehiclesLoaded = false;
    private boolean driversLoaded = false;

    TextView tCheap;
    TextView tFast;
    TextView tBalanced;

    Button fast;
    Button cheap;
    Button balanced;

    Button goBack;

    Delivery delivery;

    Driver cheapestDriver = null;
    Vehicle cheapestVehicle = null, fastestVehicle = null;

    Driver balancedDriver = null;
    Vehicle balancedVehicle = null;

    double payment;
    double cargoWeight;
    double distance;
    String cargo;

    double cheapProfit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);

        goBack = findViewById(R.id.bGoBack);

        delivery = new Delivery();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tFast = findViewById(R.id.tFastest);
        tBalanced = findViewById(R.id.tBalanced);
        tCheap = findViewById(R.id.tCheapest);

        fast = findViewById(R.id.bFastest);
        cheap = findViewById(R.id.bChepest);
        balanced = findViewById(R.id.bBalanced);

        retrieveDrivers();
        retrieveVehicles();
        retrieveDeliveryLog();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fastestVehicle == null || cheapestDriver == null) {
                    toast("No fastest option found");
                } else {
                    delivery.setVehicle(fastestVehicle);
                    delivery.setDriver(cheapestDriver);
                    delivery.setProfit(round(getProfit(payment, cheapestDriver, fastestVehicle, distance, deliveryLog)));
                    delivery.setWeight(cargoWeight);
                    delivery.setDistance(distance);
                    delivery.commitDelivery();
                    delivery.setCargo(cargo);
                    toast("Fastest option added");
                    finish();
                }
            }
        });

        cheap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cheapProfit < 0.0) {
                    toast("No profitable ride");
                } else if(cheapestDriver == null || cheapestVehicle == null) {
                    toast("No profitable option found");
                } else {
                    delivery.setVehicle(cheapestVehicle);
                    delivery.setDriver(cheapestDriver);
                    delivery.setProfit(round(getProfit(payment, cheapestDriver, cheapestVehicle, distance, deliveryLog)));
                    delivery.setWeight(cargoWeight);
                    delivery.setDistance(distance);
                    delivery.commitDelivery();
                    delivery.setCargo(cargo);
                    toast("Profitable option added");
                    finish();
                }
            }
        });

        balanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(balancedDriver == null || balancedVehicle == null) {
                    toast("No balanced option found");
                } else {
                    delivery.setVehicle(balancedVehicle);
                    delivery.setDriver(balancedDriver);
                    delivery.setProfit(round(getProfit(payment, balancedDriver, balancedVehicle, distance, deliveryLog)));
                    delivery.setWeight(cargoWeight);
                    delivery.setDistance(distance);
                    delivery.commitDelivery();
                    delivery.setCargo(cargo);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveDrivers();
        retrieveVehicles();
        retrieveDeliveryLog();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        retrieveDrivers();
        retrieveVehicles();
        retrieveDeliveryLog();
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    static private int infinite = 100000000;

    private double round(double value) {
        return Math.floor(value * 100.0) / 100.0;
    }

    private void generateOptions() {

        if(!deliveryLogLoaded || !driversLoaded || !vehiclesLoaded)
            return;

        payment = getIntent().getDoubleExtra("Payment", -1);
        cargoWeight = getIntent().getDoubleExtra("Weight", -1);
        distance = getIntent().getDoubleExtra("Distance", -1);
        cargo = getIntent().getExtras().getString("Cargo");
        delivery.setCargo(cargo);


        double cheapest = infinite;
        double fastest = -infinite;
        double balancedScore = -infinite;
        double cheapestRate = infinite;

        boolean fitCargo = false;
        boolean vehicleFound = false;
        boolean driverFound = false;

        boolean atLeastOneOption = false;

        for(Driver driver : drivers) {
            for(Vehicle vehicle : vehicles) {
                if(vehicle.getOnRide())
                    continue;
                vehicleFound = true;
                if(!vehicle.weightFits(cargoWeight))
                    continue;
                fitCargo = true;
                if(driver.getOnRide())
                    continue;
                driverFound = true;

                atLeastOneOption = true;

                double vehicleC = vehicle.getCheapestVehicleCost(deliveryLog, distance);
                if(vehicleC < cheapest) {
                    cheapestVehicle = vehicle;
                    cheapest = vehicleC;
                }

                double rate = driver.getRate();
                if(rate < cheapestRate) {
                    cheapestDriver = driver;
                    cheapestRate = rate;
                }

                double speed = vehicle.getAverageSpeed();
                if(speed > fastest) {
                    fastestVehicle = vehicle;
                    fastest = speed;
                }

                double deliveryScore = deliveryLog.deliveryScore(vehicle, driver, payment, cargoWeight, distance);

                if(deliveryScore > balancedScore) {
                    balancedScore = deliveryScore;
                    balancedDriver = driver;
                    balancedVehicle = vehicle;
                }
            }

            if(!atLeastOneOption) {
                if(!vehicleFound)
                    toast("No vehicle available");
                else if(!fitCargo)
                    toast("Weight too heavy for vehicles");
                else if(!driverFound)
                    toast("No driver available");
                return;
            }

            double fastProfit = round(getProfit(payment, cheapestDriver, fastestVehicle, distance, deliveryLog));
            double fastTime = round(fastestVehicle.getDuration(distance));

            double balancedProfit = round(getProfit(payment, balancedDriver, balancedVehicle, distance, deliveryLog));
            double balancedTime = round(balancedVehicle.getDuration(distance));

            double cheapestProfit = round(getProfit(payment, cheapestDriver, cheapestVehicle, distance, deliveryLog));
            double cheapestTime = round(cheapestVehicle.getDuration(distance));

            cheapProfit = cheapestProfit;

            if(cheapestProfit > 0)
                tCheap.setText("Duration: " + cheapestTime + " hour(s) | Profit: $ " + cheapestProfit + " | Driver name: " + cheapestDriver.getName() + " | Vehicle: " + (cheapestVehicle.getModelName().equals("Generic")? cheapestVehicle.getVehicle_type() : (cheapestVehicle.getVehicle_type() + " " + cheapestVehicle.getModelName())));
            else
                tCheap.setText("No profitable delivery was found");

            tFast.setText("Duration: " + fastTime + " hour(s) | Profit: $ " + fastProfit + " | Driver name: " + cheapestDriver.getName() + " | Vehicle: " + (fastestVehicle.getModelName().equals("Generic") ? fastestVehicle.getVehicle_type() : (fastestVehicle.getVehicle_type() + " " + fastestVehicle.getModelName())));

            tBalanced.setText("Duration: " + balancedTime + " hour(s) | Profit: $ " + balancedProfit + " | Driver name: " + balancedDriver.getName() + " | Vehicle: " + (balancedVehicle.getModelName().equals("Generic" )? balancedVehicle.getVehicle_type() : (balancedVehicle.getVehicle_type() + " " + balancedVehicle.getModelName())));
        }

    }

    public static double getProfit(double payment, Driver driver, Vehicle vehicle, double distance, DeliveryLog deliveryLog) {
        return payment - driver.getRate() * vehicle.getDuration(distance) - vehicle.getCheapestVehicleCost(deliveryLog, distance);
    }

    private void retrieveDeliveryLog() {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveryLog").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryLog = dataSnapshot.getValue(DeliveryLog.class);
                deliveryLogLoaded = true;
                generateOptions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void retrieveDrivers() {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/drivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivers = new ArrayList<>();
                for(DataSnapshot newDriver : dataSnapshot.getChildren()) {
                    Driver driver = newDriver.getValue(Driver.class);
                    if(!driver.getOnRide())
                        drivers.add(driver);
                }
                driversLoaded = true;
                generateOptions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void retrieveVehicles() {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/vehicles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicles = new ArrayList<>();
                for(DataSnapshot newVehicle : dataSnapshot.getChildren()) {
                    Vehicle vehicle = newVehicle.getValue(Vehicle.class);
                    if(!vehicle.getOnRide())
                        vehicles.add(vehicle);
                }
                vehiclesLoaded = true;
                generateOptions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
