package com.example.android.mychat.models;

import com.example.android.mychat.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 01-04-2017.
 */
public class ChatDetails implements Serializable {
    private String lastMessage, whoAreTyping;
    private int totalNoOfMessagesAvailable;
    private HashMap<String, Object> timestampLastUpdate;

    public ChatDetails() {
    }

    public ChatDetails(String lastMessage, int totalNoOfMessagesAvailable, HashMap<String, Object> timestampLastUpdate) {
        this.lastMessage = lastMessage;
        this.totalNoOfMessagesAvailable = totalNoOfMessagesAvailable;
        this.timestampLastUpdate = timestampLastUpdate;
        this.whoAreTyping = null;

    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTotalNoOfMessagesAvailable(int totalNoOfMessagesAvailable) {
        this.totalNoOfMessagesAvailable = totalNoOfMessagesAvailable;
    }

    public void setTimestampLastUpdate(HashMap<String, Object> timestampLastUpdate) {
        this.timestampLastUpdate = timestampLastUpdate;
    }

    public void setWhoAreTyping(String whoAreTyping) {
        this.whoAreTyping = whoAreTyping;
    }



    public String getLastMessage() {
        return lastMessage;
    }

    public int getTotalNoOfMessagesAvailable() {
        return totalNoOfMessagesAvailable;
    }

    public HashMap<String, Object> getTimestampLastUpdate() {
        return timestampLastUpdate;
    }

    @JsonIgnore
    public long getTimestampLastUpdateLong() {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public String getWhoAreTyping() {
        return whoAreTyping;
    }
}
