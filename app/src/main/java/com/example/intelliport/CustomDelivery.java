package com.example.intelliport;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class CustomDelivery extends AppCompatActivity {

    private ListView vehicleList, driverList;

    private Button goBack, create;

    private TextView seeResult;

    private List<Driver> drivers;
    private List<Vehicle> vehicles;

    private DeliveryLog deliveryLog;

    private boolean vehicleSelected = false, driverSelected = false;

    private boolean deliveryLogLoaded = false, vehiclesLoaded = false, driversLoaded = false;

    Driver driver;
    Vehicle vehicle;

    double payment;
    double cargoWeight;
    double distance;
    String cargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_delivery);

        drivers = new ArrayList<>();
        vehicles = new ArrayList<>();

        vehicleList = findViewById(R.id.vehiclelist);
        driverList = findViewById(R.id.driverlist);

        goBack = findViewById(R.id.goBack);
        create = findViewById(R.id.create);

        seeResult = findViewById(R.id.result);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        retrieveDeliveryLog();
        retrieveDrivers();
        retrieveVehicles();

        payment = getIntent().getDoubleExtra("Payment", -1);
        cargoWeight = getIntent().getDoubleExtra("Weight", -1);
        distance = getIntent().getDoubleExtra("Distance", -1);
        cargo = getIntent().getExtras().getString("Cargo");

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!deliveryLogLoaded || !vehicleSelected || !driversLoaded)
                    toast("Loading data...");
                else if(!vehicleSelected)
                    toast("Select vehicle");
                else if(!driverSelected)
                    toast("Select driver");
                else
                {
                    Delivery delivery = createDelivery();
                    delivery.commitDelivery();
                    toast("New delivery created");
                    finish();
                }

            }
        });
    }

    private void newProfit() {
        if(!deliveryLogLoaded) {
            toast("Fill delivery info");
            return;
        }

        if(!vehicleSelected && !driverSelected)
            return;

        if(!vehicleSelected) {
            seeResult.setText("Now select a vehicle");
            return;
        }

        if(!driverSelected) {
            seeResult.setText("Now select a driver");
            return;
        }

        double profit = getProfit(payment, driver, vehicle, distance, deliveryLog);
        seeResult.setText("Profit: " + round(profit * 100.0) / 100.0 + " | " + vehicle.getVehicle_type() + (vehicle.getModelName().equals("Generic") ? "" : (" " +  vehicle.getModelName())) + " | " + driver.getName());
    }

    private Delivery createDelivery() {
        Delivery delivery = new Delivery();
        delivery.setVehicle(vehicle);
        delivery.setDriver(driver);
        delivery.setProfit(round(getProfit(payment, driver, vehicle, distance, deliveryLog)));
        delivery.setWeight(cargoWeight);
        delivery.setDistance(distance);
        delivery.setCargo(cargo);
        return delivery;
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
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
                fillDriverList();
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
                    if(!vehicle.getOnRide() && vehicle.weightFits(cargoWeight))
                        vehicles.add(vehicle);
                }
                vehiclesLoaded = true;
                fillVehicleList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fillDriverList() {
        CustomDelivery.DriverAdapter driverListAdapter = new CustomDelivery.DriverAdapter();
        driverList.setAdapter(driverListAdapter);
    }

    private void fillVehicleList() {
        CustomDelivery.VehicleAdapter vehicleListAdapter = new CustomDelivery.VehicleAdapter();
        vehicleList.setAdapter(vehicleListAdapter);
    }

    class VehicleAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return vehicles.size();
        }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) {
            vehicleSelected = true;
            vehicle = vehicles.get(position);
            newProfit();
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_driver_list, null);

            TextView name = convertView.findViewById(R.id.name_rate);
            TextView model = convertView.findViewById(R.id.isOcuppied);

            name.setText(vehicles.get(position).getVehicle_type());
            model.setText(vehicles.get(position).getModelName());

            return convertView;
        }
    }

    class DriverAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return drivers.size();
        }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) {
            driverSelected = true;
            driver = drivers.get(position);
            newProfit();
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_driver_list, null);

            TextView name = convertView.findViewById(R.id.name_rate);
            TextView rate = convertView.findViewById(R.id.isOcuppied);

            name.setText(drivers.get(position).getName());
            rate.setText("$ " + round(drivers.get(position).getRate() * 100.0) / 100.0  + " per hour");

            return convertView;
        }
    }
}
