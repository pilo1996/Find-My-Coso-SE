package com.camoli.findmycoso;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DeviceList extends ArrayAdapter<Device> {

    private Activity context;
    private List<Device> devices;
    private int resource;
    private SharedPref sharedPref;
    private List<RadioButton> radioButtonsList;
    DatabaseReference ref;
    Query idQueryQR, idQueryReg;

    //bisogna sempre passargli in resoruce R.layout.select_device_sheet_dialogue
    public DeviceList(@NonNull Activity context, int resource, @NonNull List<Device> objects) {
        super(context, resource, objects);
        this.context = context;
        devices = objects;
        this.resource = resource;
        sharedPref = new SharedPref(context);
        radioButtonsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        final View listViewDevices = inflater.inflate(resource, null, true);

        if(position < devices.size()){
            final Device device = devices.get(position);

            if(device.getId().equals("error"))
                return listViewDevices;

            ref = FirebaseDatabase.getInstance().getReference("users");
            idQueryQR = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favorites").orderByChild("id").equalTo(device.getId());
            idQueryReg = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("id").equalTo(device.getId());

            final LinearLayout thisDevice = listViewDevices.findViewById(R.id.thisDevice);

            listViewDevices.findViewById(R.id.thisDeviceEntire).setVisibility(View.VISIBLE);

            final ImageButton deleteSingleDevice = listViewDevices.findViewById(R.id.delete_single_device);

            final RadioButton radioButton = listViewDevices.findViewById(R.id.radioButton_selected);
            if(sharedPref.getSelectedDevice().getId().equals(device.getId())){
                radioButton.setChecked(true);
            }

            TextView textView = listViewDevices.findViewById(R.id.deviceName);
            textView.setText(device.getName());
            textView = listViewDevices.findViewById(R.id.deviceInfo);
            textView.setText(device.getId());

            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(device.getOwnerID())){
                textView = listViewDevices.findViewById(R.id.isYourDevice);
                textView.setText(R.string.dispositivo_aggiunto_da_qr);
                deleteSingleDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        idQueryQR.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot device: dataSnapshot.getChildren()) {
                                    device.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                        notifyDataSetChanged();
                    }
                });
                if(sharedPref.getSelectedDevice().getId().equals(device.getId())){
                    //avevamo selezionato quello appena eliminato
                    if(sharedPref.getThisDevice().getId().equals("error"))
                        sharedPref.setSelectedDevice(new Device()); //non esiste nemmeno il mio attutale, butto in error
                    else
                        sharedPref.setSelectedDevice(sharedPref.getThisDevice()); //seleziono quello attuale
                }
            }
            else {
                //siamo in un dispositivo registrato manualmente nell'account
                deleteSingleDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        idQueryReg.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot device: dataSnapshot.getChildren()) {
                                    device.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                        notifyDataSetChanged();
                    }
                });
                if(device.getName().equals(sharedPref.getThisDevice().getName()) && device.getId().equals(sharedPref.getThisDevice().getId())){
                    //se è il dispositivo corrente ad esser stato eliminato
                    if(sharedPref.getSelectedDevice().getId().equals("error"))
                        sharedPref.setThisDevice(new Device()); //setto in error
                    //altrimento lascio quello che c'è già
                }
            }

            thisDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RadioButton prev_rb: radioButtonsList){
                        if(prev_rb != radioButton)
                            prev_rb.setChecked(false);
                    }
                    radioButton.setChecked(true);
                    sharedPref.setSelectedDevice(device);
                }
            });

            radioButtonsList.add(radioButton);
        }
        return listViewDevices;
    }
}
