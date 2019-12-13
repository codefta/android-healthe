package com.beestudio.healthe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beestudio.healthe.models.RekomendasiMakanan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MakananFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser user;
    FirebaseFirestore db;

    TextView makanPagi, makanSiang, makanMalam, makanSnack, namaUser;
    Button makananAll;

    public MakananFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MakananFragment newInstance(String param1, String param2) {
        MakananFragment fragment = new MakananFragment();
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
        View view =inflater.inflate(R.layout.fragment_makanan, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        makanPagi = view.findViewById(R.id.makan_pagi_count);
        makanSiang = view.findViewById(R.id.makan_siang_count);
        makanMalam  = view.findViewById(R.id.makan_malam_count);
        makanSnack = view.findViewById(R.id.makan_ringan_count);
        namaUser = view.findViewById(R.id.nama_tv);
        makananAll = view.findViewById(R.id.btn_all_makanan);
        updateView();
        openFragment(MakananAllFragment.newInstance("", ""));
        return view;
    }

    private void updateView() {
        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Map obj = snapshot.getData();
                    Map kebutuhanGizi = (Map) obj.get("kebutuhanGizi");
                    RekomendasiMakanan rekomendasiMakanan = new RekomendasiMakanan();
                    double tee = Double.valueOf(kebutuhanGizi.get("totalGizi").toString());
                    double mPagi = rekomendasiMakanan.hitungMakanPagi(tee);
                    double mSiang = rekomendasiMakanan.hitungMakanSiang(tee);
                    double mSnack = rekomendasiMakanan.hitungMakanSnack(tee);
                    double mMalam = rekomendasiMakanan.hitungMakanMalam(tee);

                    namaUser.setText(snapshot.get("nama").toString());
                    makanPagi.setText(new DecimalFormat("##.#").format(mPagi));
                    makanSiang.setText(new DecimalFormat("##.#").format(mSiang));
                    makanSnack.setText(new DecimalFormat("##.#").format(mSnack));
                    makanMalam.setText(new DecimalFormat("##.#").format(mMalam));

                } else {
                    Toast.makeText(getActivity(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_makanan, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_makanan:
                openFragment(MakananAllFragment.newInstance("", ""));
                makananAll.setBackgroundColor(getResources().getColor(R.color.colorHealthe));
                Toast.makeText(getActivity(), "Button All", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
