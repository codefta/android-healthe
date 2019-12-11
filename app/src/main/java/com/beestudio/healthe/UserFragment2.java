package com.beestudio.healthe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFragment2 extends Fragment {

    EditText usia, tb, bb;
    RadioGroup gender;
    RadioButton genderSelected;
    Button submit;

    FirebaseUser user;
    FirebaseFirestore db;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.user_two, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        usia = view.findViewById(R.id.input_usia);
        tb = view.findViewById(R.id.input_tb);
        bb = view.findViewById(R.id.input_bb);
        submit = view.findViewById(R.id.submit_datadiri);
        gender = view.findViewById(R.id.gender);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGender = gender.getCheckedRadioButtonId();

                genderSelected = (RadioButton) view.findViewById(selectedGender);

                updateDatabase(genderSelected.getText().toString());
            }
        });

        return view;
    }

    public void updateDatabase(String gender) {
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("jenis_kelamin", gender);
        userUpdate.put("usia", Integer.parseInt(usia.getText().toString()));
        userUpdate.put("tinggi_badan", Integer.parseInt(tb.getText().toString()));
        userUpdate.put("berat_badan", Integer.parseInt(bb.getText().toString()));

        progressDialog.show();
        db.collection("users").document(user.getUid().toString())
                .update(userUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.hide();
                        Toast.makeText(getActivity(), "Anda berhasil melengkapi data diri", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Toast.makeText(getActivity(), "Anda gagal melengkapi data diri", Toast.LENGTH_SHORT).show();
                    }
                });

        progressDialog.hide();
        getActivity().finish();
    }

}
