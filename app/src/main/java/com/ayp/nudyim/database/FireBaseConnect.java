package com.ayp.nudyim.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Punnakhun on 10/20/2016.
 */

public class FireBaseConnect {

    private static FireBaseConnect instance;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    private String mUsername;
    private String mPhotoUrl;

    public static FireBaseConnect getInstance(){
        if (instance == null){
            instance = new FireBaseConnect();
        }
        return instance;
    }

    private FireBaseConnect(){
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth mFirebaseAuth) {
        this.mFirebaseAuth = mFirebaseAuth;
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public void setFirebaseUser(FirebaseUser mFirebaseUser) {
        this.mFirebaseUser = mFirebaseUser;
    }

    public DatabaseReference getFirebaseDatabaseReference() {
        return mFirebaseDatabaseReference;
    }

    public void setFirebaseDatabaseReference(DatabaseReference mFirebaseDatabaseReference) {
        this.mFirebaseDatabaseReference = mFirebaseDatabaseReference;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }
}
