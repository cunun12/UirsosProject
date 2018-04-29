package com.uirsos.www.uirsosproject.POJO;

/**
 * Created by cunun12 on 22/04/2018.
 */

public class KategoriFakultas {

    private String idFakultas;
    private String nama_inisial;
    private String nama_fakutlas;

    public KategoriFakultas(){

    }

    public KategoriFakultas(String idFakultas, String nama_inisial, String nama_fakutlas) {
        this.idFakultas = idFakultas;
        this.nama_inisial = nama_inisial;
        this.nama_fakutlas = nama_fakutlas;
    }

    public String getIdFakultas() {
        return idFakultas;
    }

    public void setIdFakultas(String idFakultas) {
        this.idFakultas = idFakultas;
    }

    public String getNama_inisial() {
        return nama_inisial;
    }

    public void setNama_inisial(String nama_inisial) {
        this.nama_inisial = nama_inisial;
    }

    public String getNama_fakutlas() {
        return nama_fakutlas;
    }

    public void setNama_fakutlas(String nama_fakutlas) {
        this.nama_fakutlas = nama_fakutlas;
    }
}
