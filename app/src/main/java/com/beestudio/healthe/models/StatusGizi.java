package com.beestudio.healthe.models;

public class StatusGizi {

    private double batasBawah;
    private double batasAtas;
    private String kategori;
    private String keterangan;

    public StatusGizi() {}

    public StatusGizi(double batasBawah, double batasAtas, String kategori, String keterangan) {
        this.batasBawah = batasBawah;
        this.batasAtas = batasAtas;
        this.kategori = kategori;
        this.keterangan = keterangan;
    }

    public double getBatasBawah() {
        return batasBawah;
    }

    public void setBatasBawah(double batasBawah) {
        this.batasBawah = batasBawah;
    }

    public double getBatasAtas() {
        return batasAtas;
    }

    public void setBatasAtas(double batasAtas) {
        this.batasAtas = batasAtas;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
