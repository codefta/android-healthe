package com.beestudio.healthe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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


public class EditAktifitasDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayAdapter jenisAktifitasArrayAdapter;
    private List<String> jenisAktifitasId;
    private List<String> jenisAktifitasSpinnerArray;
    private List<Double> bobotAktifitas;
    private List<String> keteranganAktifitas;

    FirebaseUser user;
    FirebaseFirestore db;

    Spinner spinnerAktifitas, spinnerStres;
    Button simpanAktivitas;
    String idAktifitas;
    String nAktifitas;
    Double bAktifitas;
    String kAktifitas;


    public EditAktifitasDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditAktifitasDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAktifitasDialogFragment newInstance(String param1, String param2) {
        EditAktifitasDialogFragment fragment = new EditAktifitasDialogFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_aktifitas_dialog, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        spinnerAktifitas = view.findViewById(R.id.aktifitas_spinner);
        simpanAktivitas = view.findViewById(R.id.btn_simpan_aktifitas);
        jenisAktifitasSpinnerArray = new ArrayList<String>();
        jenisAktifitasId = new ArrayList<String>();
        keteranganAktifitas = new ArrayList<String>();
        bobotAktifitas = new ArrayList<Double>();
        loadAktifitas();
        return view;
    }

    private void loadAktifitas() {
        db.collection("bobot_aktifitas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            jenisAktifitasSpinnerArray.add("Pilih Aktifitas");
                            jenisAktifitasId.add("");
                            keteranganAktifitas.add("");
                            bobotAktifitas.add(0.0);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                jenisAktifitasSpinnerArray.add(document.get("tingkatAktifitas").toString());
                                jenisAktifitasId.add(document.getId());
                                keteranganAktifitas.add(document.get("keterangan").toString());
                                bobotAktifitas.add(Double.valueOf(document.get("bobot").toString()));
                            }

                            jenisAktifitasArrayAdapter = new ArrayAdapter(getContext(),
                                    R.layout.spinner_item,
                                    jenisAktifitasSpinnerArray);
                            spinnerAktifitas.setAdapter(jenisAktifitasArrayAdapter);

                            spinnerAktifitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                     idAktifitas = jenisAktifitasId.get(position);
                                     nAktifitas = jenisAktifitasSpinnerArray.get(position);
                                     bAktifitas = bobotAktifitas.get(position);
                                     kAktifitas = keteranganAktifitas.get(position);

                                    simpanAktivitas.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pilihAktivitas(nAktifitas, bAktifitas, kAktifitas);
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


    private void pilihAktivitas(String aktivitasJenis, double aktivitasBobot, String aktifitasKet) {
        Map<String, Object> jenisAktifitasObj = new HashMap<>();
        jenisAktifitasObj.put("bobot", aktivitasBobot);
        jenisAktifitasObj.put("tingkatAktifitas", aktivitasJenis);
        jenisAktifitasObj.put("keterangan", aktifitasKet);
        db.collection("users").document(user.getUid())
                .update("aktifitas", jenisAktifitasObj);
    }


}
