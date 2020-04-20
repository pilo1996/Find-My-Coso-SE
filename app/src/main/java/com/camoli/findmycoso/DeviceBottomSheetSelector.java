package com.camoli.findmycoso;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class DeviceBottomSheetSelector extends BottomSheetDialogFragment {

    private SharedPref sharedPref;
    private Intent i;

    public DeviceBottomSheetSelector(Intent i) {
            this.i = i;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.select_device_sheet_dialogue, container,false);

        TextView temp = v.findViewById(R.id.deviceName);

        sharedPref = new SharedPref(getActivity().getApplicationContext());

        temp.setText(sharedPref.getThisDevice().getName());
        temp = v.findViewById(R.id.deviceInfo);
        temp.setText(sharedPref.getThisDevice().getId());
        return v;
    }
}
