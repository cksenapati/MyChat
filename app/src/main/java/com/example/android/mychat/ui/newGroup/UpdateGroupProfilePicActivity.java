package com.example.android.mychat.ui.newGroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.Message;
import com.example.android.mychat.models.MyChatNotification;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateGroupProfilePicActivity extends AppCompatActivity {

    private ProgressDialog mAuthProgressDialog;

    ImageView mImageViewShowImage;
    private static final int RC_PHOTO_PICKER =  1;
    String mImageURL;
    String mEncodedEmail;
    String mGroupEmail;

    User mCurrentUser;
    Chat mCurrentGroupChat;
    public ChatDetails mChatDetails;
    public int mNoOfMessagesAvailable;


    ArrayList<User> mArrayListGroupParticipants;



    public Firebase mCurrentGroupChatRef;
    public Firebase mCurrentGroupProfilePicURLRef;
    Firebase mAllChatsRef;
    Firebase mReceiverChatMessagesRef;
    Firebase mAllNotificationReference;



    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);

        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail = intent.getStringExtra("encodedEmail");
            mGroupEmail = intent.getStringExtra("groupEmail");
        }

        this.mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();

        initializeScreen();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_loading);

        mCurrentGroupChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mCurrentGroupChat = dataSnapshot.getValue(Chat.class);
                    if (mCurrentGroupChat != null) {
                        mImageURL = mCurrentGroupChat.getChatPhotoURL();
                        Glide.with(mImageViewShowImage.getContext())
                                .load(mImageURL)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(mImageViewShowImage);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_viewimage, menu);


        MenuItem editProfilePic = menu.findItem(R.id.action_edit_list_name);


        editProfilePic.setVisible(true);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_edit_list_name :

                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PHOTO_PICKER);

                return true;


            default: return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER)
        {
            if(resultCode == RESULT_OK){

                Uri selectedImageUri = data.getData();


                StorageReference selectedPhotoRef = mFirebaseStorageReference.child(selectedImageUri.getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(selectedImageUri);
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        updateProfilePic(downloadUrl.toString());

                    }
                });
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("Group ProfilePic");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mImageViewShowImage = (ImageView) findViewById(R.id.image_view_show_full_image);


        mCurrentGroupChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(mGroupEmail);
        mCurrentGroupProfilePicURLRef = mCurrentGroupChatRef.child("chatPhotoURL");
        mAllChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS);
        mAllNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("profilePics");

        mArrayListGroupParticipants = new ArrayList<User>();


    }

    public void updateProfilePic(String downloadableProfilePicURL)
    {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_loading);
        progressBar.setVisibility(View.VISIBLE);

        //mCurrentGroupProfilePicURLRef.setValue(downloadableProfilePicURL);
        updateGroupProfilePicAtOtherParticipants(downloadableProfilePicURL);


        Glide.with(mImageViewShowImage.getContext())
                .load(downloadableProfilePicURL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mImageViewShowImage);


    }

    public void updateGroupProfilePicAtOtherParticipants(final String downloadableProfilePicURL)
    {
        Firebase groupParticipants = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mGroupEmail).child("participants");
        final Firebase myChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);

        groupParticipants.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    return;
                }

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User participant = snapshot.getValue(User.class);
                    myChatsRef.child(Utils.encodeEmail(participant.getEmail())).child(mGroupEmail).child("chatPhotoURL").setValue(downloadableProfilePicURL);
                }
                getGroupParticipants(mCurrentGroupChat);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }






    public void getGroupParticipants(final Chat receiverChat)
    {
        Firebase groupParticipantsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(receiverChat.getChatEmail()).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS);

        if(receiverChat.getChatType().equals("Group")) {
            groupParticipantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mArrayListGroupParticipants.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User participant = snapshot.getValue(User.class);
                            if (participant != null)
                                mArrayListGroupParticipants.add(participant);
                        }
                        getChatDetails(receiverChat);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public void getChatDetails(final Chat receiverChat)
    {
        Firebase mChatDetailsRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(receiverChat.chatId);
        mChatDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatDetails = dataSnapshot.getValue(ChatDetails.class);
                if (mChatDetails != null) {
                    mNoOfMessagesAvailable = mChatDetails.getTotalNoOfMessagesAvailable();
                    sendMessage(receiverChat);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void sendMessage(Chat receiverChat)
    {
        addSystemGeneratedMessage(receiverChat);

        String senderFirstName = "SystemGeneratedMessage";
        String senderEmail = "SystemGeneratedMessage";
        HashMap<String, Object> timestampMessageSentAt = new HashMap<String, Object>();
        timestampMessageSentAt.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


        String statusDelivered = getResources().getString(R.string.message_status_delivered);
        String visibleTo = "All";
        String messageText = mCurrentUser.getName() + " changed the group profilePic.";

        //Send notification
        statusDelivered = mCurrentUser.getEmail();
        for(int i=0;i<mArrayListGroupParticipants.size();i++)
        {
            //send notification to all the participants except to myself
            if(!mArrayListGroupParticipants.get(i).getEmail().equals(statusDelivered))
                updateNotification(receiverChat,mArrayListGroupParticipants.get(i).getEmail(),messageText,receiverChat.chatName);
        }


        //Update the timestampLastUpdate in chatDetails
        Firebase mChatDetailsRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(receiverChat.chatId).child(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE);
        mChatDetailsRef.setValue(timestampMessageSentAt);


        mReceiverChatMessagesRef = mAllChatsRef.child(receiverChat.chatId);
        String messageId = mReceiverChatMessagesRef.push().getKey();
        Message systemGeneratedMessage = new Message(messageText, messageId, statusDelivered, senderFirstName, senderEmail, visibleTo, timestampMessageSentAt, null, null);
        mReceiverChatMessagesRef.child(messageId).setValue(systemGeneratedMessage);
    }

    public void addSystemGeneratedMessage(Chat receiverChat)
    {
       /* HashMap<String, Object> timestampCurrentTime = new HashMap<String, Object>();
        timestampCurrentTime.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
*/
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String lastUpdateDay = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(mChatDetails.getTimestampLastUpdateLong()));

        String today = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(timestamp.getTime()));

        if((lastUpdateDay.equals(today) && mChatDetails.getTotalNoOfMessagesAvailable()==0) ||
                !lastUpdateDay.equals(today)) {

            String senderFirstName = "SystemGeneratedMessage";
            String senderEmail = "SystemGeneratedMessage";
            HashMap<String, Object> timestampMessageSentAt = new HashMap<String, Object>();
            timestampMessageSentAt.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


            String statusDelivered = getResources().getString(R.string.message_status_delivered);
            String visibleTo = "All";


            mReceiverChatMessagesRef = mAllChatsRef.child(receiverChat.chatId);
            String messageId = mReceiverChatMessagesRef.push().getKey();

            Message systemGeneratedMessage = new Message(today, messageId, statusDelivered, senderFirstName, senderEmail, visibleTo, timestampMessageSentAt, null, null);
            mReceiverChatMessagesRef.child(messageId).setValue(systemGeneratedMessage);
        }
    }

    public void updateNotification(final Chat receiverChat, String friendEmail,final String notificationText,final String chatName)
    {
        final Firebase mNotificationTo = mAllNotificationReference.child(Utils.encodeEmail(friendEmail));
        mNotificationTo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    MyChatNotification friendNotification = dataSnapshot.getValue(MyChatNotification.class);
                    if(friendNotification != null)
                    {
                        if(friendNotification.getNotificationFromChatId().equals(receiverChat.chatId))
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages.");
                        }
                        else if(friendNotification.getNotificationFromChatId().contains(receiverChat.chatId))
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages from "+friendNotification.getNotificationFromNoOfChats()+" chats.");
                        }
                        else
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationFromChatName("More than 1");
                            friendNotification.setNotificationFromChatId(friendNotification.getNotificationFromChatId()+","+receiverChat.chatId);
                            friendNotification.setNotificationFromNoOfChats(friendNotification.getNotificationFromNoOfChats() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages from "+friendNotification.getNotificationFromNoOfChats()+" chats.");

                        }
                        mNotificationTo.setValue(friendNotification);
                    }
                }
                else
                {
                    MyChatNotification friendNotification = new MyChatNotification(notificationText,receiverChat.chatId,chatName,1,1);
                    mNotificationTo.setValue(friendNotification);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}

