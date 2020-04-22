package com.camoli.findmycoso;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 1;
    private FloatingActionButton fabSettings, fabQr, fabAddDevice, fabHistory;
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
    private static final String[] REQUIRED_PERMISSIONS_OLD = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
    };
    private SharedPref sharedPref;
    private DatabaseReference databaseReferenceLocations;
    private DatabaseReference databaseReferenceDevices;
    private LatLng location;
    private List<Position> locationsList = new ArrayList<>();
    private List<Device> devicesList = new ArrayList<>();
    private TextView deviceName;
    private String selectedDeviceName;
    private ProgressBar progressBarDB;

    private void stampaDevice(Device device){
        System.out.println("Device ID: "+device.getId());
        System.out.println("Device Name: "+device.getName());
        System.out.println("Device Owner: "+device.getOwnerID());
    }

    @Override
    protected void onStart() {
        super.onStart();
        retriveDevices();
        if(!sharedPref.getSelectedDevice().equals("error")){
            retriveLocations();
        }
    }

    private void retriveDevices(){
        String temp;

        //riceve dati per i dispositivi registrati nell'account
        databaseReferenceDevices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                devicesList.clear();
                for (DataSnapshot dev : dataSnapshot.getChildren() ) {
                    devicesList.add(dev.getValue(Device.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Errore imprevisto nel scaricare i dati dei dispositivi registrati...");
            }
        });
        temp = databaseReferenceDevices.push().getKey();
        databaseReferenceDevices.child(temp).setValue(null);
    }

    private void retriveLocations(){
        String temp;

        //riceve dati per le posizioni del dispositivo associato all'account

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationsList.clear();
                for (DataSnapshot pos : dataSnapshot.getChildren() ) {
                    locationsList.add(pos.getValue(Position.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Errore imprevisto nel scaricare i dati delle ultime posizioni...");
            }
        });
        temp = databaseReferenceLocations.push().getKey();
        databaseReferenceLocations.child(temp).setValue(null);
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

                if(!findThisDevice())
                    deviceName.setText(R.string.registra_il_dispositivo);

                if(!deviceName.getText().equals(R.string.registra_il_dispositivo)){
                    if(sharedPref.getSelectedDevice().getId().equals("error")){
                        sharedPref.setSelectedDevice(sharedPref.getThisDevice());
                    }
                    selectedDeviceName = sharedPref.getSelectedDevice().getName();
                    databaseReferenceLocations = FirebaseDatabase.getInstance().getReference("/locations/"+sharedPref.getSelectedDevice().getId());
                    deviceName.setText(selectedDeviceName);
                }

                deviceName.setVisibility(View.VISIBLE);
                progressBarDB.setVisibility(View.INVISIBLE);
            }
        }, 3500);
    }

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
        progressBarDB = findViewById(R.id.progressBarDB);
        fabHistory = findViewById(R.id.historyPositions);

        if(FirebaseAuth.getInstance() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        System.out.println("******** ON CREATE inizio ***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());

        databaseReferenceDevices = FirebaseDatabase.getInstance().getReference("/users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(!findThisDevice())
            deviceName.setText(R.string.registra_il_dispositivo);

        System.out.println("******** ON CREATE post thisDevice***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());

        if(!deviceName.getText().equals(R.string.registra_il_dispositivo)){
            if(sharedPref.getSelectedDevice().getId().equals("error")){
                sharedPref.setSelectedDevice(sharedPref.getThisDevice());
            }
            selectedDeviceName = sharedPref.getSelectedDevice().getName();
            databaseReferenceLocations = FirebaseDatabase.getInstance().getReference("/locations/"+sharedPref.getSelectedDevice().getId());
            deviceName.setText(selectedDeviceName);
        }

        System.out.println("******** ON Create post selectedDevice ***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());

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

        fabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceName.getText().equals(R.string.registra_il_dispositivo)){
                    startActivity(new Intent(getApplicationContext(), RegistraDispositivo.class));
                }
                else {
                    PositionBottomSheetDialog bottomSheetSelector = new PositionBottomSheetDialog(MapsActivity.this, locationsList);
                    bottomSheetSelector.show(getSupportFragmentManager(),"Dialog");
                }
            }
        });

        deviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceName.getText().equals(R.string.registra_il_dispositivo)){
                    startActivity(new Intent(getApplicationContext(), RegistraDispositivo.class));
                }
                else {
                    DeviceBottomSheetSelector bottomSheetSelector = new DeviceBottomSheetSelector(devicesList, MapsActivity.this);
                    bottomSheetSelector.show(getSupportFragmentManager(),"Dialog");
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean findThisDevice() {
        if(sharedPref.getThisDevice().getId().equals("error")){
            for (Device d : devicesList){
                if(getDeviceName().equals(d.getName())){
                    sharedPref.setThisDevice(d);
                    return true;
                }
            }
            deviceName.setText(R.string.registra_il_dispositivo);
            return false;
        }
        return true;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
            return capitalize(model);
        else
            return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0)
            return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first))
            return s;
        else
            return Character.toUpperCase(first) + s.substring(1);
    }

    private String resolveAddress(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String indirizzoCompleto = "";
        if(addresses != null && addresses.size() > 0)
            indirizzoCompleto = addresses.get(0).getAddressLine(0);
        else
            indirizzoCompleto = latitude+", "+longitude;
        return indirizzoCompleto;
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
                if(task.isSuccessful()){
                    location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                    String timestamp = String.valueOf(System.currentTimeMillis());
                    //save location in the real-time database
                    final Position position = new Position(
                            location.latitude+"",
                            location.longitude+"",
                            timestamp,
                            sharedPref.getThisDevice().getId(),
                            sharedPref.getThisDevice().getUuid(),
                            sharedPref.getThisDevice().getOwnerID(),
                            resolveAddress(location.latitude, location.longitude),
                            resolveDate(timestamp)
                    );
                    databaseReferenceLocations.child(position.getDayTime()).setValue(position);
                    locationsList.add(position);
                }
            }
        });
    }

    private String resolveDate(String timestamp) {
        Date date =  new Date();
        date.setTime(Long.parseLong(timestamp));
        return date.toString();
    }

    private void addPreviousLocations() {
        if (locationsList.size() == 0)
            return;
        for (Position temp : locationsList){
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongitude())));
            marker.title(temp.getDateTime());
            mMap.addMarker(marker);
        }
    }

    protected String[] getRequiredPermissions() {
        //if (Build.VERSION.SDK_INT >= 29)
            return REQUIRED_PERMISSIONS;
        //else
        //    return REQUIRED_PERMISSIONS_OLD;
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
