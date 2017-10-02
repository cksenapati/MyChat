package com.example.android.mychat.models;

import com.example.android.mychat.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 03-03-2017.
 */
@SuppressWarnings("serial")
public class ReactableImage implements Serializable {

    private String pushId,name,photoURL,whoLiked,whoLaughed,whoLoved,whoDisliked;
    private int noOfLikes,noOfDislikes,noOfLaughs,noOfLoves;
    private HashMap<String, Object> timestampUploaded;

    public ReactableImage() {
    }

    public ReactableImage(String pushId,String name, String photoURL, int noOfLikes, int noOfDislikes, int noOfLaughs, int noOfLoves, HashMap<String, Object> timestampUploaded) {
        this.pushId = pushId;
        this.name = name;
        this.photoURL = photoURL;
        this.noOfLikes = noOfLikes;
        this.noOfDislikes = noOfDislikes;
        this.noOfLaughs = noOfLaughs;
        this.noOfLoves = noOfLoves;
        this.timestampUploaded = timestampUploaded;
        this.whoLiked = null;
        this.whoDisliked = null;
        this.whoLaughed = null;
        this.whoLoved = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public void setNoOfDislikes(int noOfDislikes) {
        this.noOfDislikes = noOfDislikes;
    }

    public void setNoOfLaughs(int noOfLaughs) {
        this.noOfLaughs = noOfLaughs;
    }

    public void setNoOfLoves(int noOfLoves) {
        this.noOfLoves = noOfLoves;
    }

    public void setTimestampUploaded(HashMap<String, Object> timestampUploaded) {
        this.timestampUploaded = timestampUploaded;
    }

    public void setWhoLiked(String whoLiked) {
        this.whoLiked = whoLiked;
    }

    public void setWhoLaughed(String whoLaughed) {
        this.whoLaughed = whoLaughed;
    }

    public void setWhoLoved(String whoLoved) {
        this.whoLoved = whoLoved;
    }

    public void setWhoDisliked(String whoDisliked) {
        this.whoDisliked = whoDisliked;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    //Getter


    public String getPushId() {
        return pushId;
    }

    public String getName() {
        return name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public int getNoOfDislikes() {
        return noOfDislikes;
    }

    public int getNoOfLaughs() {
        return noOfLaughs;
    }

    public int getNoOfLoves() {
        return noOfLoves;
    }

    public String getWhoLiked() {
        return whoLiked;
    }

    public String getWhoLaughed() {
        return whoLaughed;
    }

    public String getWhoLoved() {
        return whoLoved;
    }

    public String getWhoDisliked() {
        return whoDisliked;
    }

    public HashMap<String, Object> getTimestampUploaded() {
        return timestampUploaded;
    }

    @JsonIgnore
    public long getTimestampUploadedLong() {

        return (long) timestampUploaded.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
