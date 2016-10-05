package com.mrdo.nudyim.model;

/**
 * Created by Chaiwat on 10/5/2016.
 */

public class User {
    public String name;
    public String email;
    public String photoUrl;

    public User() {
        // Default constructor
    }

    public User(String name, String email, String photoUrl){
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
