package com.camoli.findmycoso;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;

public class SharedPref {

    SharedPreferences myPreferences;

    public SharedPref(Context context){
        myPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    //Metodo che salva lo stato della night mode: true o false
    public void setDarkModeState(Boolean state){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean("DarkMode", state);
        editor.commit();
    }

    public Boolean getDarkModeState(){
        return myPreferences.getBoolean("DarkMode", false);
    }

    //Metodo che salva lo stato della night mode: true o false
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
        String id = myPreferences.getString("idDeviceDatabase", "error");
        String email = myPreferences.getString("emailDevice", "error");
        if (uuid.equals("error") || name.equals("error") || id.equals("error") || email.equals("error"))
            return thisOne;
        else{
            thisOne = new Device(uuid, name, id, email);
            return thisOne;
        }
    }

    public void setThisDevice(Device thisOne){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UUID", thisOne.getUuid());
        editor.putString("deviceName", thisOne.getName());
        editor.putString("idDeviceDatabase", thisOne.getId());
        editor.putString("emailDevice", thisOne.getuserEmail());
        editor.commit();
    }
}
