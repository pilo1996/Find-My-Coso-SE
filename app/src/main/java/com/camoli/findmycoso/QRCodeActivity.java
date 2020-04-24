package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.lang.reflect.Method;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;


public class QRCodeActivity extends FragmentActivity {

    private static final String TAG = "QR Exception";
    private SharedPref sharedpref;
    private ImageView qrCodeDevice;
    private Bitmap bitmap;
    private boolean generated;
    private String data, savePath = Environment.getDataDirectory().getPath() + "/QRCode/";;
    private CoordinatorLayout snackbarCoordinator;
    private Button scanButton;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mappa:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, Impostazioni.class));
                break;
            case R.id.registerDevice:
                startActivity(new Intent(this, RegistraDispositivo.class));
                finish();
                break;
            case R.id.qrCode:
                break;
            case R.id.account:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.helpInfo:
                startActivity(new Intent(this, HelpInfo.class));
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
        menu.getItem(3).setVisible(false);
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
        setContentView(R.layout.activity_qr_code_generator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        qrCodeDevice = findViewById(R.id.thisDeviceQRCode);
        snackbarCoordinator = findViewById(R.id.coordinatorSnackbar);
        scanButton = findViewById(R.id.scanQR);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanQR.class));
            }
        });
        data = encodeQRData();
        if(data.equals(""))
            showSnackBarCustom("Errore rilevazione dispositivo.", "#ff0000");
        else
            generated = generateQR();
        if(generated){
            qrCodeDevice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(saveQRinGallery()){
                        showSnackBarCustom("Codice QR salvato nella galleria.", "#2fd339");
                        System.out.println(savePath);
                        return true;
                    }
                    else {
                        showSnackBarCustom("Errore salvataggio codice QR nella galleria.", "#ff0000");
                        return false;
                    }
                }
            });
        }
        else{
            showSnackBarCustom("Errore creazione codice QR.", "#ff0000");
        }
    }

    private String encodeQRData() {
        Device thisDevice = sharedpref.getThisDevice();
        if(thisDevice == null || thisDevice.getId().equals("error"))
            return "";
        System.out.println("UUID QRCodeActivity: "+thisDevice.getUuid());
        return thisDevice.getUuid()+";"+thisDevice.getName()+";"+thisDevice.getId()+";"+thisDevice.getuserEmail()+";"+thisDevice.getOwnerID()+";";
    }

    private boolean saveQRinGallery(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File direct = this.getExternalFilesDir("QR");

            String fileName = sharedpref.getThisDevice().getId();

            if (!direct.exists()) {
                File qrDirectory = new File(direct.getPath());
                qrDirectory.mkdirs();
            }
            File file = new File(direct.getPath(), fileName);
            if (file.exists()) {
                file.delete();
            }
            try {   // Save with location, value, bitmap returned and type of Image(JPG/PNG).
                    QRGSaver qrgSaver = new QRGSaver();
                    qrgSaver.save(direct.getPath(), fileName, bitmap, QRGContents.ImageType.IMAGE_JPEG);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            showSnackBarCustom("Mancano i permessi per accedere alla galleria.", "#ffa500");
            return false;
        }
        return true;
    }

    private boolean generateQR(){
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);

        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrCodeDevice.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
            return false;
        }

        return true;
    }

    public void showSnackBarCustom(String message, String color){
        Snackbar snackbar = Snackbar.make(snackbarCoordinator, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor(color));
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
