package com.beestudio.healthe.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RekomendasiMakanan {

    FirebaseUser user;
    FirebaseFirestore db;

    public double hitungMakanPagi(double tee, double bobotMakanPagi) {
        return bobotMakanPagi * tee;
    }

    public double hitungMakanSiang(double tee, double bobotMakanSiang ) {
        return bobotMakanSiang * tee;
    }

    public double hitungMakanSnack(double tee, double bobotMakanSnack) {
        return bobotMakanSnack * tee;
    }

    public double hitungMakanMalam(double tee, double bobotMakanMalam) {
        return bobotMakanMalam * tee;
    }
}
