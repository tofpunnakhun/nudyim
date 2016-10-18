package com.ayp.nudyim.photo;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class Photo {

    private String name;
    private String imageProfile;
    private String photoURL;

    public Photo(){

    }
    public Photo(String name, String photoURL, String imageProfile){
        this.name = name;
        this.photoURL = photoURL;
        this.imageProfile = imageProfile;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }
}
