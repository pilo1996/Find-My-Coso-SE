package com.camoli.findmycoso.activities;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.RetrofitClient;
import com.camoli.findmycoso.models.Device;
import com.camoli.findmycoso.api.LoginResponse;
import com.camoli.findmycoso.models.SharedPref;
import com.camoli.findmycoso.models.User;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION, //solo per api>=29
    };
    private static final String[] REQUIRED_PERMISSIONS_OLD = new String[] { //api <= 28
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private TextInputLayout layoutInputEmail, layoutInputPassword;
    private Button login, signup, resetPassword;
    private View background;
    private Intent i;
    private SharedPref sharedPref;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        i = getIntent();
        background = findViewById(R.id.contenitore);
        progressBar = findViewById(R.id.progress_bar);
        login = findViewById(R.id.login_button);
        login.setEnabled(false);
        login.setBackground(getDrawable(R.drawable.rounded_button_disabled));
        sharedPref = new SharedPref(getApplicationContext());
        sharedPref.setThisDevice(new Device());
        sharedPref.setSelectedDevice(new Device());
        signup = findViewById(R.id.signup_button);
        resetPassword = findViewById(R.id.reset_password);
        layoutInputEmail = findViewById(R.id.email_input_layout);
        layoutInputPassword = findViewById(R.id.password_input_layout);

        if (savedInstanceState == null){
            background.setVisibility(View.INVISIBLE);
            final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

            if(viewTreeObserver.isAlive()){
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

        if(!sharedPref.permitsAlreadyObtained()){
            if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
                if (!hasPermissions(getApplicationContext(), getRequiredPermissions())) {
                    if (Build.VERSION.SDK_INT >= 23){
                        requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
                    }
                }
            }
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Signup.class);
                int dim[] = new int[2];
                signup.getLocationInWindow(dim);
                i.putExtra("x", dim[0]+(signup.getWidth()/2));
                i.putExtra("y", dim[1]+(signup.getHeight()/2));
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!login.isEnabled())
                    return;
                String email = layoutInputEmail.getEditText().getText().toString().trim().toLowerCase();
                String password = layoutInputPassword.getEditText().getText().toString().trim();
                login.setText("");
                login.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                Call<LoginResponse> call = RetrofitClient.getInstance().getApi().userlogin(email, password);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(!response.body().isError()){
                            sharedPref.savePlainPassword(password);
                            sharedPref.setCurrentUser(response.body().getUser());
                            Toast.makeText(getApplicationContext(), "Bentornato, "+sharedPref.getCurrentUser().getNome()+"!", Toast.LENGTH_SHORT).show();

                            System.out.println(sharedPref.getCurrentUser().getValidated());
                            System.out.println(sharedPref.getCurrentUser().getEmail());
                            System.out.println(sharedPref.getCurrentUser().getUserID());

                            if(sharedPref.getCurrentUser().getValidated() == 1){
                                if(!sharedPref.isProfileUpdated()){
                                    startActivity(new Intent(getApplicationContext(), UserProfile.class));
                                    finish();
                                }
                                else {
                                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                    finish();
                                }
                            }
                            else {
                                //TODO send email verification
                                startActivity(new Intent(getApplicationContext(), EmailValidation.class));
                                finish();
                            }
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            login.setText(R.string.label_login);
                            login.setEnabled(true);
                            layoutInputEmail.setError(response.body().getMessage());
                            layoutInputPassword.setError("Riprova.");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResetDialog();
            }
        });

        layoutInputEmail.getEditText().addTextChangedListener(loginTextWatcher);
        layoutInputPassword.getEditText().addTextChangedListener(loginTextWatcher);

    }

    private void openResetDialog() {
        ResetPasswordDialog reset = new ResetPasswordDialog();
        reset.show(getSupportFragmentManager(), "reset password dialog");
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = layoutInputEmail.getEditText().getText().toString().trim().toLowerCase();
            String passwordInput = layoutInputPassword.getEditText().getText().toString().trim();
            login.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
            if(login.isEnabled())
                login.setBackground(getDrawable(R.drawable.rounded_button));
            else
                login.setBackground(getDrawable(R.drawable.rounded_button_disabled));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean checkEmail(){
        String emailInput = layoutInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            layoutInputEmail.setError("La email non può essere vuota");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            layoutInputEmail.setError("Inserire un indirizzo email valido");
            return false;
        } else {
            layoutInputEmail.setError(null);
            return true;
        }
    }

    private void confirmInput() {
        if (!checkEmail())
            return;
        String input = "Email: " + layoutInputEmail.getEditText().getText().toString();
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

    private void circularRevealActivity(){

        int cx = i.getIntExtra("x", 0);
        int cy = i.getIntExtra("y", 0);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(background, cx, cy, 0, finalRadius);
        circularReveal.setDuration(800);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private int getDips(int dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, getResources().getDisplayMetrics());
    }

    protected String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= 29)
            return REQUIRED_PERMISSIONS;
        else
            return REQUIRED_PERMISSIONS_OLD;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Called when the user has accepted (or denied) our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Mancano permessi!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            sharedPref.setPermissionsAsObtained();
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
