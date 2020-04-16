package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Impostazioni extends AppCompatActivity {

    private Switch darkMode;
    private Button getPermissionsBtn, logOutBtn, updateProfileBtn;
    SharedPref sharedpref;
    private Object mMap;

    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpref = new SharedPref(this);

        if(sharedpref.getDarkModeState())
            setTheme(R.style.DarkMode);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getPermissionsBtn = findViewById(R.id.get_permissions);
        getPermissionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
                    if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
                        if (Build.VERSION.SDK_INT >= 23)
                            requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
                    }
                }
            }
        });

        darkMode = findViewById(R.id.switchDarkMode);
        if(sharedpref.getDarkModeState())
            darkMode.setChecked(true);
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedpref.setDarkModeState(true);
                }
                else{
                    sharedpref.setDarkModeState(false);
                }
                refresh();
            }
        });

        updateProfileBtn = findViewById(R.id.update_profile);
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                finish();
            }
        });

        logOutBtn = findViewById(R.id.logOut);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), Login.class);
                int[] dim = new int[2];
                logOutBtn.getLocationInWindow(dim);
                i.putExtra("x", dim[0]+(logOutBtn.getWidth()/2));
                i.putExtra("y", dim[1]+(logOutBtn.getHeight()/2));
                startActivity(i);
                finish();
            }
        });

    }

    public void refresh(){
        Intent i = new Intent(getApplicationContext(), Impostazioni.class);
        startActivity(i);
        finish();
    }

    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Called when the user has accepted (or denied) our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Mancano permessi!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
