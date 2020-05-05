package com.camoli.findmycoso.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.DeviceListResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.SharedPref;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQR extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private String[] components;
    private BottomSheetBehavior bottomSheetBehavior;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        if(sharedPref.getDarkModeState())
            setTheme(R.style.DarkMode);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        components = new String[5];
        //init the bottom sheet view
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_favorite_sheet));

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannerView.setFrameColor(Color.parseColor("#2fd339"));
                        components = result.getText().split("\\;");
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        processDialog(components[0], components[1], Integer.parseInt(components[2]), components[3], Integer.parseInt(components[4]));
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setFrameColor(Color.WHITE);
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void processDialog(final String uuid, final String deviceName, final int deviceID, final String ownerEmail, final int ownerID){
        final List<Device> dbDevices = new ArrayList<>();
        final LinearLayout theDevice = findViewById(R.id.theDevice);
        theDevice.setVisibility(View.VISIBLE);
        final LinearLayout favoriteButton = findViewById(R.id.addFavoriteButton);
        final TextView favoriteLabel = findViewById(R.id.addFavoriteLabel);
        final ImageView favoriteIcon = findViewById(R.id.favoriteIcon);
        final FloatingActionButton closeButton = findViewById(R.id.chiudi);

        TextView textView = findViewById(R.id.deviceName);
        textView.setText(deviceName);
        textView = findViewById(R.id.deviceID);
        textView.setText(deviceID);
        textView = findViewById(R.id.emailOwner);
        textView.setText(ownerEmail);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        if(sharedPref.getCurrentUser().getUserID() == ownerID){
            favoriteLabel.setText(R.string.gia_salvato);
            favoriteIcon.setImageResource(R.drawable.ic_check_);
            favoriteLabel.setTextColor(Color.parseColor("#303030"));
            favoriteButton.setEnabled(false);
        }
        else {
            final boolean[] trovato = {false};

            Call<DeviceListResponse> call = RetrofitClient.getInstance().getApi().getAllDevicesBookmarked(sharedPref.getCurrentUser().getUserID());
            call.enqueue(new Callback<DeviceListResponse>() {
                @Override
                public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                    if(!response.body().isError()){
                        dbDevices.clear();
                        dbDevices.addAll(response.body().getDeviceList());
                        for (Device dev : dbDevices){
                            if(dev.getId() == deviceID)
                                trovato[0] = true;
                        }
                    }else
                        System.out.println(response.body().getMessage());
                }

                @Override
                public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });

            if(trovato[0]){
                favoriteLabel.setText(R.string.gia_salvato);
                favoriteIcon.setImageResource(R.drawable.ic_check_);
                favoriteLabel.setTextColor(Color.parseColor("#303030"));
                favoriteButton.setEnabled(false);
            }
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteButton.isEnabled()){
                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().bookmarkDevice(sharedPref.getCurrentUser().getUserID(), deviceID);
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            if(!response.body().isError()){
                                //favoriteIcon.setImageResource(R.drawable.ic_favorite_full);
                                favoriteIcon.setVisibility(View.GONE);
                                LottieAnimationView heart = findViewById(R.id.heart_animation);
                                heart.setVisibility(View.VISIBLE);
                                heart.playAnimation();
                                favoriteLabel.setText(R.string.salvato);
                                favoriteLabel.setTextColor(Color.parseColor("#303030"));
                                favoriteLabel.setEnabled(false);
                            }
                            else
                                favoriteLabel.setText(response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                }
            }
        });

    }
}
