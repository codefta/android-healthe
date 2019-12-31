package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class ProfileAccountSettingActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText email, password;

    FirebaseUser user;
    FirebaseFirestore db;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_account_setting);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        btnSubmit = findViewById(R.id.btn_update_setting);

        if(TextUtils.equals(email.getText().toString(), currentEmail)) {
            btnSubmit.setBackground(getResources().getDrawable(R.drawable.bg_button_signup));
            btnSubmit.setEnabled(false);
        } else {
            btnSubmit.setBackground(getResources().getDrawable(R.drawable.bg_button_login));
            btnSubmit.setEnabled(true);
        }


        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(TextUtils.equals(email.getText().toString(), currentEmail)) {
                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.bg_button_signup));
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.bg_button_login));
                    btnSubmit.setEnabled(true);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAkun(email.getText().toString(), password.getText().toString());
            }
        });

        setSettingProfilView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAkun(String email, String password) {
        if(!TextUtils.isEmpty(email)) {
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileAccountSettingActivity.this, "Email Telah diubah", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    // show error toast ot user ,user already exist
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
                                    builder.setTitle("Login Gagal");
                                    builder.setMessage("Login Anda gagal, Akun sudah terdaftar.");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } catch (FirebaseNetworkException e) {
                                    //show error tost network exception
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
                                    builder.setTitle("Login Gagal");
                                    builder.setMessage("Tidak ada internet.");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } catch (Exception e) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
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

        if(!TextUtils.isEmpty(password)) {
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileAccountSettingActivity.this, "Password Telah diubah", Toast.LENGTH_LONG).show();
                            }else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    // show error toast ot user ,user already exist
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
                                    builder.setTitle("Login Gagal");
                                    builder.setMessage("Login Anda gagal, Akun sudah terdaftar.");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } catch (FirebaseNetworkException e) {
                                    //show error tost network exception
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
                                    builder.setTitle("Login Gagal");
                                    builder.setMessage("Tidak ada internet.");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } catch (Exception e) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAccountSettingActivity.this);
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

    }

    private void setSettingProfilView() {
        if(user != null) {
            for (UserInfo profile : user.getProviderData()) {
                email.setText(profile.getEmail());
                currentEmail = profile.getEmail();
            }
        }
    }
}
