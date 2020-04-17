package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 2500;
    private ImageView cfLogo;
    SharedPref sharedpref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        sharedpref = new SharedPref(this);
        mAuth = FirebaseAuth.getInstance();

        startAnimation(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedpref.isFirstBoot()){
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                    finish();
                }
                else{
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if(currentUser != null){
                        if(mAuth.getCurrentUser().getDisplayName() == null || mAuth.getCurrentUser().getDisplayName().equals("")){
                            Toast.makeText(getApplicationContext(), "Accesso eseguito come\n"+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Bentornato, "+mAuth.getCurrentUser().getDisplayName()+"!", Toast.LENGTH_SHORT).show();
                        }
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            finish();
                        }
                        else {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startActivity(new Intent(getApplicationContext(), EmailValidation.class));
                                    finish();
                                }
                            });
                        }
                    }else {
                        startActivity(new Intent(SplashScreenActivity.this, Login.class));
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT);
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
