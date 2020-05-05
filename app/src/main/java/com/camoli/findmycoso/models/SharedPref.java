package com.camoli.findmycoso.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SharedPref {

    SharedPreferences myPreferences;

    public SharedPref(Context context){
        myPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public User getCurrentUser(){
        int id = myPreferences.getInt("userID", -1);
        String nome = myPreferences.getString("userName", "error");
        String email = myPreferences.getString("userEmail", "error");
        String plainPassword = myPreferences.getString("userPassword", "error");
        String profile_pic = myPreferences.getString("userPic", "error");
        Boolean isValidated = myPreferences.getBoolean("userValidated", false);
        int selectedDeviceID = myPreferences.getInt("userSelectedDeviceID", -1);
        return new User(id, nome, email, plainPassword, profile_pic, isValidated, selectedDeviceID);
    }

    public void setCurrentUser(User user){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putInt("userID", user.getUserID());
        editor.putString("userName", user.getNome());
        editor.putString("userEmail", user.getEmail());
        editor.putString("userPassword", user.getPlainPassword());
        editor.putString("userPic", user.getProfile_pic());
        editor.putBoolean("userValidated", user.getValidated());
        editor.putInt("userSelectedDeviceID", user.getSelectedDeviceID());
        editor.commit();
    }

    public void setDarkModeState(Boolean state){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean("DarkMode", state);
        editor.commit();
    }

    public Boolean getDarkModeState(){
        return myPreferences.getBoolean("DarkMode", false);
    }

    public void setNoFirstBoot(){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean("FirstBoot", false);
        editor.commit();
    }

    public Boolean isFirstBoot(){
        return myPreferences.getBoolean("FirstBoot", true);
    }

    public Boolean isProfileUpdated() {
        return myPreferences.getBoolean("profileUpdated", false);
    }

    public void setProfileUpdated(){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean("profileUpdated", true);
        editor.commit();
    }

    public Boolean permitsAlreadyObtained(){
        return myPreferences.getBoolean("permissionsObtained", false);
    }

    public void setPermissionsAsObtained(){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean("permissionsObtained", true);
        editor.commit();
    }

    public Device getThisDevice(){
        Device thisOne = new Device();
        String uuid = myPreferences.getString("UUID", "error");
        String name = myPreferences.getString("deviceName", "error");
        String email = myPreferences.getString("ownerEmail", "error");
        int id = myPreferences.getInt("idDeviceDatabase", -1);
        int owner = myPreferences.getInt("ownerDevice", -1);
        if (uuid.equals("error") || name.equals("error") || id == -1 || owner == -1 || email.equals("error"))
            return thisOne;
        else{
            thisOne = new Device(uuid, name, id, owner, email);
            return thisOne;
        }
    }

    public void setThisDevice(Device thisOne){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UUID", thisOne.getUuid());
        editor.putString("deviceName", thisOne.getName());
        editor.putString("ownerEmail", thisOne.getEmail());
        editor.putInt("idDeviceDatabase", thisOne.getId());
        editor.putInt("ownerDevice", thisOne.getOwnerID());
        editor.commit();
    }

    public void setSelectedDevice(Device selectedDevice){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("selectedDevice-UUID", selectedDevice.getUuid());
        editor.putString("selectedDevice-deviceName", selectedDevice.getName());
        editor.putString("selectedDevice-ownerEmail", selectedDevice.getEmail());
        editor.putInt("selectedDevice-idDeviceDatabase", selectedDevice.getId());
        editor.putInt("selectedDevice-ownerDevice", selectedDevice.getOwnerID());
        editor.commit();
        int userID = myPreferences.getInt("userID", -1);
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().updateSelectedDevice(userID, selectedDevice.getId());
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if(response.body().isError())
                    System.out.println(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public Device getSelectedDevice(){
        Device selectedDevice = new Device();
        String uuid = myPreferences.getString("selectedDevice-UUID", "error");
        String name = myPreferences.getString("selectedDevice-deviceName", "error");
        String email = myPreferences.getString("selectedDevice-ownerEmail", "error");
        int id = myPreferences.getInt("selectedDevice-idDeviceDatabase", -1);
        int owner = myPreferences.getInt("selectedDevice-ownerDevice", -1);
        if (uuid.equals("error") || name.equals("error") || id == -1 ||  owner == -1 || email.equals("error"))
            return selectedDevice;
        else{
            selectedDevice = new Device(uuid, name, id, owner, email);
            return selectedDevice;
        }
    }
}
