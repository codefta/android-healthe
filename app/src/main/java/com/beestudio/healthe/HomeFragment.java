package com.beestudio.healthe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView namaHello, kaloriCount, proteinCount, karbohidratCount, lemakCount, bmiKategori, bmiStatus;
    FirebaseUser user;
    FirebaseFirestore db;
    Spinner spinner;

    private String aktifitasFisik;


    public HomeFragment() {
        // Required empty public constructor
    }

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
        proteinCount = view.findViewById(R.id.protein_count);
        karbohidratCount = view.findViewById(R.id.karbohidrat_count);
        lemakCount = view.findViewById(R.id.lemak_count);
        bmiKategori = view.findViewById(R.id.bmi_kategori);
        bmiStatus = view.findViewById(R.id.bmi_status);

        spinner = view.findViewById(R.id.aktifitas_spinner);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(), R.array.bobot_aktifitas, R.layout.spinner_item);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapterSpinner);

        updateHomeView();
        pilihAktivitas(spinner);

        return view;
    }

    private void pilihAktivitas(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString() != "Pilih Aktifitas") {
                    aktifitasFisik = parent.getItemAtPosition(position).toString();
                    db.collection("users").document(user.getUid())
                            .update("aktifitas_harian", aktifitasFisik);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateHomeView() {
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
                    namaHello.setText(snapshot.get("nama").toString());

//                    Set Selection
                    if(snapshot.get("aktifitas_harian") != null) {
                        if(TextUtils.equals(snapshot.get("aktifitas_harian").toString(), "Tidak Melakukan Aktifitas") || snapshot.get("aktifitas_harian") == null){
                            spinner.setSelection(1);
                        } else if(TextUtils.equals(snapshot.get("aktifitas_harian").toString(), "Melakukan Aktifitas Ringan")){
                            spinner.setSelection(2);
                        }  else if(TextUtils.equals(snapshot.get("aktifitas_harian").toString(), "Melakukan Aktifitas Sedang")){
                            spinner.setSelection(3);
                        }  else if(TextUtils.equals(snapshot.get("aktifitas_harian").toString(), "Melakukan Aktifitas Berat")){
                            spinner.setSelection(4);
                        }
                    } else {
                        spinner.setSelection(1);
                    }

                    int tb = Integer.parseInt(snapshot.get("tinggi_badan").toString());
                    int bb = Integer.parseInt(snapshot.get("berat_badan").toString());
                    int umur = Integer.parseInt(snapshot.get("usia").toString());
                    String aktifitasFisik = snapshot.get("aktifitas_harian").toString();
                    String gender = snapshot.get("jenis_kelamin").toString();

//                    Menghitung BMR

                    double bbiResult = hitungBBI(tb, bb);
                    double kkbResult = hitungKKB(bbiResult, gender);
                    double aktifitasResult = aktifitasFisik(aktifitasFisik);
                    double faktorResikoResult = faktorKoreksi(umur);
                    double kktResult = hitungKKT(kkbResult, aktifitasResult, faktorResikoResult);
                    kaloriCount.setText(new DecimalFormat("##.#").format(kktResult));

                    double proteinResult = hitungProtein(kktResult);
                    double karbohidratResult = hitungKarbohidrat(kktResult);
                    double lemakResult = hitungLemak(kktResult);
                    proteinCount.setText(new DecimalFormat("##.#").format(proteinResult));
                    karbohidratCount.setText(new DecimalFormat("##.#").format(karbohidratResult));
                    lemakCount.setText(new DecimalFormat("##.#").format(lemakResult));

//                    Menghitung BMI
                    String bmiKategoriResult = kelompokBMI(bb, tb);
                    String bmiStatusResult = statusBMI(bb, tb);
                    bmiKategori.setText(bmiKategoriResult);
                    if(bmiKategoriResult == "Kurus Tingkat 2" || bmiKategoriResult == "Kurus Tingkat 1") {
                        bmiKategori.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                    } else  if(bmiKategoriResult == "Normal") {
                        bmiKategori.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    } else {
                        bmiKategori.setTextColor(getActivity().getResources().getColor(R.color.colorYellow));
                    }
                    bmiStatus.setText(bmiStatusResult);
                } else {
                    Toast.makeText(getActivity(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private double hitungBeratBadanIdeal(String gender, double umur, )

//    Menghitung berat badan ideal
//    private double hitungBBI(int tb, int bb) {
//
//    }

//    Menghitung Kebutuhan Kalori Bassal
    private double hitungKKB(double bbi, String gender) {
        if(TextUtils.equals(gender, "Laki-laki")) {
            return 30 * bbi;
        } else {
            return 25 * bbi;
        }
    }

//    Aktifitas Fisik
    private double aktifitasFisik(String aktivitasFisik) {
        if (TextUtils.equals(aktivitasFisik, "Tidak Melakukan Aktifitas")) {
            return 0;
        } else if(TextUtils.equals(aktivitasFisik, "Melakukan Aktifitas Ringan")) {
            return 10.0 / 100.0;
        } else if(TextUtils.equals(aktivitasFisik, "Melakukan Aktifitas Sedang")) {
            return 20.0 / 100.0;
        } else if(TextUtils.equals(aktivitasFisik,  "Melakukan Aktifitas Berat")) {
            return 40.0 / 100.0;
        } else {
            return 0;
        }
    }

//    faktorKoreksi
    private double faktorKoreksi(int usia) {
        if(usia >= 40 && usia <= 59) {
            return 5 / 100.0;
        } else if(usia >= 60 && usia <= 69) {
            return 10 / 100.0;
        } else if(usia >= 70) {
            return 20 / 100.0;
        } else {
            return 0;
        }
    }

//    Menghitung kebutuhan kalori total
    private double hitungKKT(double kkb, double aktifitasFisik, double faktorKoreksi ) {
        double ktt = kkb + aktifitasFisik * kkb - faktorKoreksi * kkb;
        return  ktt;
    }

    private double hitungProtein(double jumlahKalori) {

        double jumlahKaloriProtein =  (15 / 100.0) * jumlahKalori;
        double result = jumlahKaloriProtein / 4;
        return result;
    }

    private double hitungKarbohidrat(double jumlahKalori) {
        double jumlahKaloriKarbohidrat = (65 / 100.0) * jumlahKalori;
        double result = jumlahKaloriKarbohidrat / 4;
        return result;
    }

    private double hitungLemak(double jumlahKalori) {
        double jumlahKaloriLemak = (20 / 100.0) * jumlahKalori;
        double result = jumlahKaloriLemak / 9;

        return result;
    }


    public double hitungBMI(int bb, int tb) {
        if(bb != 0) {
            return  bb / Math.pow(2, (tb/100.0));
        } else {
            return 0;
        }
    }

    private String kelompokBMI(int bb, int tb) {

        double bmi = hitungBMI(bb, tb);

        if(bmi < 17) {
            return "Kurus Tingkat 2";
        } else if(bmi >= 17 && bmi < 18.5) {
            return "Kurus Tingkat 1";
        } else if(bmi >= 18.5 && bmi < 25) {
            return "Normal";
        } else if(bmi >= 25 && bmi <27) {
            return "Obesitas Tingkat 1";
        } else {
            return "Obesitas Tingkat 2";
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
