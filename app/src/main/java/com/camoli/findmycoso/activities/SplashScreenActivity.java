package com.camoli.findmycoso.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import android.Manifest;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.api.LoginResponse;
import com.camoli.findmycoso.models.SharedPref;
import com.camoli.findmycoso.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 2500;
    private ImageView cfLogo;
    SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED){
            if(!isNetworkAvailable()){
                Toast.makeText(this, "Nessuna connessione ad internet!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else{
            Toast.makeText(this, "Sono richiesti permessi necessari.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
            finish();
            startActivity(new Intent(this, SplashScreenActivity.class));
        }

        sharedpref = new SharedPref(this);

        startAnimation(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedpref.isFirstBoot()){
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                    finish();
                }
                else{
                    final User currentUser = sharedpref.getCurrentUser();
                    if(currentUser.getUserID() != -1){
                        //l'utente è salvato, ha fatto un accesso correttamente e non ha mai fatto logout
                        Call<LoginResponse> call = RetrofitClient.getInstance().getApi().userlogin(currentUser.getEmail(), currentUser.getPlainPassword());
                        call.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                if(!response.body().isError()){
                                    sharedpref.setCurrentUser(response.body().getUser());
                                    Toast.makeText(getApplicationContext(), "Bentornato, "+currentUser.getNome()+"!", Toast.LENGTH_SHORT).show();
                                    if(currentUser.getValidated()){
                                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                        finish();
                                    }
                                    else {
                                        //TODO send email verification
                                        startActivity(new Intent(getApplicationContext(), EmailValidation.class));
                                        finish();
                                    }
                                }else{
                                    sharedpref.setCurrentUser(new User(-1));
                                    startActivity(new Intent(SplashScreenActivity.this, Login.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                sharedpref.setCurrentUser(new User(-1));
                                startActivity(new Intent(SplashScreenActivity.this, Login.class));
                                finish();
                            }
                        });

                    }else {
                        //l'utente non è salvato: non ha mai acceduto o ha fatto logout
                        startActivity(new Intent(SplashScreenActivity.this, Login.class));
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void startAnimation(final Activity activity) {
        final int arancione = Color.parseColor("#fa7e1e");
        final int rosso = Color.parseColor("#d62976");
        final int viola = Color.parseColor("#962fbf");
        final int blu = Color.parseColor("#4f5bd5");

        @SuppressLint("RestrictedApi") final ArgbEvaluator evaluator = new ArgbEvaluator();
        View preloader = activity.findViewById(R.id.gradientPreloaderView);
        final GradientDrawable gradient;
        if(android.os.Build.VERSION.SDK_INT >= 29){
            gradient = (GradientDrawable) preloader.getBackground();
        }else{
            gradient = new GradientDrawable();
        }

        ValueAnimator animator = TimeAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(SPLASH_TIME_OUT);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float fraction = valueAnimator.getAnimatedFraction();
                @SuppressLint("RestrictedApi") int newArancio = (int) evaluator.evaluate(fraction, arancione, blu);
                @SuppressLint("RestrictedApi") int newRosso = (int) evaluator.evaluate(fraction, rosso, arancione);
                @SuppressLint("RestrictedApi") int newViola = (int) evaluator.evaluate(fraction, viola, rosso);
                @SuppressLint("RestrictedApi") int newBlu = (int) evaluator.evaluate(fraction, blu, viola);
                int[] newArray = {newArancio, newRosso, newViola, newBlu};
                gradient.setColors(newArray);
            }
        });

        animator.start();
    }
}
