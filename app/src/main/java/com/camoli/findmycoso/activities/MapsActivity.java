package com.camoli.findmycoso.activities;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DeviceListResponse;
import com.camoli.findmycoso.api.PositionListResponse;
import com.camoli.findmycoso.api.PositionResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.Position;
import com.camoli.findmycoso.models.SharedPref;
import com.camoli.findmycoso.models.User;
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 1;
    private FloatingActionButton fabSettings, fabQr, fabAddDevice, fabHistory;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { //solo per api>=29
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
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
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
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
    };
    private SharedPref sharedPref;
    private LatLng location;
    private List<Position> locationsList = new ArrayList<>();
    private List<Device> devicesList = new ArrayList<>();
    private TextView deviceName;
    private String selectedDeviceName;
    private ProgressBar progressBarDB;
    private List<Device> deviceFavoritesList = new ArrayList<>();
    private DeviceBottomSheetSelector bottomSheetSelector;
    private Toolbar toolbar;

    private void stampaDevice(Device device){
        System.out.println("Device ID: "+device.getId());
        System.out.println("Device Name: "+device.getName());
        System.out.println("Device Owner: "+device.getOwnerID());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mappa:
                startActivity(new Intent(this, MapsActivity.class));
                finish();
                break;
            case R.id.settings:
                startActivity(new Intent(this, Impostazioni.class));
                finish();
                break;
            case R.id.registerDevice:
                startActivity(new Intent(this, RegistraDispositivo.class));
                finish();
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
                sharedPref.setCurrentUser(new User(-1));
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.getItem(0).setVisible(false);

        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
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
    protected void onStart() {
        super.onStart();
        retriveDevices();
        if(!sharedPref.getSelectedDevice().equals("error")){
            retriveLocations();
        }
    }

    private void retriveDevices(){
        //devicesList.clear();
        //riceve dati per i dispositivi registrati nell'account
        Call<DeviceListResponse> call = RetrofitClient.getInstance().getApi().getAllDevicesRegistered(sharedPref.getCurrentUser().getUserID());
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                devicesList = response.body().getDeviceList();
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        //deviceFavoritesList.clear();
        call = RetrofitClient.getInstance().getApi().getAllDevicesBookmarked(sharedPref.getCurrentUser().getUserID());
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                deviceFavoritesList = response.body().getDeviceList();
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void retriveLocations(){
        //locationsList.clear();
        Call<PositionListResponse> call = RetrofitClient.getInstance().getApi().getAllPositionsFromDeviceID(sharedPref.getSelectedDevice().getOwnerID(), sharedPref.getSelectedDevice().getId());
        call.enqueue(new Callback<PositionListResponse>() {
            @Override
            public void onResponse(Call<PositionListResponse> call, Response<PositionListResponse> response) {
                locationsList = response.body().getPositionList();
            }

            @Override
            public void onFailure(Call<PositionListResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
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
                    if(sharedPref.getSelectedDevice().getId() == -1){
                        sharedPref.setSelectedDevice(sharedPref.getThisDevice());
                    }
                    selectedDeviceName = sharedPref.getSelectedDevice().getName();
                    deviceName.setText(selectedDeviceName);
                }
                LottieAnimationView lottieAnimationView = findViewById(R.id.geo_loading);
                lottieAnimationView.cancelAnimation();
                lottieAnimationView.setVisibility(View.GONE);
                deviceName.setVisibility(View.VISIBLE);
                progressBarDB.setVisibility(View.INVISIBLE);
            }
        }, 700);

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
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        fabSettings = findViewById(R.id.settings);
        fabAddDevice = findViewById(R.id.addDevice);
        fabQr = findViewById(R.id.qr);
        deviceName = findViewById(R.id.selectedDevice);
        progressBarDB = findViewById(R.id.progressBarDB);
        fabHistory = findViewById(R.id.historyPositions);

        if(sharedPref.getCurrentUser().getUserID() == -1){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        /*
        System.out.println("******** ON CREATE inizio ***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());
        */

        if(!findThisDevice())
            deviceName.setText(R.string.registra_il_dispositivo);
        /*
        System.out.println("******** ON CREATE post thisDevice***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());
        */
        if(!deviceName.getText().equals(R.string.registra_il_dispositivo)){
            if(sharedPref.getSelectedDevice().getId() == -1){
                sharedPref.setSelectedDevice(sharedPref.getThisDevice());
            }
            selectedDeviceName = sharedPref.getSelectedDevice().getName();
            deviceName.setText(selectedDeviceName);
        }
        /*
        System.out.println("******** ON Create post selectedDevice ***********");
        System.out.println("Selected Device:\n");
        stampaDevice(sharedPref.getSelectedDevice());
        System.out.println("\nThis Device: \n");
        stampaDevice(sharedPref.getThisDevice());
        */
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
                if(sharedPref.getThisDevice().getId() == -1 || deviceName.getText().equals(R.string.registra_il_dispositivo)){
                    startActivity(new Intent(getApplicationContext(), RegistraDispositivo.class));
                }
                else {
                    retriveLocations();
                    PositionBottomSheetDialog bottomSheetSelector = new PositionBottomSheetDialog(MapsActivity.this, locationsList);
                    bottomSheetSelector.show(getSupportFragmentManager(),"Dialog");
                }
            }
        });

        deviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Device d : deviceFavoritesList){
                    if(!devicesList.contains(d))
                        devicesList.add(d);
                }
                if(devicesList.isEmpty() || deviceName.getText().equals(R.string.registra_il_dispositivo)){
                    startActivity(new Intent(getApplicationContext(), RegistraDispositivo.class));
                }
                else {
                    bottomSheetSelector = new DeviceBottomSheetSelector(devicesList, MapsActivity.this);
                    bottomSheetSelector.show(getSupportFragmentManager(),"Dialog");
                    deviceName.setText(sharedPref.getSelectedDevice().getName());
                }
                retriveDevices();
            }
        });

        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QRCodeActivity.class));
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean findThisDevice() {
        if(sharedPref.getThisDevice().getId() == -1){
            retriveDevices();
            for (Device d : devicesList){
                if(getDeviceName().equals(d.getName()) && sharedPref.getCurrentUser().getUserID() == d.getOwnerID()){
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

        if(sharedPref.getThisDevice().getId() != -1){
            Task<Location> task = fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful() && task.getResult() != null){
                        location = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

                        String timestamp = String.valueOf(System.currentTimeMillis());
                        //save location in the real-time database
                        final Position position = new Position(
                                sharedPref.getThisDevice().getId(),
                                sharedPref.getThisDevice().getOwnerID(),
                                resolveAddress(location.latitude, location.longitude),
                                location.latitude+"",
                                location.longitude+"",
                                timestamp,
                                resolveDate(timestamp)
                        );

                        Call<PositionResponse> call = RetrofitClient.getInstance().getApi().addPosition(
                                position.getDeviceID(), position.getUserID(), position.getAddress(), position.getLatitude(),
                                position.getLongitude(), position.getDayTime(), position.getDateTime() );
                        call.enqueue(new Callback<PositionResponse>() {
                            @Override
                            public void onResponse(Call<PositionResponse> call, Response<PositionResponse> response) {
                                locationsList.add(response.body().getPosition());
                            }

                            @Override
                            public void onFailure(Call<PositionResponse> call, Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    private String resolveDate(String timestamp) {
        Date date =  new Date();
        date.setTime(Long.parseLong(timestamp));
        return date.toString();
    }

    private void addPreviousLocations() {
        if (locationsList.isEmpty())
            return;
        for (Position temp : locationsList){
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongitude())));
            marker.title(temp.getDateTime());
            mMap.addMarker(marker);
        }
    }

    protected String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= 29)
            return REQUIRED_PERMISSIONS;
        else
            return REQUIRED_PERMISSIONS_OLD;
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

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
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

    @Override
    protected void onResume() {
        super.onResume();
        deviceName.setText(sharedPref.getSelectedDevice().getName());
    }

}
