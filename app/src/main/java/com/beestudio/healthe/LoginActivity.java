package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailField;
    EditText passwordField;

    Button buttonLogin, btnExit;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        emailField = findViewById(R.id.input_email);
        passwordField = findViewById(R.id.input_password);
        buttonLogin = findViewById(R.id.button_masuk);
        btnExit = findViewById(R.id.btn_exit_intro);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailField.getText().toString(), passwordField.getText().toString());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void login(String email, String pw) {

        if(!validateForm()) {
            return;
        }

        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth.signInWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.hide();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            progressDialog.hide();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // show error toast ot user ,user already exist
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Login Gagal");
                                builder.setMessage("Login Anda gagal, Akun sudah terdaftar.");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } catch (FirebaseNetworkException e) {
                                //show error tost network exception
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Login Gagal");
                                builder.setMessage("Tidak ada internet.");
                                builder.setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } catch (Exception e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Login Gagal");
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

        return  valid;
    }
}
