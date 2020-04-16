package com.camoli.findmycoso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class RegistraDispositivo extends AppCompatActivity {

    private FloatingActionButton fabRegDevice;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private String UUID, manufacturerModel;
    private TextInputLayout nameDeviceLayout;
    private TextView UUIDdisplay;
    private Toolbar toolbar;
    private SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.getDarkModeState())
            setTheme(R.style.DarkMode);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_dispositivo);
        getSupportActionBar().setTitle(getString(R.string.registra_dispositivo));
        //getSupportActionBar().hide();
        //toolbar = findViewById(R.id.toolbar);
        //setActionBar(toolbar);
        UUIDdisplay = findViewById(R.id.uuidLabel);
        nameDeviceLayout = (TextInputLayout) findViewById(R.id.deviceNameLayout);

        manufacturerModel = getDeviceName();
        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);
        UUID = deviceUuidFactory.getDeviceUuid().toString().trim();

        if(manufacturerModel != null){
            nameDeviceLayout.getEditText().setText(manufacturerModel);
            if(UUID == null || UUID == "")
                UUID = manufacturerModel.replaceAll(" ", "").concat(""+System.currentTimeMillis());
        }

        if(UUID != null)
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("\n"+UUID));
        else
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("Errore."));

        fabRegDevice = findViewById(R.id.regDevice);
        fabRegDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = nameDeviceLayout.getEditText().getText().toString().trim();
                if(deviceName.equals("")){
                    nameDeviceLayout.setErrorEnabled(true);
                    nameDeviceLayout.setError("Il nome non può essere vuoto.");
                }
                else
                    nameDeviceLayout.setErrorEnabled(false);
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
