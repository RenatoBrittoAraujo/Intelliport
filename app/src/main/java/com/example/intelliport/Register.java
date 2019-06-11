package com.example.intelliport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView username;
    private TextView email;
    private TextView password;
    private TextView password_confirmation;

    private Button register;

    TextView flash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        flash = findViewById(R.id.flash);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password_confirmation = findViewById(R.id.password_confirmation);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_input())
                    validate_register();
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private boolean validate_input()
    {
        if(username.getText().equals("")) {
            toast("Empty username");
            return false;
        }
        if(email.getText().equals("")) {
            toast("Empty email");
            return false;
        }
        if(password.getText().equals("")) {
            toast("Empty password");
            return false;
        }
        if(password.getText().length() < 8) {
            toast("Password too small");
            return false;
        }
        if(!(password.getText().toString()).equals((password_confirmation.getText().toString()))) {
            toast("Confirmation does not match password");
            return false;
        }
        return true;
    }

    private void validate_register() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registration_successful();
                        } else {
                            toast("Register failed, try again");
                        }
                    }
                });
    }

    private void registration_successful() {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/name")
                .setValue(username.getText().toString());
        FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/balance")
                .setValue(0);
        FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/profit")
                .setValue(0);
        Intent intent = new Intent(this, MainPanel.class);
        toast("Register successful");
        startActivity(intent);
    }
}
