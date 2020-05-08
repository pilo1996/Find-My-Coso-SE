package com.camoli.findmycoso.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.camoli.findmycoso.R;
import com.camoli.findmycoso.api.DefaultResponse;
import com.camoli.findmycoso.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordDialog extends AppCompatDialogFragment {

    private EditText emailText;
    private Button abort, send;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        emailText = view.findViewById(R.id.email_password_reset);
        abort = view.findViewById(R.id.abort);
        send = view.findViewById(R.id.send);

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                if(!email.equals("")){
                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().resetPassword(email);
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            Toast.makeText(getActivity().getApplicationContext(), "Controlla la tua mail.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                    dismiss();
                }
            }
        });

        return builder.setView(view).create();
    }
}
