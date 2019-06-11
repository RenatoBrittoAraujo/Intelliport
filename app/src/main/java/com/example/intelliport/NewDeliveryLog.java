package com.example.intelliport;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class NewDeliveryLog extends AppCompatActivity {

    Button goBack;
    Button save;

    TextView gasoline;
    TextView alcohol;
    TextView diesel;

    DeliveryLog deliveryLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery_log);

        goBack = findViewById(R.id.bGoBack);
        save = findViewById(R.id.bSave);

        gasoline = findViewById(R.id.gasolineprice);
        alcohol = findViewById(R.id.alcoholprice);
        diesel = findViewById(R.id.dieselprice);

        deliveryLog = new DeliveryLog();
        deliveryLog.recalculate();

        loadLog();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_check()) {
                    deliveryLog.setAlcoholPrice(Double.parseDouble(alcohol.getText().toString()));
                    deliveryLog.setDieselPrice(Double.parseDouble(diesel.getText().toString()));
                    deliveryLog.setGasolinePrice(Double.parseDouble(gasoline.getText().toString()));
                    saveDeliveryLog(deliveryLog);
                    toast("Saved!");
                }
            }
        });
    }

    private void saveDeliveryLog(DeliveryLog deliveryLog) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveryLog");
        ref.setValue(deliveryLog);
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private void loadLog() {
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveryLog").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    DeliveryLog deliveryLog = dataSnapshot.getValue(DeliveryLog.class);
                    alcohol.setText(deliveryLog.getAlcoholPrice() + "");
                    diesel.setText(deliveryLog.getDieselPrice() + "");
                    gasoline.setText(deliveryLog.getGasolinePrice() + "");
                    deliveryLog.recalculate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean input_check() {
        if(alcohol.getText().toString().equals("")) {
            toast("Alcohol price must not be empty");
            return false;
        }
        if(Double.parseDouble(alcohol.getText().toString()) <= 0.0){
            toast("Alcohol price must positive");
            return false;
        }
        if(diesel.getText().toString().equals("")) {
            toast("Diesel price must not be empty");
            return false;
        }
        if(Double.parseDouble(diesel.getText().toString()) <= 0.0){
            toast("Diesel price must positive");
            return false;
        }
        if(gasoline.getText().toString().equals("")) {
            toast("Gasoline price not be empty");
            return false;
        }
        if(Double.parseDouble(gasoline.getText().toString()) <= 0.0){
            toast("Gasoline price must positive");
            return false;
        }
        return true;
    }
}
