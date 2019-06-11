package com.example.intelliport;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeliveryFragment extends Fragment {

    private Button getOptions;
    private Button fillDeliveryInfo;
    private Button learnMore;
    private Button customDelivery;

    private TextView tCargo;
    private TextView tPayment;
    private TextView tDistance;
    private TextView tWeight;

    boolean deliveryLogSet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.delivery_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tCargo = view.findViewById(R.id.cargo);
        tPayment = view.findViewById(R.id.payment);
        tDistance = view.findViewById(R.id.distance);
        tWeight = view.findViewById(R.id.weight);

        fillDeliveryInfo = view.findViewById(R.id.delivery_info);
        learnMore = view.findViewById(R.id.learn2);
        customDelivery = view.findViewById(R.id.custom_delivery);

        deliveryLogExists();

        getOptions = view.findViewById(R.id.options);

        getOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_check() && deliveryLogExists()) {
                    openNewDelivery();
                }
            }
        });

        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLearnMore();
            }
        });

        fillDeliveryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeliveryInfo();
            }
        });

        customDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(input_check() && deliveryLogExists()) {
                    openCustomDelivery();
                }
            }
        });
    }

    private void openCustomDelivery() {
        Intent intent = new Intent(this.getActivity(), CustomDelivery.class);
        intent.putExtra("Cargo", tCargo.getText().toString());
        intent.putExtra("Weight", Double.parseDouble(tWeight.getText().toString()));
        intent.putExtra("Payment", Double.parseDouble(tPayment.getText().toString()));
        intent.putExtra("Distance", Double.parseDouble(tDistance.getText().toString()));
        startActivity(intent);
    }

    private void openNewDelivery() {
        Intent intent = new Intent(this.getActivity(), NewDelivery.class);
        intent.putExtra("Cargo", tCargo.getText().toString());
        intent.putExtra("Weight", Double.parseDouble(tWeight.getText().toString()));
        intent.putExtra("Payment", Double.parseDouble(tPayment.getText().toString()));
        intent.putExtra("Distance", Double.parseDouble(tDistance.getText().toString()));
        startActivity(intent);
    }

    private void openLearnMore() {
        Intent intent = new Intent(this.getActivity(), LearnMore.class);
        startActivity(intent);
    }

    private void openDeliveryInfo() {
        Intent intent = new Intent(this.getActivity(), NewDeliveryLog.class);
        startActivity(intent);
    }

    private void toast(String text) {
        Toast.makeText(this.getActivity().getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private boolean input_check() {
        if(tCargo.getText().toString().equals("")) {
            toast("Cargo description must not be empty");
            return false;
        }
        if(tPayment.getText().toString().equals("")) {
            toast("Payment must not be empty");
            return false;
        }
        if(Double.parseDouble(tPayment.getText().toString()) <= 0.0) {
            toast("Payment must be positive");
            return false;
        }
        if(tDistance.getText().toString().equals("")) {
            toast("Distance must not be empty");
            return false;
        }
        if(Double.parseDouble(tDistance.getText().toString()) <= 0.0) {
            toast("Distance must be positive");
            return false;
        }
        if(tWeight.getText().toString().equals("")) {
            toast("Weight must not be empty");
            return false;
        }
        if(Double.parseDouble(tWeight.getText().toString()) <= 0.0) {
            toast("Weight must be positive");
            return false;
        }
        return true;
    }

    private boolean deliveryLogExists() {
        if(!deliveryLogSet) {
            FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/deliveryLog").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        deliveryLogSet = true;
                    else
                        toast("Fill delivery info");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        return deliveryLogSet;
    }

}
