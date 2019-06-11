package com.example.intelliport;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.DirectoryIteratorException;

public class NewDriver extends AppCompatActivity {

    TextView driver_name;
    TextView driver_rate;
    Button create_driver;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_driver);

        driver_name = findViewById(R.id.average_speed);
        driver_rate = findViewById(R.id.kilometers_per_liter);
        create_driver = findViewById(R.id.new_vehicle);

        user = FirebaseAuth.getInstance().getCurrentUser();

        create_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_check()) {
                    Driver driver = new Driver();
                    driver.setName(driver_name.getText().toString());
                    driver.setRate(Double.parseDouble(driver_rate.getText().toString()));
                    driver.setOnRide(false);
                    addDriverToDatabase(driver);
                    finish();
                }
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private void addDriverToDatabase(Driver driver) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/drivers")
                .push();
        driver.setId(ref.getKey());
        ref.setValue(driver);
        toast("Driver " + driver.getName() + " added");
    }

    private boolean input_check() {
        if(driver_name.getText().toString().equals("")) {
             toast("Name must not be empty");
            return false;
        }
        if(driver_rate.getText().toString().equals("")) {
            toast("Rate must not be empty");
            return false;
        }
        if(Double.parseDouble(driver_rate.getText().toString()) <= 0.0){
            toast("Rate must positive");
            return false;
        }
        return true;
    }
}
