package com.beestudio.healthe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beestudio.healthe.models.MakananResponse;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class KalkulatorFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    @BindView(R.id.makanan_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.makanan_list)
    RecyclerView makananList;

    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;
    TextView kaloriCount, karboCount, proteinCount, lemakCount, kaloriTotal, karboTotal, proteinTotal, lemakTotal, namaTv;

    ProgressBar kaloriProg, karboProg, proteinProg, lemakProg;

    String idMakanan;
    int jmlKalori;
    double jmlKarbo, jmlProtein, jmlLemak;


    public KalkulatorFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static KalkulatorFragment newInstance(String param1, String param2) {
        KalkulatorFragment fragment = new KalkulatorFragment();
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
        View view =  inflater.inflate(R.layout.fragment_kalkulator, container, false);
        makananList = view.findViewById(R.id.makanan_list);
        progressBar = view.findViewById(R.id.makanan_progress_bar);
        kaloriCount = view.findViewById(R.id.kalori_progress_count);
        kaloriTotal = view.findViewById(R.id.kalori_progress_total);
        karboCount = view.findViewById(R.id.karbohidrat_progress_count);
        karboTotal = view.findViewById(R.id.karbohidrat_progress_total);
        proteinCount = view.findViewById(R.id.protein_progress_count);
        proteinTotal = view.findViewById(R.id.protein_progress_total);
        lemakCount = view.findViewById(R.id.lemak_progress_count);
        lemakTotal = view.findViewById(R.id.lemak_progress_total);
        namaTv = view.findViewById(R.id.nama_tv);
        kaloriProg = view.findViewById(R.id.kalori_progress);
        karboProg = view.findViewById(R.id.karbohidrat_progress_bar);
        proteinProg = view.findViewById(R.id.protein_progress_bar);
        lemakProg = view.findViewById(R.id.lemak_progress_bar);

        makananList.setLayoutManager(new LinearLayoutManager(getContext()));
        ButterKnife.bind(getActivity());
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        jmlKalori = 0;
        getMakanan();

        hitProgres();
        return view;
    }

    private void hitProgres() {
        db.collection("users").document(user.getUid())
                .collection("makananDikonsumsi")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            jmlKalori += Integer.parseInt(doc.get("jumlahKalori").toString());
                            kaloriProg.setProgress(jmlKalori);
                            jmlLemak += Double.valueOf(doc.get("jumlahLemak").toString());
                            lemakProg.setProgress((int)jmlLemak);
                            jmlProtein += Double.valueOf(doc.get("jumlahProtein").toString());
                            proteinProg.setProgress((int)jmlProtein);
                            jmlKarbo += Double.valueOf(doc.get("jumlahKarbohidrat").toString());
                            karboProg.setProgress((int) jmlKarbo);
                            kaloriCount.setText(Integer.toString(jmlKalori));
                            karboCount.setText(new DecimalFormat("#.#").format(jmlKarbo));
                            lemakCount.setText(new DecimalFormat("#.#").format(jmlLemak));
                            proteinCount.setText(new DecimalFormat("#.#").format(jmlProtein));
                        }
                    }
                });

        db.collection("users").document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
                            Map<String, Object> kebutuhanGizi = (HashMap<String, Object>) documentSnapshot.getData().get("kebutuhanGizi");

                            double jmlKalori = Double.valueOf(kebutuhanGizi.get("totalGizi").toString());
                            kaloriProg.setMax((int) jmlKalori);
                            kaloriTotal.setText(new DecimalFormat("#").format(jmlKalori));
                            double jmlKarbo = Double.valueOf(kebutuhanGizi.get("karbohidrat").toString());
                            karboProg.setMax((int) jmlKarbo);
                            karboTotal.setText(new DecimalFormat("#.#").format(jmlKarbo));
                            double jmlProtein = Double.valueOf(kebutuhanGizi.get("protein").toString());
                            proteinProg.setMax((int)jmlProtein);
                            proteinTotal.setText(new DecimalFormat("#.#").format(jmlProtein));
                            double jmlLemak = Double.valueOf(kebutuhanGizi.get("lemak").toString());
                            lemakProg.setMax((int)jmlLemak);
                            lemakTotal.setText(new DecimalFormat("#.#").format(jmlLemak));
                            namaTv.setText(documentSnapshot.get("nama").toString());
                        }
                    }
                });

    }


    private void getMakanan() {

        Query query= db.collection("users").document(user.getUid())
                .collection("makananDikonsumsi");


        FirestoreRecyclerOptions<MakananResponse> res = new FirestoreRecyclerOptions.Builder<MakananResponse>()
                .setQuery(query, MakananResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MakananResponse, KalkulatorFragment.MakananKuHolder>(res) {
            @Override
            protected void onBindViewHolder(@NonNull KalkulatorFragment.MakananKuHolder holder, int position, @NonNull MakananResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.makananTitle.setText(model.getNama());
                holder.makananKategori.setText(model.getJenis());
                holder.makananKalori.setText(Integer.toString(model.getJumlahKalori()));
                holder.makananKarbohidrat.setText(Double.toString(model.getJumlahKarbohidrat()));
                holder.makananProtein.setText(Double.toString(model.getJumlahProtein()));
                holder.makananLemak.setText(Double.toString(model.getJumlahLemak()));
                Glide.with(getContext())
                        .load(model.getImageUrl())
                        .into(holder.makananImage);

                holder.itemView.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(model.getLinkUrl()));
                    startActivity(i);
                });

                DocumentSnapshot dSnap = getSnapshots().getSnapshot(holder.getAdapterPosition());
                String docId = dSnap.getId();

                holder.btnHitung.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("users").document(user.getUid())
                                .collection("makananDikonsumsi").document(docId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Terhapus", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }

            @NonNull
            @Override
            public KalkulatorFragment.MakananKuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.makanan_saya_item, parent, false);

                return new KalkulatorFragment.MakananKuHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        makananList.setAdapter(adapter);
    }

    public class MakananKuHolder extends RecyclerView.ViewHolder {
        ImageView makananImage;
        TextView makananTitle;
        TextView makananKategori;
        TextView makananKalori;
        TextView makananKarbohidrat;
        TextView makananProtein;
        TextView makananLemak;
        Button btnHitung;

        public MakananKuHolder(View itemView) {
            super(itemView);
            makananImage = itemView.findViewById(R.id.makanan_image);
            makananTitle = itemView.findViewById(R.id.makanan_title);
            makananKategori = itemView.findViewById(R.id.makanan_kategori);
            makananKalori = itemView.findViewById(R.id.makanan_kalori);
            makananProtein = itemView.findViewById(R.id.makanan_protein);
            makananLemak = itemView.findViewById(R.id.makanan_lemak);
            makananKarbohidrat = itemView.findViewById(R.id.makanan_karbohidrat);
            btnHitung = itemView.findViewById(R.id.btn_hitung_kal_makanan);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
