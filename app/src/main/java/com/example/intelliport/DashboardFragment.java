package com.example.intelliport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    TextView balance;
    TextView profit;

    ListView deliveryList;
    List<Delivery> deliveries;

    private double profitValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        deliveryList = view.findViewById(R.id.delivery_list);
        balance = view.findViewById(R.id.balance);
        profit = view.findViewById(R.id.profit);

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        retrieveDeliveries();
        retrieveBalance();
    }

    private String beautifyDouble(double value)
    {
        return (value < 0 ? "- " : "+ ") + Math.round(Math.abs((value * 100.0)))/100.0;
    }

    private void retrieveDeliveries() {
        profitValue = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/deliveries");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveries = new ArrayList<>();
                for(DataSnapshot newDelivery : dataSnapshot.getChildren()) {
                    Delivery delivery = newDelivery.getValue(Delivery.class);
                    deliveries.add(delivery);
                    profitValue += delivery.getProfit();
                }
                listDeliveries();
                updateDeliveries();
                profit.setText(beautifyDouble(profitValue));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void retrieveBalance() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/balance");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                balance.setText(beautifyDouble(Double.parseDouble(dataSnapshot.getValue().toString())));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void listDeliveries() {
        CustomAdapter deliveryListAdapter = new CustomAdapter();
        deliveryList.setAdapter(deliveryListAdapter);
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return deliveries.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_delivery_list, null);
            TextView cargo_vehicle = convertView.findViewById(R.id.cargo_vehicle);
            TextView time = convertView.findViewById(R.id.time);

            cargo_vehicle.setText(deliveries.get(position).driverName() + " | " + deliveries.get(position).vehicleDescriptor() + " | " + deliveries.get(position).getCargo());
            time.setText(deliveries.get(position).timeUntilEnd());

            return convertView;
        }
    }

    public void updateDeliveries() {
        boolean update = false;
        for(Delivery delivery : deliveries) {
            if(delivery.finished()) {
                delivery.destroyDelivery();
                update = true;
            }
        }
        if(update)
            retrieveDeliveries();
    }

}
