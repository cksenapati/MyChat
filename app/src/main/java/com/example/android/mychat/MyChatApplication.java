package com.example.android.mychat;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.CustomException;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.ui.login.LoginActivity;
import com.example.android.mychat.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by chandan on 13-04-2017.
 */
public class MyChatApplication extends Application {

    private String mEncodedEmail;
    private User mCurrentUser;
    private Chat mCurrentChat;
    private Intent intentSelectedMediaFile;

    public String getEncodedEmail() {
        return mEncodedEmail;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public Chat getCurrentChat() {
        return mCurrentChat;
    }

    public Intent getIntentSelectedMediaFile() {
        return intentSelectedMediaFile;
    }

    public void setEncodedEmail(String encodedEmail) {
        this.mEncodedEmail = encodedEmail;
    }

    public void setCurrentUser(User currentUser) {
        this.mCurrentUser = currentUser;
    }

    public void setCurrentChat(Chat mCurrentChat) {
        this.mCurrentChat = mCurrentChat;
    }

    public void setIntentSelectedMediaFile(Intent intentSelectedMediaFile) {
        this.intentSelectedMediaFile = intentSelectedMediaFile;
    }

    @Override
    public void onCreate ()
    {
        super.onCreate();

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });

        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }



    public void handleUncaughtException (Thread thread, Throwable e)
    {

        System.exit(1);


    }


}
