package com.camoli.findmycoso;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPositionAdapter extends RecyclerView.Adapter<MyPositionAdapter.ViewHolder> {


    private Context context;
    private List<Position> positionList;

    public MyPositionAdapter(Context context, List<Position> positionList) {
        this.context = context;
        this.positionList = positionList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView via, dayTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            via = itemView.findViewById(R.id.street);
            dayTime = itemView.findViewById(R.id.dateTime);
        }
    }

    @NonNull
    @Override
    public MyPositionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_positions, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPositionAdapter.ViewHolder holder, int position) {
        Position temp = positionList.get(position);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongitude()), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String indirizzoCompleto;

        if(addresses != null && addresses.size() > 0)
            indirizzoCompleto = addresses.get(0).getAddressLine(0);
        else
            indirizzoCompleto = temp.getLatitude()+", "+temp.getLongitude();

        Date data = new Date();
        data.setTime(Long.parseLong(temp.getDayTime()));

        holder.via.setText(indirizzoCompleto);
        holder.dayTime.setText(data.toString());
    }

    @Override
    public int getItemCount() {
        return positionList.size();
    }
}
