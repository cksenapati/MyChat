package com.example.android.mychat.models;

import android.support.annotation.NonNull;

import com.example.android.mychat.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 08-03-2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat implements Serializable , Comparable<Chat>  {
    public boolean online;
    public String chatName,chatEmail,chatType,chatId,chatPhotoURL,reactionToProfilePic;
    public int noOfMessagesIHaveSeen;
    public HashMap<String, Object> timestampLastSeen;
    public HashMap<String, Object> timestampLastUpdate;


    @Override
    public int compareTo(@NonNull Chat other) {
       long thisTimestamp = (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
        long otherTimestamp = (long) other.timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);

        return (int)(otherTimestamp - thisTimestamp);
    }

    public Chat() {
    }



    public Chat(String chatName,String chatEmail, String chatType,String chatId,String chatPhotoURL, int noOfMessagesIHaveSeen,boolean online,HashMap<String, Object> timestampLastUpdate,HashMap<String, Object> timestampLastSeen) {
        this.chatName = chatName;
        this.chatEmail = chatEmail;
        this.chatType = chatType;
        this.noOfMessagesIHaveSeen = noOfMessagesIHaveSeen;
        this.chatId = chatId;
        this.online = online;
        this.chatPhotoURL = chatPhotoURL;
        this.timestampLastUpdate = timestampLastUpdate;
        this.timestampLastSeen = timestampLastSeen;
        this.reactionToProfilePic = null;
    }

    public String getChatName() {
        return chatName;
    }

    public String getChatEmail() {
        return chatEmail;
    }

    public String getChatType() {
        return chatType;
    }


    public String getChatId() {
        return chatId;
    }

    public String getChatPhotoURL() {
        return chatPhotoURL;
    }

    public int getNoOfMessagesIHaveSeen() {
        return noOfMessagesIHaveSeen;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getReactionToProfilePic() {
        return reactionToProfilePic;
    }



    public void setNoOfMessagesIHaveSeen(int noOfMessagesIHaveSeen) {
        this.noOfMessagesIHaveSeen = noOfMessagesIHaveSeen;
    }

    public void setChatPhotoURL(String chatPhotoURL) {
        this.chatPhotoURL = chatPhotoURL;
    }



    public HashMap<String, Object> getTimestampLastSeen() {
        return timestampLastSeen;
    }

    @JsonIgnore
    public long getTimestampLastSeenLong() {

        return (long) timestampLastSeen.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampLastSeen(HashMap<String, Object> timestampLastSeen) {
        this.timestampLastSeen = timestampLastSeen;
    }

    public HashMap<String, Object> getTimestampLastUpdate() {
        return timestampLastUpdate;
    }

    @JsonIgnore
    public long getTimestampLastUpdateLong() {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }



    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setChatEmail(String chatEmail) {
        this.chatEmail = chatEmail;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setReactionToProfilePic(String reactionToProfilePic) {
        this.reactionToProfilePic = reactionToProfilePic;
    }
}
