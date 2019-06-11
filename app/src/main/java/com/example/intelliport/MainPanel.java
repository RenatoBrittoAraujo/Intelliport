package com.example.intelliport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainPanel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView title;
    Button logoutbtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            openSignin();
        }

        setContentView(R.layout.activity_app);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        title = findViewById(R.id.title);

        title.setText("Dashboard");
        loadFragment(new DashboardFragment());

        logoutbtt = findViewById(R.id.logout);

        logoutbtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                openSignin();
            }
        });

    }

    private void openSignin() {
        Intent intent = new Intent(this, Signin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment == null)
            return false;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {

            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                title.setText("Dashboard");
                break;
            case R.id.navigation_new_vehicle:
                fragment = new VehicleFragment();
                title.setText("Vehicles");
                break;
            case R.id.navigation_new_delivery:
                fragment = new DeliveryFragment();
                title.setText("Deliveries");
                break;
            case R.id.navigation_new_driver:
                fragment = new DriverFragment();
                title.setText("Drivers");
                break;
        }

        return loadFragment(fragment);
    }
}
