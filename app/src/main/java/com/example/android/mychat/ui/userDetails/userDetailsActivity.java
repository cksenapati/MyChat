package com.example.android.mychat.ui.userDetails;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.Message;
import com.example.android.mychat.models.MyChatNotification;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.ui.ViewMediaFile.ViewImageActivity;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.ui.myProfile.MyProfileActivity;
import com.example.android.mychat.ui.myProfile.UpdateProfilePicActivity;
import com.example.android.mychat.ui.newGroup.AddParticipantsActivity;
import com.example.android.mychat.ui.newGroup.AllFriendsAdapter;
import com.example.android.mychat.ui.newGroup.UpdateGroupProfilePicActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class userDetailsActivity extends AppCompatActivity {

    private Chat mCurrentChat;
    User mCurrentUser;
    public ChatDetails mChatDetails;
    public int mNoOfMessagesAvailable;


    LinearLayout mLinearLayoutUserEmail;
    LinearLayout mLinearLayoutgroupParticipants;
    public ImageView mImageViewChatProfilePic;
    public TextView mTextViewChatName;
    public TextView mTextViewUserFullName;
    public TextView mTextViewUserEmail;
    public TextView mTextViewExitGroup;
    public TextView mTextViewAddParticipants;

    ImageView mImageViewEditChatName;
    ListView mListViewGroupParticipants;

    Firebase mCurrentChatRef;
    Firebase mGroupParticipantsRef;
    Firebase mGroupDetailsRef;
    Firebase mGroupAdminsRef;
    Firebase mAllChatsRef;
    Firebase mReceiverChatMessagesRef;
    Firebase mAllNotificationReference;


    String mEncodedEmail;
    String mEncodedChatEmail;

    GroupParticipantsAdapter mGroupParticipantsAdapter;
    ArrayList<User> mArrayListGroupParticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail = intent.getStringExtra("encodedEmail");
            mEncodedChatEmail = intent.getStringExtra("encodedChatEmail");
        }

        this.mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        mCurrentChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mCurrentChat = dataSnapshot.getValue(Chat.class);
                    if (mCurrentChat != null) {
                        Glide.with(mImageViewChatProfilePic.getContext())
                                .load(mCurrentChat.getChatPhotoURL())
                                .into(mImageViewChatProfilePic);

                        if (mCurrentChat.getChatType().equals("Group")) {
                            mLinearLayoutUserEmail.setVisibility(View.GONE);
                            mLinearLayoutgroupParticipants.setVisibility(View.VISIBLE);
                            mTextViewExitGroup.setVisibility(View.VISIBLE);
                           // mTextViewAddParticipants.setVisibility(View.VISIBLE);
                        } else {
                            mLinearLayoutUserEmail.setVisibility(View.VISIBLE);
                            mLinearLayoutgroupParticipants.setVisibility(View.GONE);
                        }
                        mTextViewUserEmail.setText(mCurrentChat.getChatEmail());
                        mTextViewChatName.setText(mCurrentChat.getChatName());
                        mTextViewUserFullName.setText(mCurrentChat.getChatName());
                    }
                    getGroupParticipants();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mImageViewChatProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentChat.chatType.equals("Group") && mCurrentChat.getChatPhotoURL() != null) {
                    Intent viewImageIntent = new Intent(userDetailsActivity.this, UpdateGroupProfilePicActivity.class);
                    viewImageIntent.putExtra("encodedEmail", mEncodedEmail);
                    viewImageIntent.putExtra("groupEmail", mCurrentChat.chatEmail);
                    startActivity(viewImageIntent);
                } else if (mCurrentChat.chatType.equals("Personal") && mCurrentChat.getChatPhotoURL() != null) {
                    Intent viewImageIntent = new Intent(userDetailsActivity.this, ViewImageActivity.class);
                    viewImageIntent.putExtra("imageURL", mCurrentChat.getChatPhotoURL());
                    viewImageIntent.putExtra("activityTitle", mCurrentChat.getChatName());
                    startActivity(viewImageIntent);
                }

            }
        });


        mImageViewEditChatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditChatNameDialogFragment();
            }
        });

        mTextViewExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentChatRef.setValue(null);
                mGroupParticipantsRef.child(mEncodedEmail).setValue(null);
                ChatDetailsActivity.chatDetailsactivity.finish();
                finish();
            }
        });

        mTextViewAddParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroupIntent = new Intent(userDetailsActivity.this , AddParticipantsActivity.class);
                newGroupIntent.putExtra("encodedEmail", mEncodedEmail);
                newGroupIntent.putExtra("chatEmail", mCurrentChat.chatEmail);
                startActivity(newGroupIntent);
            }
        });


        mGroupAdminsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String allAdmin = dataSnapshot.getValue(String.class);
                    if(allAdmin != null)
                    {
                        if(allAdmin.contains(Utils.decodeEmail(mEncodedEmail)))
                            mTextViewAddParticipants.setVisibility(View.VISIBLE);
                        else
                            mTextViewAddParticipants.setVisibility(View.GONE);
                    }
                    else
                        mTextViewAddParticipants.setVisibility(View.GONE);
                }
                else
                    mTextViewAddParticipants.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mCurrentChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(mEncodedChatEmail);
        mGroupDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mEncodedChatEmail);
        mGroupAdminsRef = mGroupDetailsRef.child(Constants.FIREBASE_PROPERTY_ADMINS);
        mGroupParticipantsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mEncodedChatEmail).child("participants");
        mAllChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS);
        mAllNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS);


        mLinearLayoutUserEmail = (LinearLayout)  findViewById(R.id.linear_layout_user_email);
        mLinearLayoutgroupParticipants = (LinearLayout)  findViewById(R.id.linear_layout_group_participants);

        mImageViewChatProfilePic = (ImageView) findViewById(R.id.image_view_chat_profile_pic);
        mTextViewChatName = (TextView) findViewById(R.id.text_view_chat_name);
        mTextViewUserFullName = (TextView) findViewById(R.id.text_view_user_full_name);
        mTextViewUserEmail = (TextView) findViewById(R.id.text_view_user_email);
        mImageViewEditChatName = (ImageView) findViewById(R.id.image_view_edit_chat_name);
        mListViewGroupParticipants = (ListView) findViewById(R.id.list_view_group_participants);
        mTextViewExitGroup = (TextView) findViewById(R.id.text_view_exit_group);
        mTextViewAddParticipants = (TextView) findViewById(R.id.text_view_add_participants);

        mArrayListGroupParticipants = new ArrayList<User>();

    }


    public void showEditChatNameDialogFragment() {

        final Dialog editChatNameDialog = new Dialog(userDetailsActivity.this);
        editChatNameDialog.setContentView(R.layout.dialog_edit_name);
        editChatNameDialog.show();




        final EditText editTextChatName = (EditText) editChatNameDialog.findViewById(R.id.edit_text_name);
        TextView textViewDone = (TextView) editChatNameDialog.findViewById(R.id.text_view_done);
        TextView textViewDiscard = (TextView) editChatNameDialog.findViewById(R.id.text_view_discard);

        /**
         * Call doListEdit() when user taps "Done" keyboard action
         */
        editTextChatName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                    String enteredChatName = editTextChatName.getText().toString();

                    if (enteredChatName.trim().length() >0 && enteredChatName !=null && !enteredChatName.equals(mTextViewUserFullName.getText().toString()) )
                    {
                        editChatName(enteredChatName);
                    }
                    editChatNameDialog.dismiss();
                }
                return true;
            }
        });

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredChatName = editTextChatName.getText().toString().toUpperCase();

                if (enteredChatName.trim().length() > 0 && enteredChatName != null && !enteredChatName.equals(mTextViewUserFullName.getText().toString())) {
                    editChatName(enteredChatName);
                }
                editChatNameDialog.dismiss();
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChatNameDialog.dismiss();
            }
        });
    }

    public void editChatName(final String enteredChatName)
    {
        if(mCurrentChat.getChatType().equals("Personal"))
        {
            mCurrentChatRef.child("chatName").setValue(enteredChatName);
        }
        else if(mCurrentChat.getChatType().equals("Group"))
        {
            Firebase groupParticipants = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mCurrentChat.chatEmail).child("participants");
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
                        myChatsRef.child(Utils.encodeEmail(participant.getEmail())).child(mCurrentChat.chatEmail).child("chatName").setValue(enteredChatName);
                    }
                    getChatDetails(mCurrentChat);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public void getGroupParticipants()
    {


        if(mCurrentChat != null && mCurrentChat.getChatType().equals("Group"))
        {
            mGroupParticipantsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        mArrayListGroupParticipants.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User participant = snapshot.getValue(User.class);
                            if (participant != null)
                                mArrayListGroupParticipants.add(participant);
                        }


                        mGroupParticipantsAdapter = new GroupParticipantsAdapter(userDetailsActivity.this, User.class,
                                R.layout.item_single_friend, mGroupParticipantsRef.orderByChild(Constants.FIREBASE_PROPERTY_NAME),mCurrentChat.chatEmail,Utils.decodeEmail(mEncodedEmail));

                        mListViewGroupParticipants.setAdapter(mGroupParticipantsAdapter);

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
        String messageText = mCurrentUser.getName() + " changed the group name.";

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
