package com.uirsos.www.uirsosproject.POJO;

/**
 * Created by cunun12
 * POJO (Plain Old Java Object)
 * Disini tempat semua variable disimpan maupun di panggil
 */

public class DataItemBerita {

    /*Varible Berita*/
    private String tanggal, hari, img, fakultas, judul, isi_berita; //untuk postingan Berita

    public DataItemBerita() {
    }

    /**
     * Constructor untuk menginisialisasikan sebuah variable Berita
     * @param tanggal
     * @param hari
     * @param img
     * @param fakultas
     * @param judul
     * @param deskripsi
     * @param isi_berita
     */
    public DataItemBerita(String tanggal, String hari, String img, String fakultas, String judul, String deskripsi, String isi_berita) {
        this.tanggal = tanggal;
        this.hari = hari;
        this.img = img;
        this.fakultas = fakultas;
        this.judul = judul;
        this.isi_berita = isi_berita;
    }

    /**
     * Getter and Setter untuk mengambil dan Mengembalikan isi dari variable Berita
     */
    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi_berita() {
        return isi_berita;
    }

    public void setIsi_berita(String isi_berita) {
        this.isi_berita = isi_berita;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }
    /*Batas variabel Berita*/

}
