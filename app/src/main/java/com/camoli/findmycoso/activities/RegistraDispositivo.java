package com.camoli.findmycoso.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toolbar;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DeviceListResponse;
import com.camoli.findmycoso.api.DeviceResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.DeviceUuidFactory;
import com.camoli.findmycoso.models.SharedPref;
import com.camoli.findmycoso.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.material.snackbar.Snackbar.make;

public class RegistraDispositivo extends FragmentActivity {

    private FloatingActionButton fabRegDevice;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private String UUID, manufacturerModel, userEmail;
    private TextInputLayout nameDeviceLayout;
    private TextView UUIDdisplay, registrationStatus;
    private Toolbar toolbar;
    private SharedPref sharedpref;
    private List<Device> deviceList = new ArrayList<>();
    private View snackbarCoordinator;
    private ProgressBar waitingProgress;
    private String newID;
    private String deviceName;
    private User user;
    private Device device;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mappa:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, Impostazioni.class));
                break;
            case R.id.registerDevice:
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
                sharedpref.setCurrentUser(new User(-1));
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
        menu.getItem(2).setVisible(false);
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
    protected void onStart() {
        super.onStart();
        deviceList.clear();
        Call<DeviceListResponse> call = RetrofitClient.getInstance().getApi().getAllDevicesRegistered(sharedpref.getCurrentUser().getUserID());
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                if(call.isExecuted() && response.isSuccessful()) {
                    if (!response.body().isError()) {
                        deviceList.addAll(response.body().getDeviceList());
                    } else
                        System.out.println(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.getDarkModeState())
            setTheme(R.style.DarkModeBackup);
        else
            setTheme(R.style.AppThemeBackup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_dispositivo);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        UUIDdisplay = findViewById(R.id.uuidLabel);
        nameDeviceLayout = findViewById(R.id.deviceNameLayout);
        snackbarCoordinator = findViewById(R.id.coordinatorSnackbar);
        fabRegDevice = findViewById(R.id.regDevice);
        waitingProgress = findViewById(R.id.waitingBar);
        registrationStatus = findViewById(R.id.registeredStatus);

        if(sharedpref.getCurrentUser().getUserID() == -1){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        user = sharedpref.getCurrentUser();
        userEmail = user.getEmail();

        manufacturerModel = getDeviceName();
        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(this);
        UUID = deviceUuidFactory.getDeviceUuid().toString().trim();

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                fabRegDevice.setVisibility(View.VISIBLE);
                deviceExistsInThisAccount();
                waitingProgress.setVisibility(View.INVISIBLE);
            }
        }, 500);

        if(manufacturerModel != null){
            nameDeviceLayout.getEditText().setText(manufacturerModel);
            if(UUID == null || UUID.equals(""))
                UUID = manufacturerModel.replaceAll(" ", "").concat(""+System.currentTimeMillis());
        }

        if(UUID != null)
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("\n"+UUID));
        else
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("Errore."));


        deviceName = nameDeviceLayout.getEditText().getText().toString().trim();

        fabRegDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingProgress.setVisibility(View.VISIBLE);
                if(deviceName.equals("")){
                    nameDeviceLayout.setErrorEnabled(true);
                    nameDeviceLayout.setError("Il nome non può essere vuoto.");
                }
                else
                    nameDeviceLayout.setErrorEnabled(false);

                if(deviceExistsInThisAccount()) {
                    if(isSameUUID()){
                        registrationStatus.setText("è");
                        showSnackBarCustom("Dispositivo già registrato.", "#ffa500");
                    }
                    else {
                        registrationStatus.setText("è");
                        showSnackBarCustom("Dispositivo già registrato, tuttavia potrebbe non essere lo stesso. Cambiare il nome se si vuole registrarlo.", "#ffa500");
                    }
                }
                else {
                    device = new Device(UUID, deviceName, user.getUserID(), userEmail);
                    Call<DeviceResponse> call = RetrofitClient.getInstance().getApi().registerDevice(deviceName, UUID, user.getUserID());
                    call.enqueue(new Callback<DeviceResponse>() {
                        @Override
                        public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse> response) {
                            //if(call.isExecuted() && response.isSuccessful()) {
                                if(response.body().isError())
                                    showSnackBarCustom(response.body().getMessage(), "#ff0000");
                                else {
                                    registrationStatus.setText("è");
                                    showSnackBarCustom("Dispositivo aggiunto al tuo account.", "#2fd339");
                                    device = response.body().getDevice();
                                    sharedpref.setThisDevice(device);
                                    sharedpref.setSelectedDevice(device);
                                }
                            /*}
                            else
                                showSnackBarCustom("Errore imprevisto.", "#ff0000");*/
                        }

                        @Override
                        public void onFailure(Call<DeviceResponse> call, Throwable t) {
                            showSnackBarCustom("Errore imprevisto.", "#ff0000");
                        }
                    });
                }
                waitingProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean isSameUUID() {
        for (Device temp: deviceList){
            if (temp.getUuid().equals(UUID)){
                sharedpref.setThisDevice(temp);
                registrationStatus.setText("è");
                return true;
            }
        }
        return false;
    }

    private boolean deviceExistsInThisAccount() {
        for (Device temp: deviceList){
            if (temp.getName().equals(deviceName)){
                sharedpref.setThisDevice(temp);
                registrationStatus.setText("è");
                return true;
            }
        }
        return false;
    }

    public void showSnackBarCustom(String message, String color){
        Snackbar snackbar = Snackbar.make(snackbarCoordinator, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor(color));
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
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

}
