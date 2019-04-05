package com.app.xeross.xerfirebase.models;

import android.support.annotation.Nullable;

/**
 * Created by XeroSs on 01/04/2019.
 */
public class User {

    private String uid;
    private String username;
    private Boolean isMentor;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isMentor = false;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public Boolean getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(Boolean mentor) {
        isMentor = mentor;
    }
}
