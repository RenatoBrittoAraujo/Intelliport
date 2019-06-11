package com.example.intelliport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextView email;
    private TextView password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_check())
                    authenticate_login();
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this.getBaseContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    private void authenticate_login()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            openDashboard();
                        } else {
                            toast("Log in failed, try again");
                        }
                    }
                });
    }

    private boolean input_check()
    {
        if(email.getText().toString().equals("")) {
            toast("Empty email");
            return false;
        }
        if(password.getText().toString().equals("")) {
            toast("Empty password");
            return false;
        }
        return true;
    }

    private void openDashboard() {
        toast("Log in successful");
        Intent intent = new Intent(this, MainPanel.class);
        startActivity(intent);
    }
}
