package com.camoli.findmycoso.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Position;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPositionAdapter extends RecyclerView.Adapter<MyPositionAdapter.ViewHolder> {


    private Context context;
    private List<Position> positionList;

    public MyPositionAdapter(Context context, List<Position> positionList) {
        this.context = context;
        this.positionList = positionList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView via, dayTime;
        private ImageButton imgSingleDelete;
        private LinearLayout goToMaps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            via = itemView.findViewById(R.id.street);
            dayTime = itemView.findViewById(R.id.dateTime);
            imgSingleDelete = itemView.findViewById(R.id.img_single_delete);
            goToMaps = itemView.findViewById(R.id.position_i);
        }
    }

    @NonNull
    @Override
    public MyPositionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_positions, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPositionAdapter.ViewHolder holder, final int position) {
        if(position < positionList.size()){
            final Position temp = positionList.get(position);

            holder.via.setText(temp.getAddress());
            holder.dayTime.setText(temp.getDateTime());
            holder.imgSingleDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().deleteSinglePositionByID(temp.getPositionID());
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            if (!response.body().isError()){
                                positionList.remove(position);
                                notifyItemRemoved(position);
                            }else
                                System.out.println(response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                }
            });

            holder.goToMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+Double.parseDouble(temp.getLatitude())+","+Double.parseDouble(temp.getLongitude())+"?q="+Uri.encode(temp.getAddress()))).setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return positionList.size();
    }
}
