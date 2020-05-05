package com.camoli.findmycoso.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.SharedPref;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;


public class DeviceBottomSheetSelector extends BottomSheetDialogFragment {

    private ListView listViewDevices;
    private List<Device> deviceList;
    private DeviceList devices;
    private Activity c;
    private SharedPref sharedPref;

    public DeviceBottomSheetSelector(List<Device> deviceList, Activity c) {
            this.deviceList = deviceList;
            this.c = c;
            sharedPref = new SharedPref(c.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_device_sheet_dialogue, container,false);
        devices = new DeviceList(c, R.layout.select_device_sheet_dialogue, deviceList);
        listViewDevices = v.findViewById(R.id.listViewDevices);
        listViewDevices.setAdapter(devices);
        return v;
    }
}
