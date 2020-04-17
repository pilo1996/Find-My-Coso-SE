package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 1;
    private FloatingActionButton fabSettings, fabQr, fabAddDevice;
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
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };
    private SharedPref sharedPref;
    private DatabaseReference databaseReferenceDevices;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if(sharedPref.getDarkModeState())
            setTheme(R.style.DarkModeBackup);
        else
            setTheme(R.style.AppThemeBackup);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fabSettings = findViewById(R.id.settings);
        fabAddDevice = findViewById(R.id.addDevice);
        fabQr = findViewById(R.id.qr);

        System.out.println("id: "+sharedPref.getThisDevice().getId());

        databaseReferenceDevices = FirebaseDatabase.getInstance().getReference(sharedPref.getThisDevice().getId());

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Impostazioni.class));
            }
        });

        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistraDispositivo.class));
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchLastLocation() {
        if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
            if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
                if (Build.VERSION.SDK_INT >= 23)
                    requestPermissions(getRequiredPermissions(), REQUEST_CODE);
            }
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                mMap.addMarker(new MarkerOptions().position(location).title("Posizione Attuale"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                System.out.println("************entra qui********************");
                //final PositionDevice posDev = new PositionDevice(sharedPref.getThisDevice(), location[0], String.valueOf(System.currentTimeMillis()));
                final Position position = new Position(
                        location.latitude+"",
                        location.longitude+"",
                        String.valueOf(System.currentTimeMillis()),
                        sharedPref.getThisDevice().getId(),
                        sharedPref.getThisDevice().getUuid()
                );
                databaseReferenceDevices.setValue(position);
                //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            }
        });
            }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchLastLocation();
    }

    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
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
        if (requestCode == REQUEST_CODE) {
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
