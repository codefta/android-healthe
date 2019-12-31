package com.beestudio.healthe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class RegisterActivity extends AppCompatActivity{

    private Button btnDaftar, btnExit;
    private EditText emailField, passwordField, passwordAgainField;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

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

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                            startActivity(intent);
                            progressDialog.hide();
                        } else {
                            progressDialog.hide();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // show error toast ot user ,user already exist
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Pendaftaran Gagal");
                                builder.setMessage("Pendaftaran Anda gagal, Akun sudah terdaftar.");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } catch (FirebaseNetworkException e) {
                                //show error tost network exception
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Pendaftaran Gagal");
                                builder.setMessage("Tidak ada internet.");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } catch (Exception e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Pendaftaran Gagal");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }

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
}
