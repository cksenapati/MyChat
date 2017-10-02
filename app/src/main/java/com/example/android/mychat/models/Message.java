package com.example.android.mychat.models;

import com.example.android.mychat.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by chandan on 09-03-2017.
 */

public class Message implements Serializable {
    String messageText,sentBy,senderEmail,status,messageId,mediaFileType;
    private HashMap<String, Object> timestampMessageSentAt;
    private String mediaFileURL;
    String visibleTo;

    public Message() {
    }


    public Message(String messageText,String messageId,String status, String sentBy, String senderEmail,String visibleTo, HashMap<String, Object> timestampMessageSentAt, String mediaFileURL,String mediaFileType) {
        this.messageText = messageText;
        this.status = status;
        this.messageId = messageId;
        this.sentBy = sentBy;
        this.senderEmail = senderEmail;
        this.timestampMessageSentAt = timestampMessageSentAt;
        this.mediaFileURL = mediaFileURL;
        this.mediaFileType = mediaFileType;
        this.visibleTo=visibleTo;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getStatus() {
        return status;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMediaFileURL() {
        return mediaFileURL;
    }

    public String getMediaFileType() {
        return mediaFileType;
    }

    public String getVisibleTo() {
        return visibleTo;
    }

    public HashMap<String, Object> getTimestampMessageSentAt() {
        return timestampMessageSentAt;
    }

    @JsonIgnore
    public long getTimestampMessageSentAtLong() {

        return (long) timestampMessageSentAt.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVisibleTo(String visibleTo) {
        this.visibleTo = visibleTo;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMediaFileType(String mediaFileType) {
        this.mediaFileType = mediaFileType;
    }

    public void setTimestampMessageSentAt(HashMap<String, Object> timestampMessageSentAt) {
        this.timestampMessageSentAt = timestampMessageSentAt;
    }

    public void setMediaFileURL(String mediaFileURL) {
        this.mediaFileURL = mediaFileURL;
    }
}
