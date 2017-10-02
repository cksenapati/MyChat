package com.example.android.mychat.models;

import com.example.android.mychat.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 03-03-2017.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    private String name,email,photoURL;
    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    public User(String name, String email,String photoURL, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.photoURL = photoURL;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    @JsonIgnore
    public long getTimestampJoinedLong() {

        return (long) timestampJoined.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
