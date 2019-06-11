package com.example.intelliport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VehicleFragment extends Fragment {

    View current_view;
    Button newVehicle;
    Button  removeVehicle;

    FirebaseDatabase database;
    DatabaseReference ref;

    ArrayList<Vehicle> vehicles;

    boolean selected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vehicle_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users/" + FirebaseAuth.getInstance().getUid());

        current_view = view;

        newVehicle = view.findViewById(R.id.add_vehicle);
        removeVehicle = view.findViewById(R.id.remove_vehicle);

        reload();

        selected = false;

        removeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected) {
                    toast("Remove mode off");
                    selected = false;
                } else {
                    toast("Select vehicle from list");
                    selected = true;
                }
            }
        });

        newVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewVehicle();
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this.getActivity().getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/vehicles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicles = new ArrayList<>();
                for(DataSnapshot newVehicle : dataSnapshot.getChildren()) {
                    Vehicle vehicle = newVehicle.getValue(Vehicle.class);
                    vehicles.add(vehicle);
                }
                listVehicles();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void listVehicles() {
        ListView vehicleList = current_view.findViewById(R.id.driver_list);
        VehicleFragment.CustomAdapter driverListAdapter = new VehicleFragment.CustomAdapter();
        vehicleList.setAdapter(driverListAdapter);
    }

    private void openNewVehicle() {
        Intent intent = new Intent(current_view.getContext(), NewVehicle.class);
        startActivity(intent);
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return vehicles.size();
        }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) {
            if(selected) {
                removeVehicle(position);
            }
            selected = false;
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_driver_list, null);

            TextView name_rate = convertView.findViewById(R.id.name_rate);
            TextView occupied = convertView.findViewById(R.id.isOcuppied);

            name_rate.setText(vehicles.get(position).getVehicle_type() + " " + (!vehicles.get(position).getModelName().equals("Generic") ? (" '" + vehicles.get(position).getModelName() + "'") : ""));
            occupied.setText(vehicles.get(position).getOnRide() ? "On ride" : "Not on ride");

            return convertView;
        }
    }

    private void removeVehicle(int position) {
        if(vehicles.get(position).getOnRide()) {
            toast("You cannot remove vehicle on ride");
            return;
        }
        toast(vehicles.get(position).getVehicle_type() + " removed");
        ref.child("vehicles/" + vehicles.get(position).getId()).removeValue();
        reload();
    }

}
