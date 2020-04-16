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
}
