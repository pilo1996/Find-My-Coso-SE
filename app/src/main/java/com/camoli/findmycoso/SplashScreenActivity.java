package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 2500;
    private ImageView cfLogo;
    SharedPref sharedpref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
                    //decommentare per scopi di debug
                    //currentUser = null;
                    if(currentUser != null){
                        Toast.makeText(getApplicationContext(), "Accesso eseguito come\n"+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
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


        final ArgbEvaluator evaluator = new ArgbEvaluator();
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
                int newArancio = (int) evaluator.evaluate(fraction, arancione, blu);
                int newRosso = (int) evaluator.evaluate(fraction, rosso, arancione);
                int newViola = (int) evaluator.evaluate(fraction, viola, rosso);
                int newBlu = (int) evaluator.evaluate(fraction, blu, viola);
                int[] newArray = {newArancio, newRosso, newViola, newBlu};
                gradient.setColors(newArray);
            }
        });

        animator.start();
    }
}
