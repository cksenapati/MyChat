package com.example.android.mychat.utils;

import android.util.Log;
import android.util.TypedValue;

import com.example.android.mychat.models.Chat;
import com.example.android.mychat.ui.MainActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by chandan on 03-03-2017.
 */
public class Utils {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static final SimpleDateFormat SIMPLE_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @JsonIgnore
    public static long getTimestampInLong(HashMap<String, Object> timestampLastUpdate) {

        return (long) timestampLastUpdate.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String getFirstName(String fullName)
    {
        String arr[] = fullName.split(" ", 2);
        return arr[0];
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }


    public static void makeMeOnOrOffline(final String mEncodedEmail, final boolean isOnline)
    {
        final Firebase myChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
        Firebase myChats = myChatsRef.child(mEncodedEmail);
        myChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    return;
                }

                for (Map.Entry<String, Object> entry : ((Map<String,Object>) dataSnapshot.getValue()).entrySet()){

                    //Get single friend
                    Map singleChat = (Map) entry.getValue();

                    //Get email field
                    String chatEmail = singleChat.get("chatEmail").toString();
                    String chatType = singleChat.get("chatType").toString();
                    HashMap<String, Object> timestampLastSeen = new HashMap<String, Object>();
                    timestampLastSeen.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    if(chatType.equals("Personal"))
                    {
                        Firebase meInChatListOfMyFriendChat = myChatsRef.child(Utils.encodeEmail(chatEmail)).child(mEncodedEmail);
                        meInChatListOfMyFriendChat.child("online").setValue(isOnline);
                        meInChatListOfMyFriendChat.child("timestampLastSeen").setValue(timestampLastSeen);
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }



}
