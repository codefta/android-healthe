package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageReference;

    EditText namaEditText, noHpEditText;
    Uri filePathUri;
    EditText usia, tb, bb;
    RadioGroup gender;
    RadioButton genderSelected;
    ImageView profileFoto;
    Button submit;
    CircleImageView chooseImage;

    int imageRequestCode = 7;
    ProgressDialog progressDialog;
    String storagePath = "profile_image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        setEditProfilView();

        profileFoto = findViewById(R.id.profile_photo);
        namaEditText = findViewById(R.id.input_nama);
        noHpEditText = findViewById(R.id.input_nohp);
        usia = findViewById(R.id.input_usia);
        tb = findViewById(R.id.input_tb);
        bb = findViewById(R.id.input_bb);
        gender = findViewById(R.id.gender);

        submit = findViewById(R.id.submit_datadiri);
        chooseImage = findViewById(R.id.profile_photo);

        progressDialog = new ProgressDialog(ProfileUpdateActivity.this);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Silakan pilih gambar"), imageRequestCode);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEditProfilView() {

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
                    Picasso.get().load(snapshot.get("profile_url").toString()).into(profileFoto);
                    if(snapshot.get("jenis_kelamin").toString() == "Laki-laki") {
                        gender.check(R.id.gender_pria);
                    } else {
                        gender.check(R.id.gender_pria);
                    }
                    namaEditText.setText(snapshot.get("nama").toString());
                    noHpEditText.setText(snapshot.get("no_hp").toString());
                    usia.setText(snapshot.get("usia").toString());
                    tb.setText(snapshot.get("tinggi_badan").toString());
                    bb.setText(snapshot.get("berat_badan").toString());
                    bb.setText(snapshot.get("berat_badan").toString());

                } else {
                    Toast.makeText(ProfileUpdateActivity.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == imageRequestCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.getData();

            Picasso.get().load(filePathUri).into(profileFoto);
            Toast.makeText(ProfileUpdateActivity.this, "Gambar Terpilih", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {


        if(filePathUri != null) {
            progressDialog.setTitle("Sedang mengupload gambar...");

            progressDialog.show();

            StorageReference finalStorageReference = storageReference.child(storagePath + user.getUid() + "." + getFileExtension(filePathUri));

            finalStorageReference.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileUpdateActivity.this, "Upload gambar berhasil", Toast.LENGTH_SHORT).show();

                            int selectedGender = gender.getCheckedRadioButtonId();

                            genderSelected = (RadioButton) findViewById(selectedGender);

                            finalStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    addToDatabase(namaEditText.getText().toString(), noHpEditText.getText().toString(), uri.toString(), genderSelected.getText().toString(), Integer.parseInt(usia.getText().toString()), Integer.parseInt(tb.getText().toString()), Integer.parseInt(bb.getText().toString()));
                                }
                            });

                            Toast.makeText(ProfileUpdateActivity.this, "Anda berhasil Mengupdate data", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileUpdateActivity.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Mengupload gambar...");
                        }
                    });
        } else {
            addToDatabase(namaEditText.getText().toString(), noHpEditText.getText().toString(),null, genderSelected.getText().toString(), Integer.parseInt(usia.getText().toString()), Integer.parseInt(tb.getText().toString()), Integer.parseInt(bb.getText().toString()));
        }
    }

    public void addToDatabase(String nama, String noHp, String imageUrl, String gender, int usia, int tb, int bb) {
        Map<String, Object> userAdd = new HashMap<>();
        userAdd.put("nama", nama);
        userAdd.put("no_hp", noHp);
        if(imageUrl != null) {
            userAdd.put("profile_url", imageUrl);
        }
        userAdd.put("user_id", user.getUid());
        userAdd.put("email", user.getEmail());
        userAdd.put("jenis_kelamin", gender);
        userAdd.put("usia", usia);
        userAdd.put("tinggi_badan", tb);
        userAdd.put("berat_badan", bb);

        db.collection("users").document(user.getUid().toString())
                .set(userAdd)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileUpdateActivity.this, "Berhasil Mengupdate data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileUpdateActivity.this, "Gagal Mengupdate data", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
