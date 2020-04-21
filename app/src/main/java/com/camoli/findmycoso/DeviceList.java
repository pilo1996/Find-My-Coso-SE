package com.camoli.findmycoso;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DeviceList extends ArrayAdapter<Device> {

    private Activity context;
    private List<Device> devices;
    private int resource;
    private SharedPref sharedPref;
    private List<RadioButton> radioButtonsList;

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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewPositions = inflater.inflate(resource, null, true);

        final Device device = devices.get(position);

        final LinearLayout thisDevice = listViewPositions.findViewById(R.id.thisDevice);
        thisDevice.setVisibility(View.VISIBLE);

        final RadioButton radioButton = listViewPositions.findViewById(R.id.radioButton_selected);
        if(sharedPref.getSelectedDevice().getId().equals(device.getId())){
            radioButton.setChecked(true);
        }


        TextView textView = listViewPositions.findViewById(R.id.deviceName);
        textView.setText(device.getName());
        textView = listViewPositions.findViewById(R.id.deviceInfo);
        textView.setText(device.getId());

        if(!sharedPref.getThisDevice().getOwnerID().equals(device.getOwnerID())){
            textView = listViewPositions.findViewById(R.id.isYourDevice);
            textView.setText(R.string.dispositivo_aggiunto_da_qr);
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
        return listViewPositions;
    }

    public List<RadioButton> getRadioButtonsList(){
        return radioButtonsList;
    }
}
