package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageReference;
    private Button btnSubmit;
    String storagePath = "profile_image/";
    int imageRequestCode = 7;
    Boolean processSubmit = false;
    ImageView profileFoto;
    CircleImageView chooseImage;
    Button chooseButton;
    EditText namaEditText, tglLahirEditText, tbEditText, bbEditText;
    Uri filePathUri;
    RadioGroup gender;
    RadioButton genderSelected;

    ProgressDialog progressDialog;
    DatePickerDialog datePicker;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(ProfileUpdateActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        btnSubmit = findViewById(R.id.submit_datadiri);
        namaEditText = findViewById(R.id.input_nama);
        tglLahirEditText = findViewById(R.id.input_tgl_lahir);
        tbEditText = findViewById(R.id.input_tb);
        bbEditText = findViewById(R.id.input_bb);
        gender = findViewById(R.id.gender);
        profileFoto = findViewById(R.id.profile_photo);
        chooseImage = findViewById(R.id.profile_photo);

        loadProfile();


        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Silakan pilih gambar"), imageRequestCode);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        tglLahirEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileUpdateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGender = gender.getCheckedRadioButtonId();
                genderSelected = findViewById(selectedGender);
                String gender = genderSelected.getText().toString();
                uploadImage(gender);

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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        tglLahirEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private void loadProfile() {

        db.collection("users").document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";

                        if (snapshot != null && snapshot.exists()) {
                            Glide.with(ProfileUpdateActivity.this).load(snapshot.get("profilUrl").toString()).centerCrop().into(profileFoto);
                            if(snapshot.get("jenisKelamin").toString() == "Laki-laki") {
                                gender.check(R.id.gender_pria);
                            } else {
                                gender.check(R.id.gender_wanita);
                            }

                            namaEditText.setText(snapshot.get("nama").toString());
                            bbEditText.setText(snapshot.get("beratBadan").toString());
                            tbEditText.setText(snapshot.get("tinggiBadan").toString());
                            tglLahirEditText.setText(snapshot.get("tglLahir").toString());

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

    public void uploadImage(String gender) {

        if(filePathUri != null) {

            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            StorageReference finalStorageReference = storageReference.child(storagePath + user.getUid() + "." + getFileExtension(filePathUri));

            finalStorageReference.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            finalStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String userId = user.getUid();
                                    String nama = namaEditText.getText().toString();
                                    String jenisKelamin = gender;
                                    String tglLahir = tglLahirEditText.getText().toString();
                                    int tb = Integer.parseInt(tbEditText.getText().toString());
                                    int bb = Integer.parseInt(bbEditText.getText().toString());
                                    boolean isBayi = isBayi(tglLahir);
                                    String profil_url = uri.toString();
                                    Map<String, Object> userData= new HashMap<>();
                                    userData.put("userId", userId);
                                    userData.put("nama", nama);
                                    userData.put("jenisKelamin", jenisKelamin);
                                    userData.put("tglLahir", tglLahir);
                                    userData.put("tinggiBadan", tb);
                                    userData.put("beratBadan", bb);
                                    userData.put("isBayi", isBayi);
                                    userData.put("profilUrl", profil_url);

                                    db.collection("users").document(user.getUid())
                                            .update(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ProfileUpdateActivity.this, "Berhasil Mengubah data", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProfileUpdateActivity.this, "Gagal menghubah data", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileUpdateActivity.this, "Upload gambar berhasil", Toast.LENGTH_SHORT).show();
                                    Intent intent = new  Intent(ProfileUpdateActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileUpdateActivity.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                            processSubmit = false;
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Mengupload gambar...");
                        }
                    });
        } else {
            Toast.makeText(ProfileUpdateActivity.this, gender, Toast.LENGTH_SHORT).show();
            String userId = user.getUid();
            String nama = namaEditText.getText().toString();
            String jenisKelamin = gender;
            String tglLahir = tglLahirEditText.getText().toString();
            int tb = Integer.parseInt(tbEditText.getText().toString());
            int bb = Integer.parseInt(bbEditText.getText().toString());
            boolean isBayi = isBayi(tglLahir);
            Map<String, Object> userData= new HashMap<>();
            userData.put("userId", userId);
            userData.put("nama", nama);
            userData.put("jenisKelamin", jenisKelamin);
            userData.put("tglLahir", tglLahir);
            userData.put("tinggiBadan", tb);
            userData.put("beratBadan", bb);
            userData.put("isBayi", isBayi);

            db.collection("users").document(user.getUid())
                    .update(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileUpdateActivity.this, "Berhasil Mengubah data", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileUpdateActivity.this, "Gagal Mengubah data", Toast.LENGTH_SHORT).show();
                        }
                    });

            progressDialog.dismiss();
        }
    }

    private boolean isBayi(String birthday) {
        DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy");
        //convert String to LocalDate
        LocalDate localDate = LocalDate.parse(birthday, df);
        LocalDate today = LocalDate.now();
        int usia = Months.monthsBetween(localDate,today).getMonths();

        if(usia >= 0 && usia <= 12) {
            return true;
        } else {
            return false;
        }
    }
}
