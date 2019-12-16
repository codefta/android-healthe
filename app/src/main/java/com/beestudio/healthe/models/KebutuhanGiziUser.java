package com.beestudio.healthe.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Map;

public class KebutuhanGiziUser {

    FirebaseUser user;
    FirebaseFirestore db;
    Map kandunganProtein;
    Map kandunganLemak;
    Map kandunganKarbohidrat;

    public KebutuhanGiziUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public double hitungBBI(String gender, int tinggiBadan, int usia, boolean isBayi) {
        if(usia >= 10) {
            if((TextUtils.equals(gender, "Laki-laki") && tinggiBadan < 160) || (TextUtils.equals(gender, "Perempuan") && tinggiBadan < 150)) {
                return tinggiBadan - 100.0;
            } else {
                return 0.9 * (tinggiBadan - 100.0);
            }
        } else {
            if(isBayi) {
                return (usia / 2.0) + 4;
            } else {
                return (usia * 2.0) + 8;
            }
        }
    }

    public double hitungBMR(String gender, int tinggiBadan, int beratBadanAktual, double beratBadanIdeal, int usia, boolean isBayi) {
        double kegemukan = beratBadanIdeal + (0.1) * beratBadanIdeal;

        if(isBayi) {
            if(beratBadanAktual > kegemukan) {
                return 22.1 + 31.05 * beratBadanIdeal + 1.16 * tinggiBadan;
            } else {
                return 22.1 + 31.05 * beratBadanAktual + 1.16 * tinggiBadan;
            }
        } else if((TextUtils.equals(gender, "Perempuan"))) {
            if(beratBadanAktual > kegemukan) {
                return 66.5 + 13.8 * beratBadanIdeal + 5.0 * tinggiBadan - 6.8 * usia;
            } else {
                return 66.5 + 13.8 * beratBadanAktual + 5.0 * tinggiBadan - 6.8 * usia;
            }
        } else {
            if (beratBadanAktual > kegemukan) {
                return 655.1 + 9.6 * beratBadanIdeal  + 1.9 * tinggiBadan - 4.7 * usia;
            } else {
                return 655.1 + 9.6 * beratBadanAktual  + 1.9 * tinggiBadan - 4.7 * usia;
            }
        }
    }

    public double hitungTEE(double bobotAktifitas, double bobotStres, double totalBMR) {
        return  totalBMR * bobotAktifitas * bobotStres;
    }

    public double hitungProtein(double totalTEE, double kebutuhanHarianProtein, double bobotKandunganProtein) {

        return kebutuhanHarianProtein * totalTEE / bobotKandunganProtein;
    }

    public double hitungLemak(double totalTEE, double kebutuhanHarianLemak, double bobotKandunganLemak) {

        return kebutuhanHarianLemak * totalTEE / bobotKandunganLemak;
    }

    public double hitungKarbohidrat(double totalTEE, double kebutuhanHarianKarbohidrat, double bobotKandunganKarbohidrat) {
        return kebutuhanHarianKarbohidrat* totalTEE / bobotKandunganKarbohidrat;
    }

}
