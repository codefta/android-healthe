package com.beestudio.healthe.models;

public class Stres {

    private String tingkatStres;
    private double bobot;
    private String keterangan;

    public Stres() {}

    public Stres(String tingkatStres, double bobot, String keterangan) {
        this.tingkatStres = tingkatStres;
        this.bobot = bobot;
        this.keterangan = keterangan;
    }

    public String getTingkatStres() {
        return tingkatStres;
    }

    public void setTingkatStres(String tingkatStres) {
        this.tingkatStres = tingkatStres;
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
