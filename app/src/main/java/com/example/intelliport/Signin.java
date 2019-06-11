package com.example.intelliport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signin extends AppCompatActivity {

    private Button register;
    private Button login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
            openDashboard();

    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void openDashboard()
    {
        Intent intent = new Intent(this, MainPanel.class);
        startActivity(intent);
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
