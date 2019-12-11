package com.beestudio.healthe.models;

public class Aktifitas {

    private String tingkatAktifitas;
    private double bobot;
    private String keterangan;

    public Aktifitas() {}

    public Aktifitas(String tingkatAktifitas, double bobot, String keterangan) {

    }

    public String getTingkatAktifitas() {
        return tingkatAktifitas;
    }

    public void setTingkatAktifitas(String tingkatAktifitas) {
        this.tingkatAktifitas = tingkatAktifitas;
    }

    public double getBobot() {
        return bobot;
    }

    public void setBobot(double bobot) {
        this.bobot = bobot;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
