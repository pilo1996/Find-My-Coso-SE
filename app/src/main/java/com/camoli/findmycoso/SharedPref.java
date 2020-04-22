package com.camoli.findmycoso;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

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
        String owner = myPreferences.getString("ownerDevice", "error");
        if (uuid.equals("error") || name.equals("error") || id.equals("error") || email.equals("error") || owner.equals("error"))
            return thisOne;
        else{
            thisOne = new Device(uuid, name, id, email, owner);
            return thisOne;
        }
    }

    public void setThisDevice(Device thisOne){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UUID", thisOne.getUuid());
        editor.putString("deviceName", thisOne.getName());
        editor.putString("idDeviceDatabase", thisOne.getId());
        editor.putString("emailDevice", thisOne.getuserEmail());
        editor.putString("ownerDevice", thisOne.getOwnerID());
        editor.commit();
    }

    public void setSelectedDevice(Device selectedDevice){
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("selectedDevice-UUID", selectedDevice.getUuid());
        editor.putString("selectedDevice-deviceName", selectedDevice.getName());
        editor.putString("selectedDevice-idDeviceDatabase", selectedDevice.getId());
        editor.putString("selectedDevice-emailDevice", selectedDevice.getuserEmail());
        editor.putString("selectedDevice-ownerDevice", selectedDevice.getOwnerID());
        editor.commit();
    }

    public Device getSelectedDevice(){
        Device selectedDevice = new Device();
        String uuid = myPreferences.getString("selectedDevice-UUID", "error");
        String name = myPreferences.getString("selectedDevice-deviceName", "error");
        String id = myPreferences.getString("selectedDevice-idDeviceDatabase", "error");
        String email = myPreferences.getString("selectedDevice-emailDevice", "error");
        String owner = myPreferences.getString("selectedDevice-ownerDevice", "error");
        if (uuid.equals("error") || name.equals("error") || id.equals("error") || email.equals("error") || owner.equals("error"))
            return selectedDevice;
        else{
            selectedDevice = new Device(uuid, name, id, email, owner);
            return selectedDevice;
        }
    }

}
