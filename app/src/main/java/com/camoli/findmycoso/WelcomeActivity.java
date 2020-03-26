package com.camoli.findmycoso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.camoli.findmycoso.SplashScreenActivity.startAnimation;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager vp;
    private LinearLayout layoutDot;
    private TextView[] dotstv;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private MyPageAdapter mpa;
    private SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        sharedpref = new SharedPref(this);
        startAnimation(this);
        vp = findViewById(R.id.view_pager);
        layoutDot = findViewById(R.id.dotLayout);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        layouts = new int[]{R.layout.slide1, R.layout.slide2, R.layout.slide3, R.layout.slide4};
        setDotStatus(0);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpref.setNoFirstBoot();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = vp.getCurrentItem()+1;
                if(current == layouts.length){
                    sharedpref.setNoFirstBoot();
                    startActivity(new Intent(WelcomeActivity.this, Login.class));
                    finish();
                }
                else{
                    vp.setCurrentItem(current);
                }
            }
        });
        mpa = new MyPageAdapter(layouts, this);
        vp.setAdapter(mpa);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1){
                    btnNext.setText("Inizia");
                    btnSkip.setVisibility(View.GONE);
                }else{
                    btnNext.setText("Avanti");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setDotStatus(int page){
        layoutDot.removeAllViews();
        dotstv = new TextView[layouts.length];
        for (int i = 0; i < dotstv.length; i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226;"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            layoutDot.addView(dotstv[i]);
        }
        if(dotstv.length>0){
            dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }
    }


}
