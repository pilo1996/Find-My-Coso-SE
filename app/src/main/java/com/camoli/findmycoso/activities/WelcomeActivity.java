package com.camoli.findmycoso.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.models.SharedPref;

import static com.camoli.findmycoso.activities.SplashScreenActivity.startAnimation;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager vp;
    private LinearLayout layoutDot;
    private TextView[] dotstv;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private MyPageAdapter mpa;
    private SharedPref sharedpref;
    private Button link;
    private View background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        startAnimation(this);
        background = findViewById(R.id.gradientPreloaderView);
        vp = findViewById(R.id.view_pager);
        layoutDot = findViewById(R.id.dotLayout);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        sharedpref = new SharedPref(getApplicationContext());
        layouts = new int[]{R.layout.slide1, R.layout.slide2, R.layout.slide3, R.layout.slide4};
        setDotStatus(0);

        if (savedInstanceState == null){
            background.setVisibility(View.INVISIBLE);
            final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

            if(viewTreeObserver.isAlive()){
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, Login.class);
                int dim[] = new int[2];
                btnSkip.getLocationInWindow(dim);
                i.putExtra("x", dim[0]+(btnSkip.getWidth()/2));
                i.putExtra("y", dim[1]+(btnSkip.getHeight()/2));
                sharedpref.setNoFirstBoot();
                startActivity(i);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = vp.getCurrentItem()+1;
                if(current == layouts.length){
                    Intent i = new Intent(WelcomeActivity.this, Login.class);
                    int dim[] = new int[2];
                    btnNext.getLocationInWindow(dim);
                    i.putExtra("x", dim[0]+(btnNext.getWidth()/2));
                    i.putExtra("y", dim[1]+(btnNext.getHeight()/2));
                    sharedpref.setNoFirstBoot();
                    startActivity(i);
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
                    link = findViewById(R.id.camoli_site);
                    btnNext.setText("Inizia");
                    btnSkip.setVisibility(View.GONE);
                    link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://camoli.ns0.it")));
                        }
                    });
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

    private void circularRevealActivity(){
        int cx = background.getWidth()/2;
        int cy = background.getHeight()/2;

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(background, cx, cy, 0, finalRadius);
        circularReveal.setDuration(800);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

}
