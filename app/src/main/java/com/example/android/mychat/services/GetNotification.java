package com.example.android.mychat.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.request.target.ViewTarget;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.MyChatNotification;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import com.example.android.mychat.utils.Constants;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.id.message;

/**
 * Created by chandan on 03-05-2017.
 */

public class GetNotification extends Service {

    Context context;
    String mEncodedEmail;
    private Firebase mMyNotificationReference;

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notificationId;
    private RemoteViews remoteViews;
    private Notification notification;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mEncodedEmail = intent.getStringExtra("encodedEmail");
        showNotification();
       return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void showNotification()
    {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.mychatlogo)
                .setContent(remoteViews);

        notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;




        mMyNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS).child(mEncodedEmail);
        mMyNotificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    MyChatNotification MyNotification = dataSnapshot.getValue(MyChatNotification.class);
                    if(MyNotification != null)
                    {
                        if(MyNotification.getNotificationFromNoOfChats() == 1)
                           remoteViews.setTextViewText(R.id.text_view_notification_title, MyNotification.getNotificationFromChatName());
                        else
                            remoteViews.setTextViewText(R.id.text_view_notification_title, getString(R.string.app_name));

                        remoteViews.setTextViewText(R.id.text_view_notification_body, MyNotification.getNotificationText());


                        notificationManager.notify(1, notification);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



}
