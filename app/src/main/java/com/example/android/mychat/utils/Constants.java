package com.example.android.mychat.utils;

/**
 * Created by chandan on 03-03-2017.
 */
public final class Constants {

    //User properties
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE = "timestampLastUpdate";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_UPLOADED = "timestampUploaded";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_MESSAGE_SENT_AT= "timestampMessageSentAt";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_NAME = "name";
    public static final String FIREBASE_PROPERTY_CHAT_NAME = "chatName";
    public static final String FIREBASE_PROPERTY_PARTICIPANTS = "participants";
    public static final String FIREBASE_PROPERTY_ADMINS = "admins";
    public static final String FIREBASE_PROPERTY_TICTACTOE = "ticTacToe";
    public static final String FIREBASE_PROPERTY_VISIBLE_TO = "visibleTo";


    public static final String FIREBASE_PROPERTY_NO_OF_MESSAGES_AVAILABLE = "noOfMessagesAvailable";
    public static final String FIREBASE_PROPERTY_NO_OF_MESSAGES_I_HAVE_SEEN = "noOfMessagesIHaveSeen";



    //Firebase Root Elements
    public static final String FIREBASE_LOCATION_ALL_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_USER_PENDING_REQUESTS = "pendingRequests";
    public static final String FIREBASE_LOCATION_USER_SENT_REQUESTS = "sentRequests";
    public static final String FIREBASE_LOCATION_ALL_CHATS = "allChats";
    public static final String FIREBASE_LOCATION_MY_CHATS = "myChats";
    public static final String FIREBASE_LOCATION_GROUP_CHATS = "groupChats";
    public static final String FIREBASE_LOCATION_GROUP_DETAILS = "groupDetails";
    public static final String FIREBASE_LOCATION_CHAT_DETAILS = "chatDetails";
    public static final String FIREBASE_LOCATION_ALL_GAMES = "allGames";
    public static final String FIREBASE_LOCATION_ALL_NOTIFICATIONS = "allNotifications";
    public static final String FIREBASE_LOCATION_ALL_REACTABLE_IMAGES = "allReactableImages";
    public static final String FIREBASE_LOCATION_ALL_CUSTOM_EXCEPTION = "allCustomExceptions";







    //URLS
    public static final String FIREBASE_URL = "https://mychat-309a2.firebaseio.com/";
    public static final String OPENTDB_BASE_URL = "https://opentdb.com/api.php?";
    public static final String FIREBASE_URL_MY_CHAT_ALL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_USERS;
    public static final String FIREBASE_URL_MY_CHAT_USER_FRIENDS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_FRIENDS;
    public static final String FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_PENDING_REQUESTS;
    public static final String FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_SENT_REQUESTS;
    public static final String FIREBASE_URL_MY_CHAT_ALL_CHATS = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_CHATS;
    public static final String FIREBASE_URL_MY_CHAT_MY_CHATS = FIREBASE_URL + "/" + FIREBASE_LOCATION_MY_CHATS;
   // public static final String FIREBASE_URL_MY_CHAT_GROUP_CHATS = FIREBASE_URL + "/" + FIREBASE_LOCATION_GROUP_CHATS;
    public static final String FIREBASE_URL_MY_CHAT_GROUP_DETAILS = FIREBASE_URL + "/" + FIREBASE_LOCATION_GROUP_DETAILS;
    public static final String FIREBASE_URL_MY_CHAT_CHAT_DETAILS = FIREBASE_URL + "/" + FIREBASE_LOCATION_CHAT_DETAILS;
    public static final String FIREBASE_URL_MY_CHAT_ALL_GAMES = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_GAMES;
    public static final String FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_NOTIFICATIONS;
    public static final String FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES= FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_REACTABLE_IMAGES;
    public static final String FIREBASE_URL_MY_CHAT_ALL_CUSTOM_EXCEPTION = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_CUSTOM_EXCEPTION;


    //String values
    public static final String CHAT_CURRENT_STATUS_TYPING = "Typing...";
    public static final String USER_REACTIONS_LIKE = "Like";
    public static final String USER_REACTIONS_DISLIKE = "DisLike";
    public static final String USER_REACTIONS_LAUGH = "Laugh";
    public static final String USER_REACTIONS_LOVE = "Love";


}
