package com.example.android.mychat.models;

/**
 * Created by chandan on 03-05-2017.
 */

public class MyChatNotification {
    private String notificationText;
    private String notificationFromChatId,notificationFromChatName;
    private  int notificationFromNoOfChats, noOfNewMessages;

    public MyChatNotification() {
    }

    public MyChatNotification(String notificationText, String notificationFromChatId, String notificationFromChatName, int notificationFromNoOfChats, int noOfNewMessages) {
        this.notificationText = notificationText;
        this.notificationFromChatId = notificationFromChatId;
        this.notificationFromChatName = notificationFromChatName;
        this.notificationFromNoOfChats = notificationFromNoOfChats;
        this.noOfNewMessages = noOfNewMessages;
    }

    //Setter

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public void setNotificationFromChatId(String notificationFromEmail) {
        this.notificationFromChatId = notificationFromEmail;
    }

    public void setNotificationFromChatName(String notificationFromChatName) {
        this.notificationFromChatName = notificationFromChatName;
    }



    public void setNotificationFromNoOfChats(int notificationFromNoOfChats) {
        this.notificationFromNoOfChats = notificationFromNoOfChats;
    }

    public void setNoOfNewMessages(int noOfNewMessages) {
        this.noOfNewMessages = noOfNewMessages;
    }



    //Getter

    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationFromChatId() {
        return notificationFromChatId;
    }

    public String getNotificationFromChatName() {
        return notificationFromChatName;
    }


    public int getNotificationFromNoOfChats() {
        return notificationFromNoOfChats;
    }

    public int getNoOfNewMessages() {
        return noOfNewMessages;
    }
}

