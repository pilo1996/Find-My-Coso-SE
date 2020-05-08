package com.camoli.findmycoso.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.SharedPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailValidation extends AppCompatActivity {

    private Button goBackToLogin, resendEmail;
    private ProgressBar progressBar;
    private TextView emailAddress;
    private String email;
    private SharedPref sharedPref;

    @Override
    protected void onStart() {
        super.onStart();
        sendEmailValidation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_validation);
        getSupportActionBar().hide();
        sharedPref = new SharedPref(getApplicationContext());
        goBackToLogin = findViewById(R.id.BackToLogin);
        resendEmail = findViewById(R.id.resendEmail);
        progressBar = findViewById(R.id.resendProgressBar);
        emailAddress = findViewById(R.id.emailAddress);

        if(sharedPref.getCurrentUser().getUserID() == -1){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        email = sharedPref.getCurrentUser().getEmail();
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

                sendEmailValidation();

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

    private void sendEmailValidation() {
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().sendEmailValidation(email);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
