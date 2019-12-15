package com.beestudio.healthe.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RekomendasiMakanan {

    FirebaseUser user;
    FirebaseFirestore db;

    public double hitungMakanPagi(double tee) {
        return (20.0 / 100.0) * tee;
    }

    public double hitungMakanSiang(double tee) {
        return (30.0 / 100.0) * tee;
    }

    public double hitungMakanSnack(double tee) {
        return (10.0 / 100.0) * tee;
    }

    public double hitungMakanMalam(double tee) {
        return (20.0 / 100.0) * tee;
    }
}
