package com.beestudio.healthe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity{


    private Button btnDaftar, btnExit;
    private EditText emailField, passwordField, passwordAgainField;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseUser userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.input_email);
        passwordField = findViewById(R.id.input_password);
        passwordAgainField = findViewById(R.id.input_password_again);
        btnDaftar = findViewById(R.id.btn_daftar);
        btnExit = findViewById(R.id.btn_exit_intro);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailField.getText().toString(), passwordField.getText().toString());
            }
        });

    }

    private void createAccount(String email, String password) {
        if(!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userSession = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Register Berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Pendaftaran Gagal", Toast.LENGTH_SHORT).show();
                            userSession = null;
                        }

                        hideProgressDialog();
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)) {
            emailField.setError("Silakan Masukan Email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
            if(TextUtils.isEmpty(password)) {
            passwordField.setError("Silakan Masukan Password");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        String passwordAgain = passwordAgainField.getText().toString();
        if(TextUtils.isEmpty(passwordAgain)) {
            passwordAgainField.setError("Silakan Masukan Password");
            valid = false;
        } else {
            passwordAgainField.setError(null);
        }

        if( TextUtils.equals(password, passwordAgain) ) {
            passwordAgainField.setError(null);
        } else {
            passwordAgainField.setError("Password Tidak Sama");
            valid = false;
        }

        return  valid;
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
    }

    private void hideProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
