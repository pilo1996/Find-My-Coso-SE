package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.android.material.snackbar.Snackbar.make;

public class RegistraDispositivo extends AppCompatActivity {

    private FloatingActionButton fabRegDevice;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private String UUID, manufacturerModel, userEmail;
    private TextInputLayout nameDeviceLayout;
    private TextView UUIDdisplay, registrationStatus;
    private Toolbar toolbar;
    private SharedPref sharedpref;
    private DatabaseReference databaseReferenceDevice;
    private List<Device> deviceList = new ArrayList<>();
    private View snackbarCoordinator;
    private ProgressBar waitingProgress;
    private FirebaseUser user;
    private String newID;

    @Override
    protected void onStart() {
        super.onStart();
        deviceList.clear();
        databaseReferenceDevice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dev : dataSnapshot.getChildren() ) {
                    deviceList.add(dev.getValue(Device.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Errore imprevisto nel scaricare i dati...");
            }
        });

        String temp = databaseReferenceDevice.child(user.getUid()).push().getKey();
        databaseReferenceDevice.child(temp).setValue(null);
    }

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
        nameDeviceLayout = findViewById(R.id.deviceNameLayout);
        snackbarCoordinator = findViewById(R.id.coordinatorSnackbar);
        fabRegDevice = findViewById(R.id.regDevice);
        waitingProgress = findViewById(R.id.waitingBar);
        registrationStatus = findViewById(R.id.registeredStatus);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        databaseReferenceDevice = FirebaseDatabase.getInstance().getReference("/users/"+user.getUid());

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
        }, 5000);

        if(manufacturerModel != null){
            nameDeviceLayout.getEditText().setText(manufacturerModel);
            if(UUID == null || UUID.equals(""))
                UUID = manufacturerModel.replaceAll(" ", "").concat(""+System.currentTimeMillis());
        }

        if(UUID != null)
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("\n"+UUID));
        else
            UUIDdisplay.setText(UUIDdisplay.getText().toString().concat("Errore."));

        fabRegDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingProgress.setVisibility(View.VISIBLE);
                String deviceName = nameDeviceLayout.getEditText().getText().toString().trim();
                if(deviceName.equals("")){
                    nameDeviceLayout.setErrorEnabled(true);
                    nameDeviceLayout.setError("Il nome non può essere vuoto.");
                }
                else
                    nameDeviceLayout.setErrorEnabled(false);

                if(deviceExistsInThisAccount()) {
                    registrationStatus.setText("è");
                    showSnackBarCustom("Dispositivo già registrato.", "#ffa500");
                }
                else {
                    DatabaseReference temp = databaseReferenceDevice.push();
                    newID = temp.getKey();
                    if(newID == null)
                        showSnackBarCustom("Errore imprevisto.", Color.RED+"");
                    else{
                        final Device device = new Device(UUID, deviceName, newID, userEmail, user.getUid());
                        databaseReferenceDevice.child(newID).setValue(device).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    registrationStatus.setText("è");
                                    showSnackBarCustom("Dispositivo aggiunto al tuo account.", "#2fd339");
                                }
                                else
                                    showSnackBarCustom("Errore imprevisto.", "#ff0000");
                            }
                        });
                        sharedpref.setThisDevice(device);
                        sharedpref.setSelectedDevice(device);
                    }
                }
                waitingProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean deviceExistsInThisAccount() {
        for (Device temp: deviceList){
            if (temp.getOwnerID().equals(user.getUid())){
                if (temp.getUuid().equals(UUID)){
                    sharedpref.setThisDevice(new Device(temp.getUuid(), temp.getName(), newID, temp.getuserEmail(), temp.getOwnerID()));
                    registrationStatus.setText("è");
                    return true;
                }
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
