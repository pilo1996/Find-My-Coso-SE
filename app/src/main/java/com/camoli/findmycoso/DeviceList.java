package com.camoli.findmycoso;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        final View listViewDevices = inflater.inflate(resource, null, true);

        final Device device = devices.get(position);

        if(device.getId().equals("error"))
            return listViewDevices;

        final LinearLayout thisDevice = listViewDevices.findViewById(R.id.thisDevice);
        final LinearLayout thisEntireDevice = listViewDevices.findViewById(R.id.thisDeviceEntire);
        thisEntireDevice.setVisibility(View.VISIBLE);

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
        }

        deleteSingleDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference item = FirebaseDatabase.getInstance().getReference("/devices/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/favorites/");
                item.child(device.getId()).setValue(null);
                devices.remove(position);
            }
        });

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
        return listViewDevices;
    }

    public List<RadioButton> getRadioButtonsList(){
        return radioButtonsList;
    }

}
