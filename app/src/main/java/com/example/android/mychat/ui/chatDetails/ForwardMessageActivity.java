package com.example.android.mychat.ui.chatDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.Message;
import com.example.android.mychat.models.MyChatNotification;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.ui.newGroup.AddSubjectActivity;
import com.example.android.mychat.ui.newGroup.AllFriendsAdapter;
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

import static android.R.id.message;

public class ForwardMessageActivity extends AppCompatActivity {

    public static Activity forwardMessageActivity;

    TextView mTextViewActivityTitle;
    TextView mTextViewActivityPurpose;
    ListView mListViewMyChats;
    EditText mEditTextSearchChat;
    //ImageView mImageViewAddFriend;
    FloatingActionButton mFloatingActionButtonSendMessage;

    public int mNoOfMessagesAvailable;
    private String mEncodedEmail;
    private Message mMessageToBeForwarded;
    private User mCurrentUser;
    public ChatDetails mChatDetails;

    private AllChatsToForwardMessageAdapter mAllChatsAdapter;
    ArrayList<Chat> mArrayListSelectedChats;
    ArrayList<User> mArrayListGroupParticipants;
    ArrayList<Integer> mArrayListSelectedPositions;

    private Firebase mMyChatsReference;
    Firebase mAllChatsRef;
    Firebase mReceiverChatMessagesRef;
    Firebase mAllNotificationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);


        forwardMessageActivity = this;

        Intent intent = getIntent();
        if(intent != null)
        {
            mMessageToBeForwarded = (Message) intent.getSerializableExtra("messageToBeForwarded");
        }

        this.mEncodedEmail = ((MyChatApplication) this.getApplication()).getEncodedEmail();
        this.mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();

        if(mMessageToBeForwarded.getMediaFileType() != null)
            mMessageToBeForwarded.setMessageText(null);

        initializeScreen();



        mMyChatsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mAllChatsAdapter = new AllChatsToForwardMessageAdapter(ForwardMessageActivity.this, Chat.class,
                            R.layout.item_single_friend, mMyChatsReference.orderByChild(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE+"/"+Constants.FIREBASE_PROPERTY_TIMESTAMP));

                    mListViewMyChats.setAdapter(mAllChatsAdapter);


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mEditTextSearchChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textEntered = mEditTextSearchChat.getText().toString().toUpperCase();

                if (mAllChatsAdapter != null)
                    mAllChatsAdapter.cleanup();

                if (textEntered.equals("") || textEntered.length() < 1) {
                    mAllChatsAdapter = new AllChatsToForwardMessageAdapter(ForwardMessageActivity.this, Chat.class,
                            R.layout.item_single_friend, mMyChatsReference.orderByChild(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE+"/"+Constants.FIREBASE_PROPERTY_TIMESTAMP));
                    mListViewMyChats.setAdapter(mAllChatsAdapter);

                } else {
                    mAllChatsAdapter = new AllChatsToForwardMessageAdapter(ForwardMessageActivity.this, Chat.class,
                            R.layout.item_single_friend, mMyChatsReference.orderByChild(Constants.FIREBASE_PROPERTY_CHAT_NAME).startAt(textEntered).endAt(textEntered + "~"));
                    mListViewMyChats.setAdapter(mAllChatsAdapter);

                }

            }
        });

        mListViewMyChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = mAllChatsAdapter.getItem(position);

                if (chat != null) {
                    ImageView imageViewAddFriend = (ImageView) view.findViewById(R.id.image_view_add_friend);
                    if (imageViewAddFriend.getVisibility() == View.VISIBLE) {
                        imageViewAddFriend.setVisibility(View.GONE);

                        for (int i = 0; i < mArrayListSelectedChats.size(); i++) {
                            Chat eachChat = mArrayListSelectedChats.get(i);
                            if (eachChat.getChatEmail().equals(chat.getChatEmail())) {
                                mArrayListSelectedChats.remove(i);
                                break;
                            }
                        }

                        //mArrayListGroupParticipants.remove(friend);
                    } else if (imageViewAddFriend.getVisibility() == View.GONE) {
                        imageViewAddFriend.setVisibility(View.VISIBLE);
                        mArrayListSelectedChats.add(chat);
                    }

                }
            }
        });



        mFloatingActionButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrayListSelectedChats.size() < 1) {
                    Toast.makeText(ForwardMessageActivity.this, "Select a chat.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    sendMessageToEachChat();
                }

            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_addparticipants, menu);

        MenuItem search = menu.findItem(R.id.action_search);


        search.setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_search :
                if(mEditTextSearchChat.getVisibility() == View.GONE) {
                    mEditTextSearchChat.setVisibility(View.VISIBLE);

                }
                else {
                    mEditTextSearchChat.setVisibility(View.GONE);
                    mAllChatsAdapter = new AllChatsToForwardMessageAdapter(ForwardMessageActivity.this, Chat.class,
                            R.layout.item_single_friend, mMyChatsReference.orderByChild(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE+"/"+Constants.FIREBASE_PROPERTY_TIMESTAMP));

                    mListViewMyChats.setAdapter(mAllChatsAdapter);
                }

                return true;


            default: return super.onOptionsItemSelected(item);
        }

    }

    private void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextViewActivityTitle = (TextView) findViewById(R.id.text_view_activity_title);
        mTextViewActivityTitle.setText("Forward Message");

        mTextViewActivityPurpose = (TextView) findViewById(R.id.text_view_activity_purpose);
        mTextViewActivityPurpose.setText("Select chat(s)");

        mListViewMyChats = (ListView) findViewById(R.id.list_view_my_friends);
        //mImageViewAddFriend = (ImageView) findViewById(R.id.image_view_add_friend);
        mFloatingActionButtonSendMessage = (FloatingActionButton) findViewById(R.id.fab_next);
        mMyChatsReference =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail);
        mEditTextSearchChat = (EditText) findViewById(R.id.edit_text_search_friends);

        mAllChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS);
        mAllNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS);

        mArrayListSelectedChats = new ArrayList<Chat>();
        mArrayListGroupParticipants = new ArrayList<User>();
        mArrayListSelectedPositions = new ArrayList<Integer>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.makeMeOnOrOffline(mEncodedEmail, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.makeMeOnOrOffline(mEncodedEmail, false);

    }

    public void sendMessageToEachChat()
    {
        for(int i=0;i<mArrayListSelectedChats.size();i++)
        {
            Chat chat = mArrayListSelectedChats.get(i);
            if(chat.getChatType().equals("Group"))
                getGroupParticipants(chat);
            else
                getChatDetails(chat);


        }

        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);

    }

    public void sendMessage(Chat receiverChat,String messageText)
    {
        addSystemGeneratedMessage(receiverChat);

        // Create a pojo
        String senderFirstName = Utils.getFirstName(mCurrentUser.getName());
        String senderEmail = Utils.decodeEmail(mEncodedEmail);
        HashMap<String, Object> timestampMessageSentAt = new HashMap<String, Object>();
        timestampMessageSentAt.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


        //Adding POJO in Firebase
        String lastMessage;
        if(messageText != null)
            lastMessage = messageText;
        else
            lastMessage = "media file";


        String statusDelivered="";
        String visibleTo = "";
        if(receiverChat.getChatType().equals("Personal"))
        {
            statusDelivered = getResources().getString(R.string.message_status_delivered);
            visibleTo = mCurrentUser.getEmail() + "," + receiverChat.getChatEmail();
            updateNotification(receiverChat,receiverChat.chatEmail,lastMessage,mCurrentUser.getName());
        }
        else if(receiverChat.getChatType().toString().equals("Group"))
        {
            statusDelivered = mCurrentUser.getEmail();
            for(int i=0;i<mArrayListGroupParticipants.size();i++)
            {
                visibleTo = visibleTo + mArrayListGroupParticipants.get(i).getEmail()+",";

                //send notification to all the participants except to myself
                if(!mArrayListGroupParticipants.get(i).getEmail().equals(statusDelivered))
                    updateNotification(receiverChat,mArrayListGroupParticipants.get(i).getEmail(),lastMessage,receiverChat.chatName);
            }

        }
        mReceiverChatMessagesRef = mAllChatsRef.child(receiverChat.chatId);

        String messageId = mReceiverChatMessagesRef.push().getKey();


        mMessageToBeForwarded.setMessageId(messageId);
        mMessageToBeForwarded.setStatus(statusDelivered);
        mMessageToBeForwarded.setSentBy(senderFirstName);
        mMessageToBeForwarded.setSenderEmail(senderEmail);
        mMessageToBeForwarded.setVisibleTo(visibleTo);
        mMessageToBeForwarded.setTimestampMessageSentAt(timestampMessageSentAt);

        //Message message = new Message(messageText, messageId, statusDelivered, senderFirstName, senderEmail,visibleTo, timestampMessageSentAt, mediaFileURL,mediaFileType);

        HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
        timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);






        Firebase mChatDetailsRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(receiverChat.chatId);
        Firebase mCurrentChatRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(receiverChat.chatEmail);

        ChatDetails chatDetails = new ChatDetails(lastMessage,mNoOfMessagesAvailable + 1,timestampLastUpdate);
        mChatDetailsRef.setValue(chatDetails);


        mReceiverChatMessagesRef.child(messageId).setValue(mMessageToBeForwarded);
        mCurrentChatRef.child(Constants.FIREBASE_PROPERTY_NO_OF_MESSAGES_I_HAVE_SEEN).setValue(mNoOfMessagesAvailable + 1);
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
                     sendMessage(receiverChat,mMessageToBeForwarded.getMessageText());
                 }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



}
