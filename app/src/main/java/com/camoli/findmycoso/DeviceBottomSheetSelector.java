package com.camoli.findmycoso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;


public class DeviceBottomSheetSelector extends BottomSheetDialogFragment {

    private ListView listViewDevices;
    private List<Device> deviceList;
    private DeviceList devices;
    private Activity c;

    public DeviceBottomSheetSelector(List<Device> deviceList, Activity c) {
            this.deviceList = deviceList;
            this.c = c;
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
