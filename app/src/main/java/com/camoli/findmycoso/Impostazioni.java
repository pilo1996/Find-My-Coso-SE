package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Method;

public class Impostazioni extends FragmentActivity {

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
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION, //solo per api>=29
            Manifest.permission.CAMERA,
    };
    private static final String[] REQUIRED_PERMISSIONS_OLD = new String[] { //api <= 28
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mappa:
                startActivity(new Intent(this, MapsActivity.class));
                finish();
                break;
            case R.id.settings:
                break;
            case R.id.registerDevice:
                startActivity(new Intent(this, RegistraDispositivo.class));
                break;
            case R.id.qrCode:
                startActivity(new Intent(this, QRCodeActivity.class));
                break;
            case R.id.account:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.helpInfo:
                startActivity(new Intent(this, HelpInfo.class));
                break;
            case R.id.esci:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), Login.class);
                int[] dim = new int[2];
                logOutBtn.getLocationInWindow(dim);
                i.putExtra("x", dim[0]+(logOutBtn.getWidth()/2));
                i.putExtra("y", dim[1]+(logOutBtn.getHeight()/2));
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(1).setVisible(false);
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e("Menù bitch", "onMenuOpened", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpref = new SharedPref(this);

        if(sharedpref.getDarkModeState())
            setTheme(R.style.DarkModeBackup);
        else
            setTheme(R.style.AppThemeBackup);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
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
        if (Build.VERSION.SDK_INT >= 29)
            return REQUIRED_PERMISSIONS;
        else
            return REQUIRED_PERMISSIONS_OLD;
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
                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
