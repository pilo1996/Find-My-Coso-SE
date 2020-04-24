package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Method;

public class HelpInfo extends FragmentActivity {

    private SharedPref sharedpref;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mappa:
                startActivity(new Intent(this, MapsActivity.class));
                finish();
                break;
            case R.id.registerDevice:
                startActivity(new Intent(this, RegistraDispositivo.class));
                finish();
                break;
            case R.id.qrCode:
                startActivity(new Intent(this, QRCodeActivity.class));
                finish();
                break;
            case R.id.account:
                startActivity(new Intent(this, UserProfile.class));
                finish();
                break;
            case R.id.helpInfo:
                startActivity(new Intent(this, HelpInfo.class));
                finish();
                break;
            case R.id.esci:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e("Menù bitch", "onMenuOpened", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);

        if(sharedpref.getDarkModeState())
            setTheme(R.style.DarkModeBackup);
        else
            setTheme(R.style.AppThemeBackup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }
}
