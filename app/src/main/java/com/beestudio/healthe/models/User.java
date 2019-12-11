package com.beestudio.healthe.models;

import java.util.Date;

public class User {

    private String userId;
    private String nama;
    private String jenisKelamin;
    private Date tglLahir;
    private int beratBadan;
    private int tinggiBadan;
    private boolean isBayi;
    private boolean isAnak;
    private String jenisAktifitas;
    private String jenisStres;
    private String profilUrl;

    public User() {}

    public User(String userId, String nama, String jenisKelamin, Date tglLahir, int beratBadan, int tinggiBadan, boolean isBayi, boolean isAnak, String jenisAktifitas, String jenisStres, String profilUrl) {
        this.userId = userId;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.tglLahir = tglLahir;
        this.beratBadan = beratBadan;
        this.tinggiBadan = tinggiBadan;
        this.isBayi = isBayi;
        this.isAnak = isAnak;
        this.jenisAktifitas = jenisAktifitas;
        this.jenisStres = jenisStres;
        this.profilUrl = profilUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public Date getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(Date tglLahir) {
        this.tglLahir = tglLahir;
    }

    public int getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(int beratBadan) {
        this.beratBadan = beratBadan;
    }

    public int getTinggiBadan() {
        return tinggiBadan;
    }

    public void setTinggiBadan(int tinggiBadan) {
        this.tinggiBadan = tinggiBadan;
    }

    public boolean isBayi() {
        return isBayi;
    }

    public void setBayi(boolean bayi) {
        isBayi = bayi;
    }

    public boolean isAnak() {
        return isAnak;
    }

    public void setAnak(boolean anak) {
        isAnak = anak;
    }

    public String getJenisAktifitas() {
        return jenisAktifitas;
    }

    public void setJenisAktifitas(String jenisAktifitas) {
        this.jenisAktifitas = jenisAktifitas;
    }

    public String getJenisStres() {
        return jenisStres;
    }

    public void setJenisStres(String jenisStres) {
        this.jenisStres = jenisStres;
    }

    public String getProfilUrl() {
        return profilUrl;
    }

    public void setProfilUrl(String profilUrl) {
        this.profilUrl = profilUrl;
    }
}
