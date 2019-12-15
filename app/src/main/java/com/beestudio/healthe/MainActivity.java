package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
//        checkDataDiri();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkDataDiri();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    openFragment(HomeFragment.newInstance("",""));
                    return true;
                case R.id.nav_makanan:
                    openFragment(MakananFragment.newInstance("",""));
                    return true;
                case R.id.nav_kalkulator:
                    openFragment(KalkulatorFragment.newInstance("",""));
                    return true;
//                case R.id.nav_reminder:
//                    openFragment(ReminderFragment.newInstance("",""));
//                    return true;
                case R.id.nav_profile:
                    openFragment(ProfileFragment.newInstance("",""));
                    return true;
            }
            return false;
        }
    };

//    private void checkDataDiri() {

//        db.collection("users").document(user.getUid())
//        .get()
//        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(!task.isSuccessful()) {
//                }
//            }
//        });
//
//
//        if(docRef == null) {
//        }

//        DocumentReference docRef = (DocumentReference) db.collection("users").document(user.getUid());

//        db.collection("users").whereEqualTo("userId", user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if(TextUtils.isEmpty(document.get("userId").toString())) {
//                                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
//                                    startActivity(intent);
//                                }
//                            }
//                        } else {
//
//                        }
//                    }
//                });

//        if(TextUtils.isEmpty(docRef.toString())) {
//                Intent intent = new Intent(MainActivity.this, UserActivity.class);
//                startActivity(intent);
//
//        }
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//
//                    }
//                }
//            }
//        });

//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//
//                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";
//
//                if(TextUtils.isEmpty(snapshot.get("nama").toString())) {
//
//                } else {
//                    Toast.makeText(MainActivity.this, "Data Kosong", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

}
