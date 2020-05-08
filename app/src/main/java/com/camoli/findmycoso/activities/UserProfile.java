package com.camoli.findmycoso.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.api.LoginResponse;
import com.camoli.findmycoso.api.UploadResponse;
import com.camoli.findmycoso.models.SharedPref;
import com.camoli.findmycoso.models.User;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity {

    private Intent i;
    private ImageView userPic;
    private Button saveBtn, goHomeBtn;
    private SharedPref sharedPref;
    private TextInputLayout nome_layout;
    private static final int CHOOSE_IMAGE = 101;
    private String localProfileImg;
    private Bitmap bitmap;
    private ProgressBar saveProgressBar, imgProgressBar;
    private User currentUser;
    private Uri selectedImg;

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

        currentUser = sharedPref.getCurrentUser();

        if(currentUser.getUserID() != -1){
            nome_layout.getEditText().setText(currentUser.getNome());
            if(currentUser.getProfile_pic() != null && !currentUser.getProfile_pic().equals("") && !currentUser.getProfile_pic().equals("error")){
                imgProgressBar.setVisibility(View.INVISIBLE);
                Glide.with(getApplicationContext()).load(currentUser.getProfile_pic()).into(userPic);
                done();
            }
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
                done();
            }
        });

        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.getUserID() != -1)
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

    private void done(){
        LottieAnimationView lottieAnimationView = findViewById(R.id.done_animation);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
    }

    private void goHomeResumeState(){
        done();
        goHomeBtn.setEnabled(true);
        goHomeBtn.setBackground(getDrawable(R.drawable.rounded_button_alt));
        sharedPref.setProfileUpdated();
    }

    private void saveUserInformation(){
        String nome_text = nome_layout.getEditText().getText().toString().trim();
        if(nome_text.isEmpty()){
            //se viene modificato il nome precedente, o non è mai esistito, allora compare l'errore
            nome_layout.setError("Il nome è richiesto.");
            nome_layout.requestFocus();
            return;
        }
        if(currentUser.getUserID() != -1 && localProfileImg != null && !currentUser.getProfile_pic().equals("") && !currentUser.getProfile_pic().equals("error")){
            if(updateUserName(nome_text)) {
                if (uploadToServer()) {
                    Toast.makeText(getApplicationContext(), "Profilo aggiornato!", Toast.LENGTH_SHORT).show();
                    goHomeResumeState();
                    return;
                }
            }
        }
        else{
            updateUserName(nome_text);
        }
    }

    private boolean updateUserName(String nome_text){
        final boolean[] res = {false};
        //Caso se si vuole aggiornare solo il nome
        if(currentUser.getUserID() == -1)
            return res[0];
        Call<LoginResponse> call = RetrofitClient.getInstance().getApi().updateUser(currentUser.getUserID(), nome_text, currentUser.getProfile_pic());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(!response.body().isError()){
                    sharedPref.setCurrentUser(response.body().getUser());
                    Toast.makeText(getApplicationContext(), "Nome aggiornato!", Toast.LENGTH_SHORT).show();
                    goHomeResumeState();
                    res[0] = true;
                }else {
                    Toast.makeText(getApplicationContext(), "Errore aggiornamento nome utente.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return res[0];
    }

    private boolean uploadToServer(){
        final boolean[] res = {false};
        if(localProfileImg != null || !localProfileImg.isEmpty()){
            Toast.makeText(getApplicationContext(), "Upload immagine in corso...", Toast.LENGTH_LONG).show();

            localProfileImg = getPath(getApplicationContext(), selectedImg);
            System.out.println("path: "+localProfileImg);
            File file = new File(localProfileImg);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part img = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            RequestBody userID = RequestBody.create(MediaType.parse("text/plain"), ""+sharedPref.getCurrentUser().getUserID());

            Call<UploadResponse> call = RetrofitClient.getInstance().getApi().uploadProfilePic(img, userID);
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    System.out.println("Codice errore upload Photo: "+response.message());
                    if(response.body().isError())
                        Toast.makeText(getApplicationContext(), "Errore caricamento foto profilo al server.", Toast.LENGTH_SHORT).show();
                    else {
                        localProfileImg = response.body().getUrl();
                        sharedPref.updateProfile(currentUser, localProfileImg, currentUser.getNome());
                        Toast.makeText(getApplicationContext(), "Foto caricata", Toast.LENGTH_SHORT).show();
                        done();
                        imgProgressBar.setVisibility(View.INVISIBLE);
                        saveBtn.setBackground(getDrawable(R.drawable.rounded_button));
                        saveBtn.setEnabled(true);
                        res[0] = true;
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    t.printStackTrace();
                    System.out.println("Callback fallita uploadPhoto: "+t.getMessage());
                }
            });
        }
        return res[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            localProfileImg = data.getData().toString();
            selectedImg = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                userPic.setImageBitmap(bitmap);
                uploadToServer();
            } catch (IOException e) {
                System.out.println("Errore BITMAP");
            }
        }else {
            imgProgressBar.setVisibility(View.INVISIBLE);
            Glide.with(getApplicationContext()).load(currentUser.getProfile_pic()).into(userPic);
            saveBtn.setBackground(getDrawable(R.drawable.rounded_button));
            saveBtn.setEnabled(true);
            done();
        }
    }

    private void showImageChooser(){
        imgProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Seleziona una immagine profilo"), CHOOSE_IMAGE);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
