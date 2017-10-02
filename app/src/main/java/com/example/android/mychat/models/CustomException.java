package com.example.android.mychat.models;

import java.util.HashMap;

/**
 * Created by chandan on 07-06-2017.
 */

public class CustomException {
    private String userEmail,createdAt,message;
    private HashMap<String, Object> timestampCreated;

    public CustomException() {
    }

    public CustomException(String userEmail, String createdAt, String message,HashMap<String, Object> timestampCreated) {
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.message = message;
        this.timestampCreated = timestampCreated;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
