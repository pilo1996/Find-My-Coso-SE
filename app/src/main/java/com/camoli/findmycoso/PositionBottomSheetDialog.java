package com.camoli.findmycoso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class PositionBottomSheetDialog extends BottomSheetDialogFragment {


    private List<Position> positionList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Activity c;
    private LinearLayout deleteAll, close;
    private SharedPref sharedPref;

    public PositionBottomSheetDialog(Activity c, List<Position> positionList) {
            this.positionList = positionList;
            this.c = c;
            sharedPref = new SharedPref(c.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_history_dialog_, container,false);
        adapter = new MyPositionAdapter(c, positionList);
        deleteAll = v.findViewById(R.id.deleteAll);
        TextView deviceSelected = (TextView) v.findViewById(R.id.deviceNameSelected);
        deviceSelected.setText(sharedPref.getSelectedDevice().getName());
        close = v.findViewById(R.id.chiudi);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference aux = FirebaseDatabase.getInstance().getReference("/locations/");
                aux.child(sharedPref.getSelectedDevice().getId()).setValue(null);
                dismiss();
            }
        });
        recyclerView = v.findViewById(R.id.recyclerViewPositions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(c.getApplicationContext()));
        recyclerView.setAdapter(adapter);
        return v;
    }
}
