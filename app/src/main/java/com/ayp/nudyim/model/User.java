package com.ayp.nudyim.model;

/**
 * Created by Chaiwat on 10/5/2016.
 */

public class User {
    private String name;
    private String email;
    private String photoUrl;

    public User() {
        // Default constructor
    }

    public User(String name, String email, String photoUrl){
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
