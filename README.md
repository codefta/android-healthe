### Menggunakan Firebase Firestore

1. Silakan copy kode di bawah ini ke dalam `gradle:module app`
```
implementation 'com.google.firebase:firebase-firestore:21.3.0'
```

2. Instansiasi objek untuk memanggil firestore
```
FirebaseFirestore db;
db = FirebaseFirestore.getInstance();
```

3. Contoh add data di `UserFragment.java`
```
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
```

4. Contoh get data secara Realtime di `ProfileFragment.java`
```
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

                    profileNama.setText(snapshot.get("nama").toString());
                    profileGender.setText(snapshot.get("jenis_kelamin").toString());
                    profileHeight.setText(snapshot.get("tinggi_badan").toString());
                    profileWeight.setText(snapshot.get("berat_badan").toString());
                    profileAge.setText(snapshot.get("usia").toString());

                } else {
                    Toast.makeText(getActivity(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

5. Contoh Update data di `ProfileAccountSettingActivityjava`
```
user.updateEmail(email)
    .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                updateAkunDb(email);
                Toast.makeText(ProfileAccountSettingActivity.this, "Email Telah diubah", Toast.LENGTH_LONG).show();
            }
        }
    });
```


###Cara Mengambil data dari EditText, Spinner, dan RadioButton

1. Deklarasikan dulu type data untuk setiap variabel
```
EditText usia, tb, bb;
RadioGroup gender;
RadioButton genderSelected;
Spinner spinner;
Button submit;
```

2. Assigment nilai untuk setiap id di xmlnya
```
usia = view.findViewById(R.id.input_usia);
tb = view.findViewById(R.id.input_tb);
bb = view.findViewById(R.id.input_bb);
submit = view.findViewById(R.id.submit_datadiri);

// RadioButton
gender = view.findViewById(R.id.gender);

// Spinner

spinner = view.findViewById(R.id.aktifitas_spinner);
ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(), R.array.bobot_aktifitas, android.R.layout.simple_spinner_item);
adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item);
spinner.setAdapter(adapterSpinner);
```

3. Mengambil data ketika button di click (lakukan di onCreate)
```
String aktifitasFisik;

spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).toString() != "Pilih Aktifitas") {
            aktifitasFisik = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});
submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGender = gender.getCheckedRadioButtonId();
                genderSelected = (RadioButton) findViewById(selectedGender);
                String gender = genderSelected.getText().toString()



                String usia = Integer.parseInt(usia.getText().toString())
                ...
            }
        });
```
