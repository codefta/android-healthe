package com.beestudio.healthe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountSettingActivity extends AppCompatActivity {

    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        btnExit = findViewById(R.id.btn_exit_daftar);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingActivity.this, ProfileFragment.class);
                startActivity(intent);
            }
        });
    }
}
