package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class UserProfile extends AppCompatActivity {

    private Intent i;
    private ImageView userPic;
    private Button saveBtn, goHomeBtn;
    private SharedPref sharedPref;
    private TextInputLayout nome_layout;
    private static final int CHOOSE_IMAGE = 101;
    private String localProfileImg;
    private StorageReference mStorageRef;
    private Bitmap bitmap;
    private ProgressBar saveProgressBar, imgProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference profileImgRef;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();
        i = getIntent();
        sharedPref = new SharedPref(this);
        userPic = findViewById(R.id.userPic);
        saveBtn = findViewById(R.id.saveButton);
        nome_layout = findViewById(R.id.name_input_layout);
        goHomeBtn = findViewById(R.id.goHome);
        saveProgressBar = findViewById(R.id.save_progress_bar);
        imgProgressBar = findViewById(R.id.imgProgress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(mAuth.getCurrentUser() != null){
            if (mAuth.getCurrentUser().getDisplayName() == null || mAuth.getCurrentUser().getDisplayName() == ""){
                goHomeBtn.setEnabled(false);
                goHomeBtn.setBackground(getDrawable(R.drawable.rounded_button_disabled));
            }
            else{
                nome_layout.getEditText().setText(mAuth.getCurrentUser().getDisplayName());
            }
            if(user.getPhotoUrl() != null)
                Glide.with(getApplicationContext()).load(user.getPhotoUrl().toString()).into(userPic);
        }
        else{
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressSave();
                saveUserInformation();
                hideProgressSave();
            }
        });

        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getDisplayName().equals(""))
                    sharedPref.setProfileUpdated();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn.setBackground(getDrawable(R.drawable.rounded_button_disabled));
                saveBtn.setEnabled(false);
                showImageChooser();
            }
        });
    }

    private void showProgressSave(){
        saveBtn.setVisibility(View.INVISIBLE);
        //saveBtn.setText("");
        //saveBtn.setBackground(getDrawable(R.drawable.rounded_button_disabled));
        saveProgressBar.setVisibility(View.VISIBLE);
        //saveBtn.setEnabled(false);
    }

    private void hideProgressSave(){
       // saveBtn.setEnabled(true);
        saveProgressBar.setVisibility(View.INVISIBLE);
        //saveBtn.setText(getString(R.string.salva));
        //saveBtn.setBackground(getDrawable(R.drawable.rounded_button));
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void goHomeResumeState(){
        goHomeBtn.setEnabled(true);
        goHomeBtn.setBackground(getDrawable(R.drawable.rounded_button_alt));
        sharedPref.setProfileUpdated();
    }

    private void saveUserInformation(){
        String nome_text = nome_layout.getEditText().getText().toString().trim();
        user = mAuth.getCurrentUser();
        if(nome_text.isEmpty()){
            //se viene modificato il nome precedente, o non è mai esistito, allora compare l'errore
            nome_layout.setError("Il nome è richiesto.");
            nome_layout.requestFocus();
            return;
        }
        if(user != null && localProfileImg != null){
            //aggiornamento sia della foto che del nome, se non viene cambiato il nome non cambia nulla!
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome_text).setPhotoUri(Uri.parse(localProfileImg)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Profilo aggiornato!", Toast.LENGTH_SHORT).show();
                        goHomeResumeState();
                    }else{
                        if(!task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Errore aggiornamento profilo utente.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //Caso se si vuole aggiornare solo il nome
            if(user == null)
                return;
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome_text).setPhotoUri(user.getPhotoUrl()).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Nome aggiornato!", Toast.LENGTH_SHORT).show();
                        goHomeResumeState();
                    }else{
                        if(!task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Errore aggiornamento nome utente.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void uploadToFirebaseStorage(){
        profileImgRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(localProfileImg != null){
            Toast.makeText(getApplicationContext(), "Upload immagine in corso...", Toast.LENGTH_LONG).show();
            profileImgRef.putFile(Uri.parse(localProfileImg)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        profileImgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                localProfileImg = task.getResult().toString();
                                Toast.makeText(getApplicationContext(), "Foto caricata", Toast.LENGTH_SHORT).show();
                                imgProgressBar.setVisibility(View.INVISIBLE);
                                saveBtn.setBackground(getDrawable(R.drawable.rounded_button));
                                saveBtn.setEnabled(true);
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Errore caricamento foto profilo al server.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            localProfileImg = data.getData().toString();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                userPic.setImageBitmap(bitmap);
                uploadToFirebaseStorage();
            } catch (IOException e) {
                System.out.println("Errore BITMAP");
            }
        }
    }

    private void showImageChooser(){
        imgProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Seleziona una immagine profilo"), CHOOSE_IMAGE);
    }
}
