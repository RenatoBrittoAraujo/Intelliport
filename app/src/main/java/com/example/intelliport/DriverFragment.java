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
import java.util.List;

public class DriverFragment extends Fragment {

    View current_view;
    Button newDriver;
    Button removeDriver;

    TextView manager_name;

    List<Driver> drivers;

    boolean selected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.driver_layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        current_view = view;

        newDriver = view.findViewById(R.id.add_driver);
        removeDriver = view.findViewById(R.id.remove_driver);
        manager_name = view.findViewById(R.id.manager);

        reload();

        selected = false;

        removeDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected) {
                    toast("Remove mode off");
                    selected = false;
                } else {
                    toast("Select driver from list");
                    selected = true;
                }
            }
        });

        newDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewDriver();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void openNewDriver() {
        Intent intent = new Intent(current_view.getContext(), NewDriver.class);
        startActivity(intent);
    }

    private void toast(String text) {
        Toast.makeText(this.getActivity().getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private void reload() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/drivers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivers = new ArrayList<>();
                for(DataSnapshot newDriver : dataSnapshot.getChildren()) {
                    Driver driver = newDriver.getValue(Driver.class);
                    drivers.add(driver);
                }
                listDrivers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setManager(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setManager(String name) {
        manager_name.setText("Manager: " + name);
    }

    private void listDrivers() {
        ListView driverList = current_view.findViewById(R.id.driver_list);
        DriverFragment.CustomAdapter driverListAdapter = new DriverFragment.CustomAdapter();
        driverList.setAdapter(driverListAdapter);
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return drivers.size();
        }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) {
            if(selected) {
                removeDriver(position);
                selected = false;
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_driver_list, null);

            TextView name_rate = convertView.findViewById(R.id.name_rate);
            TextView occupied = convertView.findViewById(R.id.isOcuppied);

            name_rate.setText(drivers.get(position).getName() + " $ " + drivers.get(position).getRate() + " per hour");
            occupied.setText(drivers.get(position).getOnRide() ? "On ride" : "Not on ride");

            return convertView;
        }
    }

    private void removeDriver(int position) {
        if(drivers.get(position).getOnRide()) {
            toast("You cannot remove driver on ride");
            return;
        }
        toast(drivers.get(position).getName() + " removed");
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid() + "/drivers/" + drivers.get(position).getId()).removeValue();
        reload();
    }
}
