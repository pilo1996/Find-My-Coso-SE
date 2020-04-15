package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EmailValidation extends AppCompatActivity {

    private Button goBackToLogin, resendEmail;
    private ProgressBar progressBar;
    private TextView emailAddress;
    private String email;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        else{
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_validation);
        getSupportActionBar().hide();

        goBackToLogin = findViewById(R.id.BackToLogin);
        resendEmail = findViewById(R.id.resendEmail);
        progressBar = findViewById(R.id.resendProgressBar);
        emailAddress = findViewById(R.id.emailAddress);

        emailAddress.setText(email);

        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendEmail.setText("");
                resendEmail.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resendEmail.setText(getString(R.string.non_hai_ricevuto_la_mail));
                        resendEmail.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }
}
