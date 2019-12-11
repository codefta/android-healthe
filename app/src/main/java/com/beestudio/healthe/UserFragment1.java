package com.beestudio.healthe;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class UserFragment1 extends Fragment {

    private Button btnSubmit;
    String storagePath = "profile_image/";
    int imageRequestCode = 7;
    Boolean processSubmit = false;

    Button chooseButton;
    EditText namaEditText, noHpEditText;
    ImageView selectImage;
    Uri filePathUri;

    StorageReference storageReference;
    FirebaseUser user;
    FirebaseFirestore db;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.user_one, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        chooseButton = view.findViewById(R.id.btn_pilih_image);
        selectImage = view.findViewById(R.id.show_image);

        btnSubmit = view.findViewById(R.id.next_datadiri);
        namaEditText = view.findViewById(R.id.input_nama);
        noHpEditText = view.findViewById(R.id.input_nohp);

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Silakan pilih gambar"), imageRequestCode);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == imageRequestCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.getData();

            Picasso.get().load(filePathUri).into(selectImage);
            chooseButton.setText("Gambar Terpilih");
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {

        if(!validate()) {
            return;
        }

        if(filePathUri != null) {

            progressDialog.show();

            StorageReference finalStorageReference = storageReference.child(storagePath + user.getUid() + "." + getFileExtension(filePathUri));

            finalStorageReference.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Upload gambar berhasil", Toast.LENGTH_SHORT).show();

                            finalStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    addToDatabase(namaEditText.getText().toString(), noHpEditText.getText().toString(), uri.toString());
                                }
                            });

                            UserFragment2 newFragment = new UserFragment2();
                            Bundle args = new Bundle();
                            newFragment.setArguments(args);

                            UserActivity.currentPosition = 2;

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                            transaction.replace(R.id.fragment_container, newFragment);
                            transaction.addToBackStack(null);

                            transaction.commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                            processSubmit = false;
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Mengupload gambar...");
                        }
                    });
        }
    }

    public void addToDatabase(String nama, String noHp, String imageUrl) {
        Map<String, Object> userAdd = new HashMap<>();
        userAdd.put("nama", nama);
        userAdd.put("no_hp", noHp);
        userAdd.put("profile_url", imageUrl);
        userAdd.put("user_id", user.getUid());
        userAdd.put("email", user.getEmail());
        userAdd.put("jenis_kelamin", "");
        userAdd.put("usia", 0);
        userAdd.put("tinggi_badan", 0);
        userAdd.put("berat_badan", 0);
        userAdd.put("aktifitas_harian", "");

        db.collection("users").document(user.getUid().toString())
                .set(userAdd)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Berhasil Menambahkan data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Gagal Menambahkan data", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean validate() {
        boolean valid = true;

        if(filePathUri == null) {
            Toast.makeText(getActivity(), "Silakan pilih gambar yang ingin diupload!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        String namaField = namaEditText.getText().toString();
        if(TextUtils.isEmpty(namaField)) {
            namaEditText.setError("Silakan isi nama lengkap anda!");
            valid = false;
        } else {
            namaEditText.setError(null);
        }


        String noHpField = noHpEditText.getText().toString();
        if(TextUtils.isEmpty(noHpField)) {
            noHpEditText.setError("Silakan isi nomor handphone anda!");
            valid = false;
        } else {
            namaEditText.setError(null);
        }

        return valid;
    }
}
