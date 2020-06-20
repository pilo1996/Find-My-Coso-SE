package com.camoli.findmycoso.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.camoli.findmycoso.models.SharedPref;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Signup extends AppCompatActivity {

    private Button confirmSignup, turnBackToLogin;
    private TextInputLayout layoutInputEmail, layoutInputPassword, layoutInputRetypedPassword;
    private Intent i;
    private View background;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    private static final Pattern EMAIL_ADDRESS =
            Pattern.compile(
                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
            );

    private SharedPref sharedPref;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkModeFull);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        i = getIntent();
        background = findViewById(R.id.contenitore);

        sharedPref = new SharedPref(getApplicationContext());

        progressBar = findViewById(R.id.progress_bar);

        confirmSignup = findViewById(R.id.confirm_signup_button);
        confirmSignup.setEnabled(false);
        confirmSignup.setBackground(getDrawable(R.drawable.rounded_button_disabled));

        turnBackToLogin = findViewById(R.id.turn_back_login);
        layoutInputEmail = findViewById(R.id.email_input_layout);
        layoutInputPassword = findViewById(R.id.password_input_layout);
        layoutInputRetypedPassword = findViewById(R.id.retype_input_password);

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

        layoutInputEmail.getEditText().addTextChangedListener(loginTextWatcher);
        layoutInputPassword.getEditText().addTextChangedListener(loginTextWatcher);

        turnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                int dim[] = new int[2];
                turnBackToLogin.getLocationInWindow(dim);
                i.putExtra("x", dim[0]+(turnBackToLogin.getWidth()/2));
                i.putExtra("y", dim[1]+(turnBackToLogin.getHeight()/2));
                startActivity(i);
                finish();
            }
        });

        confirmSignup.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                if(!confirmInput())
                    return;
                String email = layoutInputEmail.getEditText().getText().toString().trim();
                String password = layoutInputPassword.getEditText().getText().toString().trim();
                confirmSignup.setText("");
                confirmSignup.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = RetrofitClient.getInstance().getApi().createUser(email, email.substring(0, email.indexOf("@")), password);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        System.out.println("LOL "+response.code());
                        switch (response.code()){
                            case 201: //registrazione andata a buon fine
                                Toast.makeText(getApplicationContext(), "Registrato con successo!", Toast.LENGTH_SHORT).show();
                                int dim[] = new int[2];
                                turnBackToLogin.getLocationInWindow(dim);
                                i.putExtra("x", dim[0]+(turnBackToLogin.getWidth()/2));
                                i.putExtra("y", dim[1]+(turnBackToLogin.getHeight()/2));
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                break;
                            case 422: //email già registrata
                                progressBar.setVisibility(View.INVISIBLE);
                                confirmSignup.setText(R.string.conferma_registrazione);
                                confirmSignup.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Email già registrata.", Toast.LENGTH_SHORT).show();
                                break;
                            case 423: //errore a caso
                                progressBar.setVisibility(View.INVISIBLE);
                                confirmSignup.setText(R.string.conferma_registrazione);
                                confirmSignup.setEnabled(true);
                                try {
                                    Toast.makeText(getApplicationContext(), "Impossibile registrarsi. ("+response.errorBody().string()+")", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            }
        });

}

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = layoutInputEmail.getEditText().getText().toString().trim().toLowerCase();
            String passwordInput = layoutInputPassword.getEditText().getText().toString().trim();
            confirmSignup.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
            if(confirmSignup.isEnabled())
                confirmSignup.setBackground(getDrawable(R.drawable.rounded_button_alt));
            else
                confirmSignup.setBackground(getDrawable(R.drawable.rounded_button_disabled));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean checkPassword() {
        String passwordInput = layoutInputPassword.getEditText().getText().toString().trim();
        String passwordRetypedInput = layoutInputRetypedPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            layoutInputPassword.setError("La password non può essere vuota");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            layoutInputPassword.setError("La password deve contenere almeno 6 caratteri, almeno una maiuscola, almeno una minuscola e almeno un carattere speciale (@#$%^&+=)");
            return false;
        } else {
            if(passwordInput.equals(passwordRetypedInput)){
                layoutInputPassword.setError(null);
                return true;
            }
            else {
                layoutInputRetypedPassword.setError("Le password non coincidono");
                return false;
            }
        }
    }

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

    private boolean confirmInput() {
        return (checkEmail() && checkPassword());
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
