package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

public class ScanQR extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private String[] components;
    private BottomSheetBehavior bottomSheetBehavior;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        if(sharedPref.getDarkModeState())
            setTheme(R.style.DarkMode);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        components = new String[5];
        //init the bottom sheet view
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_favorite_sheet));

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannerView.setFrameColor(Color.parseColor("#2fd339"));
                        components = result.getText().split("\\;");
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        processDialog(components[0], components[1], components[2], components[3], components[4]);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setFrameColor(Color.WHITE);
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void processDialog(final String uuid, final String name, final String ID, final String email, final String ownerID){
        final LinearLayout theDevice = findViewById(R.id.theDevice);
        theDevice.setVisibility(View.VISIBLE);
        final LinearLayout favoriteButton = findViewById(R.id.addFavoriteButton);
        final TextView favoriteLabel = findViewById(R.id.addFavoriteLabel);
        final ImageView favoriteIcon = findViewById(R.id.favoriteIcon);
        final FloatingActionButton closeButton = findViewById(R.id.chiudi);
        final DatabaseReference favoriteDBR = FirebaseDatabase.getInstance().getReference("/users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/favorites/");;

        TextView textView = findViewById(R.id.deviceName);
        textView.setText(name);
        textView = findViewById(R.id.deviceID);
        textView.setText(ID);
        textView = findViewById(R.id.emailOwner);
        textView.setText(email);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ownerID)){
            favoriteLabel.setText(R.string.gia_salvato);
            favoriteIcon.setImageResource(R.drawable.ic_check_);
            favoriteLabel.setTextColor(Color.parseColor("#303030"));
            favoriteButton.setEnabled(false);
        }
        else {
            final boolean[] trovato = {false};
            favoriteDBR.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot id : dataSnapshot.getChildren()){
                        if(id.getValue(String.class).equals(ID))
                            trovato[0] = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(trovato[0]){
                favoriteLabel.setText(R.string.gia_salvato);
                favoriteIcon.setImageResource(R.drawable.ic_check_);
                favoriteLabel.setTextColor(Color.parseColor("#303030"));
                favoriteButton.setEnabled(false);
            }
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteButton.isEnabled()){
                    favoriteDBR.child(ID).setValue(new Device(uuid, name, ID, email, ownerID));
                    favoriteIcon.setImageResource(R.drawable.ic_favorite_full);
                    favoriteLabel.setText(R.string.salvato);
                    favoriteLabel.setTextColor(Color.parseColor("#303030"));
                    favoriteLabel.setEnabled(false);
                }
            }
        });

    }
}
