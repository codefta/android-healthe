package com.beestudio.healthe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class EditStresDialogFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FirebaseUser user;
    FirebaseFirestore db;

    private ArrayAdapter jenisStresArrayAdapter;
    private List<String> jenisStresSpinnerArray;
    private List<String> jenisStresId;
    private List<Double> bobotStres;
    private List<String> keteranganStres;

    Spinner spinnerStres;
    Button simpanStres;

    String idStres;
    double bStres;
    String nStres;
    String kStres;

    TextView stresKeteranganView;

    public EditStresDialogFragment() {
        // Required empty public constructor
    }

    public static EditStresDialogFragment newInstance(String param1, String param2) {
        EditStresDialogFragment fragment = new EditStresDialogFragment();
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
        View view =inflater.inflate(R.layout.fragment_edit_stres_dialog, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        spinnerStres = view.findViewById(R.id.stres_spinner);
        simpanStres = view.findViewById(R.id.btn_simpan_stres);
        jenisStresSpinnerArray = new ArrayList<String>();
        jenisStresId = new ArrayList<String>();
        bobotStres = new ArrayList<Double>();
        keteranganStres = new ArrayList<String>();
        loadStres();

        return view;
    }

    private void loadStres() {
        db.collection("bobot_stres")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            jenisStresSpinnerArray.add("Pilih Kondisi");
                            jenisStresId.add("");
                            keteranganStres.add("");
                            bobotStres.add(0.0);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bobotStres.add(Double.valueOf(document.get("bobot").toString()));
                                keteranganStres.add(document.get("keterangan").toString());
                                jenisStresSpinnerArray.add(document.get("tingkatStres").toString());

                                jenisStresId.add(document.getId());

                            }

                            jenisStresArrayAdapter = new ArrayAdapter(getContext(),
                                    R.layout.spinner_item,
                                    jenisStresSpinnerArray);
                            jenisStresArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinnerStres.setAdapter(jenisStresArrayAdapter);

                            spinnerStres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    idStres = jenisStresId.get(position);
                                    nStres = jenisStresSpinnerArray.get(position);
                                    kStres = keteranganStres.get(position);
                                    bStres = bobotStres.get(position);
                                    stresKeteranganView.setText(kStres);
                                    stresKeteranganView.setVisibility(View.VISIBLE);

                                    simpanStres.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pilihStres(kStres, bStres, nStres);
                                            getDialog().dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }


                    }
                });
    }

    private void pilihStres(String ketStres, double botStres, String jenStres) {
        Map<String, Object> jenisStresObj = new HashMap<>();
        jenisStresObj.put("bobot", botStres);
        jenisStresObj.put("tingkatStres", jenStres);
        jenisStresObj.put("keterangan", ketStres);
        db.collection("users").document(user.getUid())
                .update("stres", jenisStresObj);
    }


}
