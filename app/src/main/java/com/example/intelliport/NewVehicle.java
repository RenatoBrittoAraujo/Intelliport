package com.example.intelliport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewVehicle extends AppCompatActivity {

    boolean custom_vehicle;

    TextView model;
    TextView averageSpeed;
    TextView maxCargo;
    TextView kmPerLiterAlcohol;
    TextView kmPerLiterDiesel;
    TextView kmPerLiterGasoline;

    TextView alcoholText;
    TextView dieselText;
    TextView gasolineText;

    Switch custom;
    Switch diesel;
    Switch gasoline;
    Switch alcohol;

    boolean usesAlcohol = false;
    boolean usesDiesel = false;
    boolean usesGasoline = false;

    Button addVehicle;

    Spinner type_vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        model = findViewById(R.id.vehicle_model);
        averageSpeed = findViewById(R.id.average_speed);
        maxCargo = findViewById(R.id.max_cargo);

        kmPerLiterAlcohol = findViewById(R.id.kilometers_per_liter_alcohol);
        kmPerLiterDiesel = findViewById(R.id.kilometers_per_liter_diesel);
        kmPerLiterGasoline = findViewById(R.id.kilometers_per_liter_gasoline);

        alcoholText = findViewById(R.id.alcohol_text);
        dieselText = findViewById(R.id.diesel_text);
        gasolineText = findViewById(R.id.gasoline_text);

        custom = findViewById(R.id.custom_vehicle);

        alcohol = findViewById(R.id.alcohol);
        diesel = findViewById(R.id.diesel);
        gasoline = findViewById(R.id.gasoline);

        addVehicle = findViewById(R.id.new_vehicle);
        type_vehicle = findViewById(R.id.type_vehicle);

        custom_vehicle = false;
        custom.setChecked(false);
        hideCustom();

        custom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    custom_vehicle = true;
                    showCustom();
                } else {
                    custom_vehicle = false;
                    hideCustom();
                }
            }
        });

        alcohol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usesAlcohol = isChecked;
            }
        });

        diesel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usesDiesel = isChecked;
            }
        });

        gasoline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usesGasoline = isChecked;
            }
        });

        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!custom_vehicle || (custom_vehicle && input_check_custom())) {
                    addVehicleToDatabase();
                    finish();
                }
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private void hideCustom() {
        model.setVisibility(View.INVISIBLE);
        averageSpeed.setVisibility(View.INVISIBLE);
        maxCargo.setVisibility(View.INVISIBLE);
        kmPerLiterAlcohol.setVisibility(View.INVISIBLE);
        kmPerLiterDiesel.setVisibility(View.INVISIBLE);
        kmPerLiterGasoline.setVisibility(View.INVISIBLE);
        alcoholText.setVisibility(View.INVISIBLE);
        dieselText.setVisibility(View.INVISIBLE);
        gasolineText.setVisibility(View.INVISIBLE);
        alcohol.setVisibility(View.INVISIBLE);
        diesel.setVisibility(View.INVISIBLE);
        gasoline.setVisibility(View.INVISIBLE);
    }

    private void showCustom() {
        model.setVisibility(View.VISIBLE);
        averageSpeed.setVisibility(View.VISIBLE);
        maxCargo.setVisibility(View.VISIBLE);
        kmPerLiterAlcohol.setVisibility(View.VISIBLE);
        kmPerLiterDiesel.setVisibility(View.VISIBLE);
        kmPerLiterGasoline.setVisibility(View.VISIBLE);
        alcoholText.setVisibility(View.VISIBLE);
        dieselText.setVisibility(View.VISIBLE);
        gasolineText.setVisibility(View.VISIBLE);
        alcohol.setVisibility(View.VISIBLE);
        diesel.setVisibility(View.VISIBLE);
        gasoline.setVisibility(View.VISIBLE);
    }

    private boolean input_check_custom() {
        if(model.getText().toString().equals("")) {
            toast("Model must not be empty");
            return false;
        }
        if(averageSpeed.getText().toString().equals("")) {
            toast("Average Speed must not be empty");
            return false;
        }
        if(Double.parseDouble(averageSpeed.getText().toString()) <= 0.0) {
            toast("Average Speed must be positive");
            return false;
        }
        if(maxCargo.getText().toString().equals("")) {
            toast("Max Cargo must not empty");
            return false;
        }
        if(Double.parseDouble(maxCargo.getText().toString()) <= 0.0) {
            toast("Max Cargo must be positive");
            return false;
        }
        if(usesAlcohol && kmPerLiterAlcohol.getText().toString().equals("")) {
            toast("Alcohol consumption must not empty");
            return false;
        }
        if(usesAlcohol && Double.parseDouble(kmPerLiterAlcohol.getText().toString()) <= 0.0) {
            toast("Alcohol consumption must be positive");
            return false;
        }
        if(usesDiesel && kmPerLiterDiesel.getText().toString().equals("")) {
            toast("Diesel consumption must not empty");
            return false;
        }
        if(usesDiesel && Double.parseDouble(kmPerLiterDiesel.getText().toString()) <= 0.0) {
            toast("Diesel consumption must be positive");
            return false;
        }
        if(usesGasoline && kmPerLiterGasoline.getText().toString().equals("")) {
            toast("Gasoline consumption must not empty");
            return false;
        }
        if(usesGasoline && Double.parseDouble(kmPerLiterGasoline.getText().toString()) <= 0.0) {
            toast("Gasoline consumption must be positive");
            return false;
        }
        if(!usesGasoline && !usesDiesel && !usesAlcohol) {
            toast("Vehicle must use fuel");
            return false;
        }
        return true;
    }

    private void addVehicleToDatabase() {

        Vehicle newVehicle = new Vehicle();
        String vehicle_type = type_vehicle.getSelectedItem().toString();
        newVehicle.setVehicle_type(vehicle_type);

        if(custom_vehicle) {

            newVehicle.setModelName(model.getText().toString());
            newVehicle.setMaxCargo(Double.parseDouble(maxCargo.getText().toString()));
            newVehicle.setAverageSpeed(Double.parseDouble(averageSpeed.getText().toString()));

            if(usesAlcohol) {
                newVehicle.setUsesAlcohol(true);
                newVehicle.setDistancePerLiterAlcohol(Double.parseDouble(kmPerLiterAlcohol.getText().toString()));
            }
            if(usesDiesel) {
                newVehicle.setUsesDiesel(true);
                newVehicle.setDistancePerLiterDiesel(Double.parseDouble(kmPerLiterDiesel.getText().toString()));
            }
            if(usesGasoline) {
                newVehicle.setUsesGasoline(true);
                newVehicle.setDistancePerLiterGasoline(Double.parseDouble(kmPerLiterGasoline.getText().toString()));
            }

        } else {

            if(vehicle_type.equals("Truck")) {
                newVehicle.setUsesDiesel(true);
                newVehicle.setDistancePerLiterDiesel(8);
                newVehicle.setAverageSpeed(60);
                newVehicle.setMaxCargo(30000);
            }

            if(vehicle_type.equals("Motorcycle")) {
                newVehicle.setUsesGasoline(true);
                newVehicle.setUsesAlcohol(true);
                newVehicle.setDistancePerLiterAlcohol(43);
                newVehicle.setDistancePerLiterGasoline(50);
                newVehicle.setAverageSpeed(110);
                newVehicle.setMaxCargo(50);
            }

            if(vehicle_type.equals("Car")) {
                newVehicle.setUsesGasoline(true);
                newVehicle.setUsesAlcohol(true);
                newVehicle.setDistancePerLiterAlcohol(12);
                newVehicle.setDistancePerLiterGasoline(14);
                newVehicle.setAverageSpeed(100);
                newVehicle.setMaxCargo(360);
            }

            if(vehicle_type.equals("Utility Van")) {
                newVehicle.setUsesDiesel(true);
                newVehicle.setDistancePerLiterDiesel(10);
                newVehicle.setAverageSpeed(80);
                newVehicle.setMaxCargo(3500);
            }

            newVehicle.setModelName("Generic");
        }

        DatabaseReference vehicleNode = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/vehicles/").push();

        newVehicle.setId(vehicleNode.getKey());

        vehicleNode.setValue(newVehicle);
        toast("New "  + type_vehicle.getSelectedItem().toString() + " created");
    }
}
