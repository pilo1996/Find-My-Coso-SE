package com.camoli.findmycoso.activities;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.SharedPref;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

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

        if(position < devices.size()){
            final Device device = devices.get(position);

            if(device.getId() == -1)
                return listViewDevices;

            /*
            ref = FirebaseDatabase.getInstance().getReference("users");
            idQueryQR = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favorites").orderByChild("id").equalTo(device.getId());
            idQueryReg = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("id").equalTo(device.getId());
            */

            final LinearLayout thisDevice = listViewDevices.findViewById(R.id.thisDevice);

            listViewDevices.findViewById(R.id.thisDeviceEntire).setVisibility(View.VISIBLE);

            final ImageButton deleteSingleDevice = listViewDevices.findViewById(R.id.delete_single_device);

            final RadioButton radioButton = listViewDevices.findViewById(R.id.radioButton_selected);
            if(sharedPref.getSelectedDevice().getId() == device.getId()){
                radioButton.setChecked(true);
            }

            TextView textView = listViewDevices.findViewById(R.id.deviceName);
            textView.setText(device.getName());
            textView = listViewDevices.findViewById(R.id.deviceInfo);
            textView.setText(device.getId());

            if(sharedPref.getCurrentUser().getUserID() != device.getOwnerID()){
                textView = listViewDevices.findViewById(R.id.isYourDevice);
                textView.setText(R.string.dispositivo_aggiunto_da_qr);
                deleteSingleDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().removeBookmarkedDevice(device.getId(), sharedPref.getCurrentUser().getUserID());
                        call.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                if(!response.body().isError()){
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });
                    }
                });
                if(sharedPref.getCurrentUser().getSelectedDeviceID() == device.getId()){
                    //avevamo selezionato quello appena eliminato
                    if(sharedPref.getThisDevice().getId() == -1)
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
                        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().removeDeviceRegistered(device.getId());
                        call.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                if(!response.body().isError()){
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });
                    }
                });
                if(device.getId() == sharedPref.getThisDevice().getId()){
                    //se è il dispositivo corrente ad esser stato eliminato
                    if(sharedPref.getSelectedDevice().getId() == -1)
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
