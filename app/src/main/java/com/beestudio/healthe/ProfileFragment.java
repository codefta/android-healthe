package com.beestudio.healthe;


import android.content.Intent;

import android.os.Build;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser user;
    FirebaseFirestore db;

    ImageView profileFoto;
    TextView profileNama, profileGender, profileHeight, profileWeight, profileAge, satuanThn;
    Button editProfile;
    CardView logout, pengaturanAccount, infoApp;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        logout = view.findViewById(R.id.logout_layout);
        profileFoto = view.findViewById(R.id.profile_photo);
        profileNama = view.findViewById(R.id.profile_nama);
        profileAge = view.findViewById(R.id.profile_age);
        profileHeight = view.findViewById(R.id.profile_height);
        profileGender = view.findViewById(R.id.profile_gender);
        profileWeight = view.findViewById(R.id.profile_weight);
        editProfile = view.findViewById(R.id.profile_edit);
        pengaturanAccount = view.findViewById(R.id.pengaturan_akun);
        infoApp = view.findViewById(R.id.informasi_aplikasi);
        satuanThn = view.findViewById(R.id.user_thn_tv);
        setProfileView();

        pengaturanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileAccountSettingActivity.class);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
                startActivity(intent);
            }
        });

        infoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileAppInformasiActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), IntroActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    private void setProfileView() {

        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Picasso.get().load(snapshot.get("profilUrl").toString()).into(profileFoto);

                    profileNama.setText(snapshot.get("nama").toString());
                    profileGender.setText(snapshot.get("jenisKelamin").toString());
                    profileHeight.setText(snapshot.get("tinggiBadan").toString());
                    profileWeight.setText(snapshot.get("beratBadan").toString());

                    String birthday = snapshot.get("tglLahir").toString();
                    DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy");
                    //convert String to LocalDate
                    LocalDate localDate = LocalDate.parse(birthday, df);
                    LocalDate today = LocalDate.now();
                    int usia;
                    if(Boolean.valueOf(snapshot.get("isBayi").toString())) {
                        usia = Months.monthsBetween(localDate,today).getMonths();
                        satuanThn.setText("Bln");
                    } else {
                        usia = Years.yearsBetween(localDate,today).getYears();
                    }
                    profileAge.setText(Integer.toString(usia));

                } else {
                    Toast.makeText(getActivity(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
