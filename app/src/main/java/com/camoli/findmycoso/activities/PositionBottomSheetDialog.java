package com.camoli.findmycoso.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Position;
import com.camoli.findmycoso.models.SharedPref;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().deleteAllPositionsByDevice(sharedPref.getSelectedDevice().getId(), sharedPref.getCurrentUser().getUserID());
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(!response.body().isError())
                            dismiss();
                        else
                            System.out.println(response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
            }
        });
        recyclerView = v.findViewById(R.id.recyclerViewPositions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(c.getApplicationContext()));
        recyclerView.setAdapter(adapter);
        return v;
    }
}
