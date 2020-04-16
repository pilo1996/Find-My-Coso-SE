package com.camoli.findmycoso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private TextInputLayout layoutInputEmail, layoutInputPassword;
    private Button login, signup, resetPassword;
    private View background;
    private static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    private Intent i;
    private SharedPref sharedPref;
    private FirebaseAuth mAuth;
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
        signup = findViewById(R.id.signup_button);
        resetPassword = findViewById(R.id.reset_password);
        layoutInputEmail = findViewById(R.id.email_input_layout);
        layoutInputPassword = findViewById(R.id.password_input_layout);

        mAuth = FirebaseAuth.getInstance();

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
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if(mAuth.getCurrentUser().getDisplayName() == null || mAuth.getCurrentUser().getDisplayName().equals(""))
                                Toast.makeText(getApplicationContext(), "Accesso eseguito come\n"+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Bentornato, "+mAuth.getCurrentUser().getDisplayName()+"!", Toast.LENGTH_SHORT).show();
                            if(mAuth.getCurrentUser().isEmailVerified()){
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
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(getApplicationContext(), EmailValidation.class));
                                        finish();
                                    }
                                });
                            }
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            login.setText(R.string.label_login);
                            login.setEnabled(true);
                            layoutInputEmail.setError("Email o password invalidi.");
                            layoutInputPassword.setError("Riprova.");
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
}
