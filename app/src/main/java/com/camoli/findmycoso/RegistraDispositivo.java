package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.material.snackbar.Snackbar.make;

public class RegistraDispositivo extends AppCompatActivity {

    private FloatingActionButton fabRegDevice;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private String UUID, manufacturerModel;
    private TextInputLayout nameDeviceLayout;
    private TextView UUIDdisplay;
    private Toolbar toolbar;
    private SharedPref sharedpref;
    private DatabaseReference databaseReferenceDevice;

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

        databaseReferenceDevice = FirebaseDatabase.getInstance().getReference("devices");

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
                //TODO controllare se è già presente un dispositivo con uguale UUID in questo account (uguale email).
                String id = databaseReferenceDevice.push().getKey();
                if(id == null)
                    showSnackBarFailure("Errore imprevisto.");
                else{
                    Device device = new Device(UUID, deviceName, id, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    databaseReferenceDevice.child(id).setValue(device).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                showSnackBarSuccess("Dispositivo aggiunto al tuo account.");
                            else
                                showSnackBarFailure("Errore imprevisto.");
                        }
                    });
                }
            }
        });
    }

    public void showSnackBarSuccess(String message){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#2fd339"));
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showSnackBarFailure(String message){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#2fd339"));
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
