package com.example.android.arkanoid.entity;


public class Messages {
    private String utente;
    private String testo;
    private String id;


    public Messages(String utente, String testo, String id) {
        this.utente = utente;
        this.testo = testo;
        this.id = id;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}