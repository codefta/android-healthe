package com.beestudio.healthe.models;

import android.text.TextUtils;

public class KebutuhanGiziUser {


    public KebutuhanGiziUser() {

    }

    public double hitungBBI(String gender, int tinggiBadan, int usia, boolean isBayi) {
        if(usia >= 10) {
            if((gender == "Laki-laki" && tinggiBadan < 160) || (gender == "Perempuan" && tinggiBadan < 150)) {
                return tinggiBadan - 100.0;
            } else {
                return (90 / 100.0) * (tinggiBadan - 100.0);
            }
        } else {
            if(isBayi == true) {
                return (usia / 2.0) + 4;
            } else {
                return (usia * 2.0) + 8;
            }
        }
    }

    public double hitungBMR(String gender, int tinggiBadan, int beratBadanAktual, double beratBadanIdeal, int usia, boolean isBayi) {
        double kegemukan = beratBadanIdeal + (10.0 / 100.0) * beratBadanIdeal;

        if(isBayi) {
            if(beratBadanAktual > kegemukan) {
                return 22.1 + 31.05 * beratBadanIdeal + 1.16 * tinggiBadan;
            } else {
                return 22.1 + 31.05 * beratBadanAktual + 1.16 * tinggiBadan;
            }
        } else if(gender == "Perempuan") {
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

    public double hitungProtein(double totalTEE) {
        return ((15.0 / 100.0) * totalTEE) / 4.0;
    }

    public double hitungLemak(double totalTEE) {
        return ((25.0 /100.0) * totalTEE) / 9.0;
    }

    public double hitungKarbohidrat(double totalTEE) {
        return ((60.0 /100.0) * totalTEE) / 4.0;
    }

}
