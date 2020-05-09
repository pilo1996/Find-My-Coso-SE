package com.camoli.findmycoso.models;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.activities.MapsActivity;
import com.camoli.findmycoso.api.PositionResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Position;
import com.camoli.findmycoso.models.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.camoli.findmycoso.models.FMC.CHANNEL_ID;

public class UpdateBackgroundLocationService extends Service {

    public static final long DEFAULT_SYNC_INTERVAL = 120 * 1000;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng location;
    private SharedPref sharedPref;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = new SharedPref(getApplicationContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            fetchAndSavePosition();
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    private String resolveAddress(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String indirizzoCompleto = "";
        if(addresses != null && addresses.size() > 0)
            indirizzoCompleto = addresses.get(0).getAddressLine(0);
        else
            indirizzoCompleto = latitude+", "+longitude;
        return indirizzoCompleto;
    }

    private String resolveDate(String timestamp) {
        Date date =  new Date();
        date.setTime(Long.parseLong(timestamp));
        return date.toString();
    }

    private synchronized void fetchAndSavePosition() {
        if(sharedPref.getThisDevice().getId() != -1){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task1 -> {
                if(task1.isSuccessful() && task1.getResult() != null){
                    location = new LatLng(task1.getResult().getLatitude(), task1.getResult().getLongitude());
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    final Position position = new Position(
                            sharedPref.getThisDevice().getId(),
                            sharedPref.getThisDevice().getOwnerID(),
                            resolveAddress(location.latitude, location.longitude),
                            location.latitude+"",
                            location.longitude+"",
                            timestamp,
                            resolveDate(timestamp)
                    );
                    Call<PositionResponse> call = RetrofitClient.getInstance().getApi().addPosition(
                            position.getDeviceID(), position.getUserID(), position.getAddress(), position.getLatitude(),
                            position.getLongitude(), position.getDayTime(), position.getDateTime() );
                    call.enqueue(new Callback<PositionResponse>() {
                        @Override
                        public void onResponse(Call<PositionResponse> call, Response<PositionResponse> response) {
                            System.out.println(response.code());
                        }

                        @Override
                        public void onFailure(Call<PositionResponse> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Find My Coso - Tracciamento Posizione")
                .setContentText("Aggiornamento della posizione ogni "+DEFAULT_SYNC_INTERVAL/60000L+" minuti.")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        mHandler = new Handler();
        mHandler.post(runnableService);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
