package com.beestudio.healthe;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class UserActivity extends FragmentActivity {

    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageReference;
    private Button btnSubmit;
    String storagePath = "profile_image/";
    int imageRequestCode = 7;
    Boolean processSubmit = false;

    Button chooseButton;
    EditText namaEditText, tglLahirEditText, tbEditText, bbEditText;
    ImageView selectImage;
    Uri filePathUri;
    RadioGroup gender;
    RadioButton genderSelected;
    ProgressDialog progressDialog;
    final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(UserActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        chooseButton = findViewById(R.id.btn_pilih_image);
        selectImage = findViewById(R.id.show_image);

        btnSubmit = findViewById(R.id.submit_datadiri);
        namaEditText = findViewById(R.id.input_nama);
        tglLahirEditText = findViewById(R.id.input_tgl_lahir);
        tbEditText = findViewById(R.id.input_tb);
        bbEditText = findViewById(R.id.input_bb);
        gender = findViewById(R.id.gender);

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
                new DatePickerDialog(UserActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
                int selectedGender = gender.getCheckedRadioButtonId();
                genderSelected = findViewById(selectedGender);
                String gender = genderSelected.getText().toString();
                uploadImage(gender);

            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        tglLahirEditText.setText(sdf.format(myCalendar.getTime()));
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
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage(String gender) {

        if(!validate()) {
            return;
        }

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
                                    boolean isAnak = isAnak(tglLahir);
                                    String profil_url = uri.toString();
                                    Map<String, Object> userData= new HashMap<>();
                                    userData.put("userId", userId);
                                    userData.put("nama", nama);
                                    userData.put("jenisKelamin", jenisKelamin);
                                    userData.put("tglLahir", tglLahir);
                                    userData.put("tinggiBadan", tb);
                                    userData.put("beratBadan", bb);
                                    userData.put("isBayi", isBayi);
                                    userData.put("isAnak", isAnak);
                                    userData.put("profilUrl", profil_url);

                                    Map<String, Object> jenisAktifitasObj = new HashMap<>();
                                    jenisAktifitasObj.put("bobot", 1.2);
                                    jenisAktifitasObj.put("tingkatAktifitas", "Istirahat");
                                    jenisAktifitasObj.put("keterangan", "Istirahat di rumah");
                                    userData.put("aktifitas", jenisAktifitasObj);

                                    Map<String, Object> jenisStresObj = new HashMap<>();
                                    jenisStresObj.put("bobot", 1.2);
                                    jenisStresObj.put("tingkatStres", "Normal");
                                    jenisStresObj.put("keterangan", "Tidak stres, normal");

                                    userData.put("stres", jenisStresObj);

                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(UserActivity.this, "Berhasil Menambahkan data", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UserActivity.this, "Gagal Menambahkan data", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    progressDialog.dismiss();
                                    Toast.makeText(UserActivity.this, "Upload gambar berhasil", Toast.LENGTH_SHORT).show();
                                    Intent intent = new  Intent(UserActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserActivity.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
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


    private boolean validate() {
        boolean valid = true;

        if(filePathUri == null) {
            Toast.makeText(UserActivity.this, "Silakan pilih gambar yang ingin diupload!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        String namaField = namaEditText.getText().toString();
        if(TextUtils.isEmpty(namaField)) {
            namaEditText.setError("Silakan isi nama lengkap anda!");
            valid = false;
        } else {
            namaEditText.setError(null);
        }


        String tglLahir = tglLahirEditText.getText().toString();
        if(TextUtils.isEmpty(tglLahir)) {
            tglLahirEditText.setError("Silakan isi tanggal lahir anda!");
            valid = false;
        } else {
            tglLahirEditText.setError(null);
        }

        String tb = tbEditText.getText().toString();
        if(TextUtils.isEmpty(tb)) {
            tbEditText.setError("Silakan isi tinggi badan anda!");
            valid = false;
        } else {
            tbEditText.setError(null);
        }

        String bb = bbEditText.getText().toString();
        if(TextUtils.isEmpty(bb)) {
            bbEditText.setError("Silakan isi berat badan anda!");
            valid = false;
        } else {
            bbEditText.setError(null);
        }

        return valid;
    }


    private boolean isAnak(String birthday) {
        DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(birthday, df);
        LocalDate today = LocalDate.now();
        int usia = Years.yearsBetween(localDate,today).getYears();

        if(usia > 1 && usia < 10) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isBayi(String birthday) {
        DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy");
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
