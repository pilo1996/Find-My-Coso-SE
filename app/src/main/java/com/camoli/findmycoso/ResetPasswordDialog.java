package com.camoli.findmycoso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordDialog extends AppCompatDialogFragment {

    private EditText emailText;
    private FirebaseAuth mAuth;
    private Button abort, send;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        emailText = view.findViewById(R.id.email_password_reset);
        mAuth = FirebaseAuth.getInstance();
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
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(getActivity().getApplicationContext(), "Mail di reset password inviata.", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity().getApplicationContext(), "Errore, controlla connessione e mail.", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    });
                }
            }
        });

        return builder.setView(view).create();
    }
}
