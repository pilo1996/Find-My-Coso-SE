package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Position> locationsList = new ArrayList<>();
    private TextView deviceName;
    private String selectedDeviceName;
    private LinearLayout linearLayout;

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
        deviceName = findViewById(R.id.selectedDevice);

        if(sharedPref.getSelectedDevice().getId().equals("error"))
            selectedDeviceName = sharedPref.getThisDevice().getName();
        else
            selectedDeviceName = sharedPref.getSelectedDevice().getName();
        deviceName.setText(selectedDeviceName);

        databaseReferenceDevices = FirebaseDatabase.getInstance().getReference("/locations/"+sharedPref.getThisDevice().getId());

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



        deviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                DeviceBottomSheetSelector bottomSheetSelector = new DeviceBottomSheetSelector(i);
                bottomSheetSelector.show(getSupportFragmentManager(),"Dialog");
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                //save location in the real-time database
                final Position position = new Position(
                        location.latitude+"",
                        location.longitude+"",
                        String.valueOf(System.currentTimeMillis()),
                        sharedPref.getThisDevice().getId(),
                        sharedPref.getThisDevice().getUuid()
                );
                databaseReferenceDevices.child(position.getDayTime()).setValue(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationsList.clear();
        databaseReferenceDevices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dev : dataSnapshot.getChildren() ) {
                    locationsList.add(dev.getValue(Position.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Errore imprevisto nel scaricare i dati delle ultime posizioni...");
            }
        });
        String temp = databaseReferenceDevices.push().getKey();
        databaseReferenceDevices.child(temp).setValue(null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(sharedPref.getDarkModeState())
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setMyLocationEnabled(true);
        fetchLastLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addPreviousLocations();
            }
        }, 3500);
    }

    private void addPreviousLocations() {
        for (Position temp : locationsList){
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongitude())));
            Date data = new Date();
            data.setTime(Long.parseLong(temp.getDayTime()));
            marker.title(data.toString());
            //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.registered_position_marker));
            mMap.addMarker(marker);
        }
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
