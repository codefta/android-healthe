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

import android.text.Layout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MakananAllFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.makanan_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.makanan_list)
    RecyclerView makananList;

    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    FirebaseUser user;

    String jenisMakanan;
    String idMakanan;

    public MakananAllFragment() {
        // Required empty public constructor
    }


    public static MakananAllFragment newInstance(String param1, String param2) {
        MakananAllFragment fragment = new MakananAllFragment();
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
        View viewLayout = inflater.inflate(R.layout.fragment_makanan_all, container, false);
        makananList = viewLayout.findViewById(R.id.makanan_list);
        progressBar = viewLayout.findViewById(R.id.makanan_progress_bar);
        makananList.setLayoutManager(new LinearLayoutManager(getContext()));
        ButterKnife.bind(getActivity());
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            jenisMakanan= bundle.getString("jenisMakanan", "all");
//            Toast.makeText(getActivity(), jenisMakanan, Toast.LENGTH_SHORT).show();
        }

        getMakanan(jenisMakanan);
        return viewLayout;
    }


    private void getMakanan(String jenisMakanan) {
        Query query;
        if(TextUtils.equals(jenisMakanan, "ringan")) {
            query = db.collection("makanan").whereEqualTo("jenis", "Snack");;
        } else if(TextUtils.equals(jenisMakanan, "berat")) {
            query = db.collection("makanan").whereEqualTo("jenis", "Makanan Berat");;
        } else if(TextUtils.equals(jenisMakanan, "minuman")) {
            query = db.collection("makanan").whereEqualTo("jenis", "Minuman");;
        } else if(TextUtils.equals(jenisMakanan, "buahSayur")) {
            query = db.collection("makanan").whereEqualTo("jenis", "Buah dan Sayur");;
        } else{
            query= db.collection("makanan");
        }

        FirestoreRecyclerOptions<MakananResponse> res = new FirestoreRecyclerOptions.Builder<MakananResponse>()
                .setQuery(query, MakananResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MakananResponse, MakananHolder>(res) {
            @Override
            protected void onBindViewHolder(@NonNull MakananHolder holder, int position, @NonNull MakananResponse model) {
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

//                DocumentSnapshot dSnap = getSnapshots().getSnapshot(holder.getAdapterPosition());
//                String docId = dSnap.getId();
//
//                db.collection("users").document(user.getUid())
//                        .collection("makananDikonsumsi")
//                        .whereEqualTo("makananId", docId)
//                        .limit(1)
//                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                if (!queryDocumentSnapshots.isEmpty()){
//
////                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
////                                        idMakanan = doc.get("makananId").toString();
////
////                                        if(idMakanan)
////                                    }
//                                    holder.btnHitung.setBackgroundColor(getResources().getColor(R.color.colorYellow));
//                                    holder.btnHitung.setText("- Dihitung");
//                                }
//                            }
//                        });

                holder.btnHitung.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot dSnap = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        String docId = dSnap.getId();
                        Toast.makeText(getActivity(), "Test Button Hitung", Toast.LENGTH_LONG).show();
                        Map<String, Object> objMakanan = new HashMap<>();
                        objMakanan.put("nama", model.getNama());
                        objMakanan.put("jenis", model.getJenis());
                        objMakanan.put("jumlahKalori", model.getJumlahKalori());
                        objMakanan.put("jumlahKarbohidrat", model.getJumlahKarbohidrat());
                        objMakanan.put("jumlahProtein", model.getJumlahProtein());
                        objMakanan.put("jumlahLemak", model.getJumlahLemak());
                        objMakanan.put("linkUrl", model.getLinkUrl());
                        objMakanan.put("imageUrl", model.getImageUrl());
                        objMakanan.put("makananId", docId);

                        db.collection("users").document(user.getUid())
                                .collection("makananDikonsumsi")
                                .add(objMakanan)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()) {
                                            holder.btnHitung.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                            holder.btnHitung.setText("Dihitung");
                                        }
                                    }
                                });
                    }
                });
            }

            @NonNull
            @Override
            public MakananHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.makanan_all_item, parent, false);

                return new MakananHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        makananList.setAdapter(adapter);
    }

    public class MakananHolder extends RecyclerView.ViewHolder {
        ImageView makananImage;
        TextView makananTitle;
        TextView makananKategori;
        TextView makananKalori;
        TextView makananKarbohidrat;
        TextView makananProtein;
        TextView makananLemak;
        Button btnHitung;

        public MakananHolder(View itemView) {
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
