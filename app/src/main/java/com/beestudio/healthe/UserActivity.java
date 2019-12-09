package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;
    public static int currentPosition = 1;
    private TextView tvCurrent, tvLength;
    FirebaseUser user;
    FirebaseFirestore db;
    UserFragment1 userFragment1;
    UserFragment2 userFragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userFragment1 = new UserFragment1();
        userFragment2 = new UserFragment2();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            setFragment();

        }

        tvCurrent = findViewById(R.id.layout_current);
        tvCurrent.setText(Integer.toString(currentPosition));
        tvLength = findViewById(R.id.layout_length);
        tvLength.setText(Integer.toString(NUM_PAGES));
    }

    private void setFragment() {

        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                if(document.get("nama") == null || document.get("no_hp") == null || document.get("profile_url") == null ) {
                                    userFragment1.setArguments(getIntent().getExtras());
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment_container, userFragment1).commit();
                                } else if(document.get("tb") == null || document.get("gender") == null || document.get("bb") == null || document.get("usia") == null) {
                                    userFragment2.setArguments(getIntent().getExtras());
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment_container, userFragment1).commit();
                                }
                            }
                        }
                    }
                });
    }
}
