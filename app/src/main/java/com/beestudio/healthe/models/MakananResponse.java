package com.beestudio.healthe.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MakananResponse {

    private String nama;
    private int jumlahKalori;
    private double jumlahKarbohidrat;
    private double jumlahProtein;
    private double jumlahLemak;
    private String jenis;
    private String imageUrl;
    private String linkUrl;

    public MakananResponse() {
    }

    public MakananResponse(String nama, int jumlahKalori, double jumlahKarbohidrat, double jumlahProtein, double jumlahLemak, String jenis, String imageUrl, String linkUrl) {
        this.nama = nama;
        this.jumlahKalori = jumlahKalori;
        this.jumlahKarbohidrat = jumlahKarbohidrat;
        this.jumlahProtein = jumlahProtein;
        this.jumlahLemak = jumlahLemak;
        this.jenis = jenis;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getJumlahKalori() {
        return jumlahKalori;
    }

    public void setJumlahKalori(int jumlahKalori) {
        this.jumlahKalori = jumlahKalori;
    }

    public double getJumlahKarbohidrat() {
        return jumlahKarbohidrat;
    }

    public void setJumlahKarbohidrat(double jumlahKarbohidrat) {
        this.jumlahKarbohidrat = jumlahKarbohidrat;
    }

    public double getJumlahProtein() {
        return jumlahProtein;
    }

    public void setJumlahProtein(double jumlahProtein) {
        this.jumlahProtein = jumlahProtein;
    }

    public double getJumlahLemak() {
        return jumlahLemak;
    }

    public void setJumlahLemak(double jumlahLemak) {
        this.jumlahLemak = jumlahLemak;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
