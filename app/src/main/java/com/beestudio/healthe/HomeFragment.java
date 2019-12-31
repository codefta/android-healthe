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
    double karbohidrat;
    double lemak;
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
        Map<String, Object> o1  = new HashMap<>();
        o1.put("nama", "Bubur Ayam");
        o1.put("jumlahKalori", 372);
        o1.put("jumlahKarbohidrat", 36.12);
        o1.put("jumlahProtein", 27.56);
        o1.put("jumlahLemak", 12.4);
        o1.put("jenis", "Makanan Berat");
        o1.put("forAnak", true);
        o1.put("forBayi", false);
        o1.put("imageUrl", "https://img-global.cpcdn.com/recipes/f43b615362fa88a1/751x532cq70/bubur-ayam-kuah-kuning-foto-resep-utama.jpg");
        o1.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/bubur-ayam");

        Map<String, Object> o2  = new HashMap<>();
        o2.put("nama", "Bagel");
        o2.put("jumlahKalori", 270);
        o2.put("jumlahKarbohidrat", 53.02);
        o2.put("jumlahProtein", 10.52);
        o2.put("jumlahLemak", 1.7);
        o2.put("jenis", "Snack");
        o2.put("forAnak", true);
        o2.put("forBayi", false);
        o2.put("imageUrl", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Bagel-Plain-Alt.jpg/250px-Bagel-Plain-Alt.jpg");
        o2.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/bagel");

        Map<String, Object> o3  = new HashMap<>();
        o3.put("nama", "Donat");
        o3.put("jumlahKalori", 190);
        o3.put("jumlahKarbohidrat", 21.62);
        o3.put("jumlahProtein", 2.62);
        o3.put("jumlahLemak", 10.51);
        o3.put("jenis", "Snack");
        o3.put("forAnak", true);
        o3.put("forBayi", false);
        o3.put("imageUrl", "https://cdn.pixabay.com/photo/2018/05/12/12/56/donat-3393223_960_720.jpg");
        o3.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/doughnut");

        Map<String, Object> o4  = new HashMap<>();
        o4.put("nama", "Cokelat Kocok");
        o4.put("jumlahKalori", 478);
        o4.put("jumlahKarbohidrat", 77.08);
        o4.put("jumlahProtein", 12.78);
        o4.put("jumlahLemak", 13.91);
        o4.put("jenis", "Snack");
        o4.put("forAnak", true);
        o4.put("forBayi", false);
        o4.put("imageUrl", "https://www.tokomesin.com/wp-content/uploads/2017/10/resep-coklat-dingin2.jpg");
        o4.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/usda/chocolate-shake");

        Map<String, Object> o5  = new HashMap<>();
        o5.put("nama", "Nasi Goreng Mawut");
        o5.put("jumlahKalori", 210);
        o5.put("jumlahKarbohidrat", 42);
        o5.put("jumlahProtein", 5);
        o5.put("jumlahLemak", 1.5);
        o5.put("jenis", "Makanan Berat");
        o5.put("forAnak", true);
        o5.put("forBayi", false);
        o5.put("imageUrl", "https://www.netralnews.com/foto/2017/05/02/904-nasi_goreng_mawut_mudah_membuatnya_dan_lezat_rasanya.jpg");
        o5.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/trader-joes/vegetable-fried-rice");

        Map<String, Object> o6  = new HashMap<>();
        o6.put("nama", "Nestle Cerelac");
        o6.put("jumlahKalori", 60);
        o6.put("jumlahKarbohidrat", 10);
        o6.put("jumlahProtein", 2);
        o6.put("jumlahLemak", 1.5);
        o6.put("jenis", "Makanan Berat");
        o6.put("forAnak", false);
        o6.put("forBayi", true);
        o6.put("imageUrl", "https://www.tororo.com/pub/media/catalog/product/cache/c687aa7517cf01e65c009f6943c2b1e9/6/_/6_bulan_-_cerelac_beras_merah.png");
        o6.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/nestle/cerelac");

        Map<String, Object> o7  = new HashMap<>();
        o7.put("nama", "Biskuit Bayi");
        o7.put("jumlahKalori", 60);
        o7.put("jumlahKarbohidrat", 4.36);
        o7.put("jumlahProtein", 0.77);
        o7.put("jumlahLemak", 0.86);
        o7.put("jenis", "Snack");
        o7.put("forAnak", false);
        o7.put("forBayi", true);
        o7.put("imageUrl", "http://www.cutdekayi.com/wp-content/uploads/2016/08/milna-firtsbite-1024x629.jpg");
        o7.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/cookie-baby");

        Map<String, Object> o8  = new HashMap<>();
        o8.put("nama", "Bubur Pisang dan Nanas");
        o8.put("jumlahKalori", 10);
        o8.put("jumlahKarbohidrat", 2.72);
        o8.put("jumlahProtein", 0.03);
        o8.put("jumlahLemak", 4.54);
        o8.put("jenis", "Snack");
        o8.put("forAnak", false);
        o8.put("forBayi", true);
        o8.put("imageUrl", "https://i.pinimg.com/originals/f2/48/54/f24854910ce38d03484fe8bc3471d6ce.jpg");
        o8.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/bananas-and-pineapple-baby-food");

        Map<String, Object> o9  = new HashMap<>();
        o9.put("nama", "Saus Apel");
        o9.put("jumlahKalori", 6);
        o9.put("jumlahKarbohidrat", 1.69);
        o9.put("jumlahProtein", 0.02);
        o9.put("jumlahLemak", 0.02);
        o9.put("jenis", "Snack");
        o9.put("forAnak", false);
        o9.put("forBayi", true);
        o9.put("imageUrl", "https://images.parents.mdpcdn.com/sites/parents.com/files/styles/width_360/public/recipe/images/R034887.jpg");
        o9.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/generic/bananas-and-pineapple-baby-food");

        Map<String, Object> o10  = new HashMap<>();
        o10.put("nama", "Bubur Pear");
        o10.put("jumlahKalori", 70);
        o10.put("jumlahKarbohidrat", 16);
        o10.put("jumlahProtein", 0);
        o10.put("jumlahLemak", 0);
        o10.put("jenis", "Snack");
        o10.put("forAnak", false);
        o10.put("forBayi", true);
        o10.put("imageUrl", "https://keyassets-p2.timeincuk.net/wp/prod/wp-content/uploads/sites/53/2007/10/Pear-baby-food.jpg");
        o10.put("linkUrl", "https://www.fatsecret.com/calories-nutrition/earths-best/pear-baby-food");


        tambahData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    db.collection("makanan").document().set(o1);
//                    db.collection("makanan").document().set(o2);
//                    db.collection("makanan").document().set(o3);
//                    db.collection("makanan").document().set(o4);
                    db.collection("makanan").document().set(o5);
//                    db.collection("makanan").document().set(o6);
                    db.collection("makanan").document().set(o7);
//                    db.collection("makanan").document().set(o8);
//                    db.collection("makanan").document().set(o9);
//                    db.collection("makanan").document().set(o10);
            }
        });
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
                                        protein = kebutuhanGiziUser.hitungProtein(tee, kebutuhanHarian, bobotKandungan);

                                        proteinCount.setText(new DecimalFormat("##.#").format(protein));
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
                                        karbohidrat = kebutuhanGiziUser.hitungKarbohidrat(tee, kebutuhanHarian, bobotKandungan);

                                        karbohidratCount.setText(new DecimalFormat("##.#").format(karbohidrat));
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
                                        lemak = kebutuhanGiziUser.hitungLemak(tee, kebutuhanHarian, bobotKandungan);

                                        lemakCount.setText(new DecimalFormat("##.#").format(lemak));
                                    }
                                }
                            });

                    gizi.put("beratBadanIdeal", bbi);
                    gizi.put("lemak", lemak);
                    gizi.put("karbohidrat", karbohidrat);
                    gizi.put("protein", protein);
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
