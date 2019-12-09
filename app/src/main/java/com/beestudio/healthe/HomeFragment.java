package com.beestudio.healthe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView namaHello, kaloriCount, bmiKategori, bmiStatus;
    FirebaseUser user;
    FirebaseFirestore db;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        namaHello = view.findViewById(R.id.nama_tv);
        kaloriCount = view.findViewById(R.id.kalori_count);
        bmiKategori = view.findViewById(R.id.bmi_kategori);
        bmiStatus = view.findViewById(R.id.bmi_status);

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                namaHello.setText(document.get("nama").toString());
                                int tb = Integer.parseInt(document.get("tinggi_badan").toString());
                                int bb = Integer.parseInt(document.get("berat_badan").toString());
                                int umur = Integer.parseInt(document.get("usia").toString());
                                String gender = document.get("jenis_kelamin").toString();

                                int kaloriResult = hitungBMR(gender, tb, umur, bb);
                                String bmiKategoriResult = kelompokBMI(bb, tb);
                                String bmiStatusResult = statusBMI(bb, tb);

                                kaloriCount.setText(Integer.toString(kaloriResult));
                                bmiKategori.setText(bmiKategoriResult);
                                bmiStatus.setText(bmiStatusResult);

                            } else {

                            }
                        } else {
                        }
                    }
                });

        return view;
    }


    public int hitungBMR(String gender, int tb, int umur, int bb) {

        if(gender == "Laki-laki") {
            return (int) (88.362 + (13.397 * bb) + (4.799 * tb) - (5.677 * umur));
        } else {
            return (int) (447.563 + (9.247 * bb) + (3.098 * tb) - (4.330 * umur));
        }
    }

    public double hitungBMI(int bb, int tb) {
        return  bb / (tb / 100);
    }

    private String kelompokBMI(int bb, int tb) {

        double bmi = hitungBMI(bb, tb);

        if(bmi < 17) {
            return "Kurus";
        } else if(bmi >= 17 && bmi < 18.5) {
            return "Kurus";
        } else if(bmi >= 18.5 && bmi < 25) {
            return "Normal";
        } else if(bmi >= 25 && bmi <27) {
            return "Gemuk";
        } else {
            return "Gemuk";
        }
    }

    private String statusBMI(int bb, int tb) {
        double bmi = hitungBMI(bb, tb);

        if(bmi < 17) {
            return "Kekurangan berat badan tingkat berat";
        } else if(bmi >= 17 && bmi < 18.5) {
            return "Kekurangan berat badan tingkat rendah";
        } else if(bmi >= 18.5 && bmi < 25) {
            return "Normal";
        } else if(bmi >= 25 && bmi <27) {
            return "Kelebihan berat badan tingkat ringan";
        } else {
            return "Kelebihan berat badan tingkat berat";
        }
    }

}
