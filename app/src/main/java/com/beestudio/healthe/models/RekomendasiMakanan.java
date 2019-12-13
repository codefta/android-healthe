package com.beestudio.healthe.models;

public class RekomendasiMakanan {

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
