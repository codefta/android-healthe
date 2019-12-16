package com.beestudio.healthe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beestudio.healthe.models.KebutuhanGiziUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView namaHello, kaloriCount, proteinCount, karbohidratCount, lemakCount, bmiKategori, bmiStatus, stresStatus, aktivitasStatus, stresChange, aktifitasChange, aktivitasKeterangan, stresKeterangan;
    FirebaseUser user;
    FirebaseFirestore db;

    KebutuhanGiziUser kebutuhanGiziUser;
    Button tambahData;

    double protein;
    double tee;
    Map<String, Object> gizi;

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

        kebutuhanGiziUser = new KebutuhanGiziUser();
        gizi = new HashMap<>();

        namaHello = view.findViewById(R.id.nama_tv);
        kaloriCount = view.findViewById(R.id.kalori_count);
        proteinCount = view.findViewById(R.id.protein_count);
        karbohidratCount = view.findViewById(R.id.karbohidrat_count);
        lemakCount = view.findViewById(R.id.lemak_count);
        aktivitasStatus = view.findViewById(R.id.aktivitas_tv);
        stresStatus = view.findViewById(R.id.stres_tv);
        aktifitasChange = view.findViewById(R.id.aktivitas_change);
        stresChange = view.findViewById(R.id.stres_change);
        tambahData = view.findViewById(R.id.tambah_data_makanan);
        aktivitasKeterangan = view.findViewById(R.id.aktivitas_keterangan_tv);
        stresKeterangan = view.findViewById(R.id.stres_keterangan_tv);

        aktifitasChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditAktifitasDialog();
            }
        });

        stresChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditStresDialog();
            }
        });

        updateHomeView();
        Map<String, Object> o  = new HashMap<>();
        o.put("nama", "Jus Jeruk");
        o.put("jumlahKalori", 112);
        o.put("jumlahKarbohidrat", 25.79);
        o.put("jumlahProtein", 1.74);
        o.put("jumlahLemak", 0.5);
        o.put("jenis", "Minuman");
        o.put("imageUrl", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/A_glass_of_orange_juice_%282015-10-10%29.JPG/1200px-A_glass_of_orange_juice_%282015-10-10%29.JPG");
        o.put("linkUrl", "https://www.fatsecret.co.id/kalori-gizi/umum/jus-jeruk");

        Map<String, Object> o2  = new HashMap<>();
        o2.put("nama", "Buah Apel");
        o2.put("jumlahKalori", 72);
        o2.put("jumlahKarbohidrat", 19.06);
        o2.put("jumlahProtein", 0.36);
        o2.put("jumlahLemak", 0.5);
        o2.put("jenis", "Buah dan Sayur");
        o2.put("imageUrl", "https://www.parentingclub.co.id/uploads/default/files/article/578/eksperimen-menyegarkan-apel_578_Eksperimen_Menyegarkan_Apel_261-Eksperimen-Menyegarkan-Apel-th.jpg");
        o2.put("linkUrl", "https://www.fatsecret.co.id/kalori-gizi/umum/apel");

//        tambahData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.collection("makanan").document().set(o);
//                db.collection("makanan").document().set(o2);
//
//            }
//        });
        tambahData.setVisibility(View.GONE);
        return view;
    }

    private void showEditAktifitasDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
            EditAktifitasDialogFragment editNameDialogFragment = EditAktifitasDialogFragment.newInstance("", "");
        editNameDialogFragment.show(fm, "fragment_edit_aktifitas_dialog");
    }

    private void showEditStresDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditStresDialogFragment editStresDialogFragment = EditStresDialogFragment.newInstance("", "");
        editStresDialogFragment.show(fm, "fragment_edit_stres_dialog");
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
                    Map obj = snapshot.getData();
                    Map aktifitas = (Map) obj.get("aktifitas");
                    Map stres = (Map) obj.get("stres");


                    namaHello.setText(snapshot.get("nama").toString());
                    aktivitasStatus.setText(aktifitas.get("tingkatAktifitas").toString());
                    aktivitasKeterangan.setText(aktifitas.get("keterangan").toString());
                    stresStatus.setText(stres.get("tingkatStres").toString());
                    stresKeterangan.setText(stres.get("keterangan").toString());

                    int tb = Integer.parseInt(snapshot.get("tinggiBadan").toString());
                    int bb = Integer.parseInt(snapshot.get("beratBadan").toString());
                    String gender = snapshot.get("jenisKelamin").toString();
                    boolean isBayi = Boolean.valueOf(snapshot.get("isBayi").toString());
                    String birthday = snapshot.get("tglLahir").toString();
                    DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy");
                    LocalDate localDate = LocalDate.parse(birthday, df);
                    LocalDate today = LocalDate.now();
                    int usia;
                    if(Boolean.valueOf(snapshot.get("isBayi").toString())) {
                        usia = Months.monthsBetween(localDate,today).getMonths();
                    } else {
                        usia = Years.yearsBetween(localDate,today).getYears();
                    }

                    double bbi = kebutuhanGiziUser.hitungBBI(gender, tb, usia, isBayi);
                    double bmr = kebutuhanGiziUser.hitungBMR(gender, tb, bb, bbi, usia, isBayi);
                    double bobotAktivitas = Double.valueOf(aktifitas.get("bobot").toString());
                    double bobotStres = Double.valueOf(stres.get("bobot").toString());
                    tee = kebutuhanGiziUser.hitungTEE(bobotAktivitas, bobotStres, bmr);
                    kaloriCount.setText(new DecimalFormat("#").format(tee));

                    db.collection("kandungan_kalori")
                            .whereEqualTo("namaKandungan", "Protein")
                            .limit(1)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        double bobotKandungan = Double.valueOf(document.get("bobotKandungan").toString());
                                        double kebutuhanHarian = Double.valueOf(document.get("kebutuhanHarian").toString());
                                        double protein = kebutuhanGiziUser.hitungProtein(tee, kebutuhanHarian, bobotKandungan);
                                        proteinCount.setText(new DecimalFormat("##.#").format(protein));
                                        gizi.put("protein", protein);
                                    }
                                }
                            });

                    db.collection("kandungan_kalori")
                            .whereEqualTo("namaKandungan", "Karbohidrat")
                            .limit(1)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        double bobotKandungan = Double.valueOf(document.get("bobotKandungan").toString());
                                        double kebutuhanHarian = Double.valueOf(document.get("kebutuhanHarian").toString());
                                        double karbohidrat = kebutuhanGiziUser.hitungKarbohidrat(tee, kebutuhanHarian, bobotKandungan);
                                        karbohidratCount.setText(new DecimalFormat("##.#").format(karbohidrat));
                                        gizi.put("karbohidrat", karbohidrat);
                                    }
                                }
                            });

                    db.collection("kandungan_kalori")
                            .whereEqualTo("namaKandungan", "Lemak")
                            .limit(1)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        double bobotKandungan = Double.valueOf(document.get("bobotKandungan").toString());
                                        double kebutuhanHarian = Double.valueOf(document.get("kebutuhanHarian").toString());
                                        double lemak = kebutuhanGiziUser.hitungLemak(tee, kebutuhanHarian, bobotKandungan);
                                        lemakCount.setText(new DecimalFormat("##.#").format(lemak));
                                        gizi.put("lemak", lemak);
                                    }
                                }
                            });

                    gizi.put("beratBadanIdeal", bbi);
                    gizi.put("bmr", bmr);
                    gizi.put("totalGizi", tee);

                    db.collection("users").document(user.getUid())
                            .update("kebutuhanGizi", gizi);
                } else {
                    Toast.makeText(getActivity(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
