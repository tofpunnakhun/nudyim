package com.ayp.nudyim.model;

/**
 * Created by Punnakhun on 10/19/2016.
 */

public class UserTrip {
    private String key;

    public UserTrip() {
        // Default constructor
    }

    public UserTrip(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
