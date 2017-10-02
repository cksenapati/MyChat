package com.example.android.mychat.ui.chatDetails;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.example.android.mychat.ui.addFriends.AddFriendsAdapter;
import com.example.android.mychat.ui.games.TicTacToeActivity;
import com.example.android.mychat.ui.myProfile.MyProfileActivity;
import com.example.android.mychat.ui.newGroup.AddParticipantsActivity;
import com.example.android.mychat.ui.userDetails.userDetailsActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.R.id.message;
import static android.R.id.primary;

public class ChatDetailsActivity extends AppCompatActivity {

    public static Activity chatDetailsactivity;

    private static final int RC_PHOTO_PICKER =  1;
    private static final int RC_VIDEO_PICKER =  2;
    private static final int RC_AUDIO_PICKER =  3;
    private static final int RC_TEXT_PICKER_FOR_MEDIAFILE = 4;

    public String mEncodedEmail;
    public User mCurrentUser;
    public Chat mCurrentChat;
    public ChatDetails mCurrentChatDetails;
    public Message mMessageTOBeOperated;

    public Button mButtonSendMessage;
    public EditText mEditTextWriteMessage;
    public ListView mListViewMessage;
    public ImageView mFriendProfilePic;
    public TextView mTextViewFriendFirstName;
    public TextView mTextViewFriendLastSeen;
    private ImageButton mPhotoPickerButton;
    public LinearLayout mLinearLayoutChatNameAndLastSeen;
    public LinearLayout mLinearLayoutOperationOnMessage;
    public ImageView mImageViewGoBack;
    public ImageView mImageViewShowAbout;
    public ImageView mImageViewDeleteMessage;
    public ImageView mImageViewForwardMessage;
    public Toolbar mToolBar;
    public View mViewSelectedItem;
     ProgressBar progressBar;


    public Firebase mCurrentChatRef;
    public Firebase mCurrentChatMessagesRef;
    public Firebase mCurrentMyChatRef;
    Firebase groupParticipantsRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;
    Firebase mGroupDetailsRef;
    Firebase mGroupParticipantsRef;
    Firebase mCurrentChatDetailsRef;
    Firebase mChatDetailsRef;
    Firebase mAllNotificationReference;

    int mNoOfMessagesAvailable;
    ArrayList<User> mArrayListGroupParticipants;
    public ChatDetailsAdapter mChatDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);


        chatDetailsactivity = this;



        mEncodedEmail = ((MyChatApplication) this.getApplication()).getEncodedEmail();
        mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();
        mCurrentChat = ((MyChatApplication) this.getApplication()).getCurrentChat();


        initializeScreen();
        progressBar.setVisibility(View.VISIBLE);



        mButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageTest = mEditTextWriteMessage.getText().toString();

                // Clear input box
                mEditTextWriteMessage.setText("");

                sendMessage(messageTest,null,null);


            }
        });


        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseMediaFile();

            }
        });



        mCurrentChatMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mChatDetailsAdapter = new ChatDetailsAdapter(ChatDetailsActivity.this, Message.class,
                            R.layout.item_single_message, mCurrentChatMessagesRef.orderByChild(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP),
                            mEncodedEmail, mCurrentUser, mCurrentChat);

                    mListViewMessage.setAdapter(mChatDetailsAdapter);
                    progressBar.setVisibility(View.GONE);

                } else {
                    if (mChatDetailsAdapter != null)
                        mChatDetailsAdapter.cleanup();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mListViewMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Message message = mChatDetailsAdapter.getItem(position);
                if (message != null && !message.getSenderEmail().equals("SystemGeneratedMessage")) {

                    //Used to change the background colour of the selected item view later
                    mViewSelectedItem = view;

                    view.setBackgroundColor(Color.parseColor("#BEDDA2"));
                    mLinearLayoutOperationOnMessage.setVisibility(View.VISIBLE);
                    mListViewMessage.setEnabled(false);
                    mMessageTOBeOperated = message;
                }

                return false;
            }
        });


        //Operation of message
        mImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mViewSelectedItem != null)
                  mViewSelectedItem.setBackgroundColor(Color.parseColor("#EDEEEF"));
                mViewSelectedItem = null;

                mLinearLayoutOperationOnMessage.setVisibility(View.INVISIBLE);
                 mListViewMessage.setEnabled(true);
                mMessageTOBeOperated = null;
            }
        });

        mImageViewForwardMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mViewSelectedItem != null)
                    mViewSelectedItem.setBackgroundColor(Color.parseColor("#EDEEEF"));
                mViewSelectedItem = null;

                Intent forwardMessageIntent = new Intent(ChatDetailsActivity.this,ForwardMessageActivity.class);
                forwardMessageIntent.putExtra("messageToBeForwarded",mMessageTOBeOperated);
                startActivity(forwardMessageIntent);

                mLinearLayoutOperationOnMessage.setVisibility(View.INVISIBLE);
                mListViewMessage.setEnabled(true);
                mMessageTOBeOperated = null;

            }
        });

        mImageViewShowAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mViewSelectedItem != null)
                    mViewSelectedItem.setBackgroundColor(Color.parseColor("#EDEEEF"));
                mViewSelectedItem = null;



                Intent aboutMessageIntent = new Intent(ChatDetailsActivity.this,MessageDetailsActivity.class);
                aboutMessageIntent.putExtra("message",mMessageTOBeOperated);
                startActivity(aboutMessageIntent);

                mLinearLayoutOperationOnMessage.setVisibility(View.INVISIBLE);
                mListViewMessage.setEnabled(true);
                mMessageTOBeOperated = null;

            }
        });

        mImageViewDeleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mViewSelectedItem != null)
                    mViewSelectedItem.setBackgroundColor(Color.parseColor("#EDEEEF"));
                mViewSelectedItem = null;

                try
                {
                    Firebase messageShouldBeVisibleToRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS).child(mCurrentChat.chatId).child(mMessageTOBeOperated.getMessageId()).child(Constants.FIREBASE_PROPERTY_VISIBLE_TO);

                    String visibleTo = mMessageTOBeOperated.getVisibleTo();
                    if (visibleTo.contains(mCurrentUser.getEmail())) {
                        visibleTo = visibleTo.replace(mCurrentUser.getEmail(), "");
                        messageShouldBeVisibleToRef.setValue(visibleTo);
                    }
                }
                catch (Exception ex)
                {

                }

                mLinearLayoutOperationOnMessage.setVisibility(View.INVISIBLE);
                mListViewMessage.setEnabled(true);
                mMessageTOBeOperated = null;

            }
        });



        mCurrentMyChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mCurrentChat = dataSnapshot.getValue(Chat.class);
                    if (mCurrentChat != null) {
                        setOnlineOrOffLine();

                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        mLinearLayoutChatNameAndLastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailsActivity.this, userDetailsActivity.class);

                if (mCurrentChat.getChatType().equals("Group"))
                    intent.putExtra("encodedChatEmail", mCurrentChat.chatEmail);
                else
                    intent.putExtra("encodedChatEmail", Utils.encodeEmail(mCurrentChat.chatEmail));

                intent.putExtra("encodedEmail", mEncodedEmail);
                startActivity(intent);
            }
        });



        if(mCurrentChat.getChatType().equals("Group")) {
            groupParticipantsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mArrayListGroupParticipants.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User participant = snapshot.getValue(User.class);
                            if (participant != null)
                                mArrayListGroupParticipants.add(participant);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


        mCurrentChatDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    ChatDetails chatDetails = dataSnapshot.getValue(ChatDetails.class);
                    if (chatDetails != null)
                    {
                        mCurrentChatDetails = chatDetails;
                        mNoOfMessagesAvailable = mCurrentChatDetails.getTotalNoOfMessagesAvailable();

                        if(mCurrentChatDetails.getWhoAreTyping() != null)
                        {
                            String whoAreTypingWithoutMe = mCurrentChatDetails.getWhoAreTyping().replace(","+Utils.getFirstName(mCurrentUser.getName()),"");
                            String whoAreTypingWithoutMeAndFirstComma = whoAreTypingWithoutMe.replaceFirst(",","");

                            if(whoAreTypingWithoutMeAndFirstComma.length() > 3)
                            {
                                if(mCurrentChat.getChatType().equals("Group"))
                                    mTextViewFriendLastSeen.setText(whoAreTypingWithoutMeAndFirstComma + "  typing...");
                                else if(mCurrentChat.getChatType().equals("Personal"))
                                    mTextViewFriendLastSeen.setText("Typing...");

                                mTextViewFriendLastSeen.setTextColor(Color.parseColor("#ffffff"));

                            }
                            else
                            {
                                mTextViewFriendLastSeen.setTextColor(Color.parseColor("#000000"));
                                setOnlineOrOffLine();
                            }
                        }
                        else
                        {
                            mTextViewFriendLastSeen.setTextColor(Color.parseColor("#000000"));
                            setOnlineOrOffLine();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mEditTextWriteMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String myFirstName = Utils.getFirstName(mCurrentUser.getName());

                if (charSequence.toString().trim().length() > 0) {
                    mButtonSendMessage.setEnabled(true);
                    //mButtonSendMessage.getBackground().setColorFilter(primaryColourCode, PorterDuff.Mode.MULTIPLY);

                    //Update the chatDetails
                    if( mCurrentChatDetails.getWhoAreTyping() != null)
                    {
                        if(!mCurrentChatDetails.getWhoAreTyping().contains(myFirstName))
                        {
                            mCurrentChatDetails.setWhoAreTyping(mCurrentChatDetails.getWhoAreTyping() +","+ myFirstName);
                            mCurrentChatDetailsRef.setValue(mCurrentChatDetails);
                        }
                    }
                    else
                    {
                        mCurrentChatDetails.setWhoAreTyping(","+ myFirstName);
                        mCurrentChatDetailsRef.setValue(mCurrentChatDetails);
                    }


                } else {
                    mButtonSendMessage.setEnabled(false);

                    if( mCurrentChatDetails.getWhoAreTyping() != null && mCurrentChatDetails.getWhoAreTyping().contains(myFirstName))
                    {
                        mCurrentChatDetails.setWhoAreTyping(mCurrentChatDetails.getWhoAreTyping().replace(","+myFirstName,""));
                        mCurrentChatDetailsRef.setValue(mCurrentChatDetails);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //For intent coming from SelectedMediaFileActivity
        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra("mediaFileType") != null)
        {
            String mediaFileType,messageText,mediaFileLocation;

            mediaFileType = intent.getStringExtra("mediaFileType");
            messageText = intent.getStringExtra("messageText");
            mediaFileLocation = intent.getStringExtra("mediaFileLocation");
            //sendMessage(messageText,mediaFileLocation,mediaFileType);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_TEXT_PICKER_FOR_MEDIAFILE)
        {
            if(resultCode == RESULT_OK){
                //progressBar.setVisibility(View.VISIBLE);
                String mediaFileType,messageText,mediaFileLocation;

                mediaFileType = data.getStringExtra("mediaFileType");
                messageText = data.getStringExtra("messageText");
                mediaFileLocation = data.getStringExtra("mediaFileLocation");
                sendMessage(messageText,mediaFileLocation,mediaFileType);

            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

        else if(requestCode == RC_PHOTO_PICKER)
        {
            if(resultCode == RESULT_OK){
                //progressBar.setVisibility(View.VISIBLE);
                uploadAndDownloadMediaFile(data, "imageFile");
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
               // progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

        else if(requestCode == RC_VIDEO_PICKER)
        {
            if(resultCode == RESULT_OK){
                //progressBar.setVisibility(View.VISIBLE);
                uploadAndDownloadMediaFile(data,"videoFile");
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Unable to pick this Video file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

        else if(requestCode == RC_AUDIO_PICKER)
        {
            if(resultCode == RESULT_OK){
                //progressBar.setVisibility(View.VISIBLE);
                uploadAndDownloadMediaFile(data,"audioFile");
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Unable to pick this Audio file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_chatdetails, menu);


        MenuItem actionTicTacToe = menu.findItem(R.id.action_play_tictactoe);

        if(mCurrentChat.getChatType().equals("Personal"))
           actionTicTacToe.setVisible(true);
        else
            actionTicTacToe.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_play_tictactoe :

                ((MyChatApplication) this.getApplication()).setCurrentChat(mCurrentChat);
                Intent myProfileIntent = new Intent(ChatDetailsActivity.this , TicTacToeActivity.class);
                startActivity(myProfileIntent);
                return true;



            default: return super.onOptionsItemSelected(item);
        }

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextViewFriendLastSeen = (TextView) findViewById(R.id.text_view_friend_last_seen);


        mFriendProfilePic = (ImageView) findViewById(R.id.image_view_friend_profile_pic);
        Glide.with(mFriendProfilePic.getContext())
                .load(mCurrentChat.getChatPhotoURL())
                .into(mFriendProfilePic);

        mTextViewFriendFirstName = (TextView) findViewById(R.id.text_view_friend_first_name);
        mTextViewFriendFirstName.setText(mCurrentChat.getChatName());


        mLinearLayoutChatNameAndLastSeen = (LinearLayout) findViewById(R.id.linear_layout_chat_name_and_lastseen);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.image_button_photo_picker);
        mButtonSendMessage = (Button) findViewById(R.id.button_send_message);
        mEditTextWriteMessage = (EditText) findViewById(R.id.edit_text_write_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_file_uploading);
        mLinearLayoutOperationOnMessage = (LinearLayout) findViewById(R.id.linear_layout_operation_on_message);
        mImageViewGoBack = (ImageView)  findViewById(R.id.image_view_go_back);
        mImageViewShowAbout = (ImageView)  findViewById(R.id.image_view_message_info);
        mImageViewDeleteMessage = (ImageView) findViewById(R.id.image_view_message_delete);
        mImageViewForwardMessage = (ImageView)  findViewById(R.id.image_view_message_forward);
        mToolBar = (Toolbar)  findViewById(R.id.toolbar);

        mCurrentChatMessagesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS).child(mCurrentChat.chatId);
        mGroupDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS);
        mCurrentChatDetailsRef = new  Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(mCurrentChat.getChatId());

        if(mCurrentChat.chatType.equals("Personal"))
            mCurrentMyChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(Utils.encodeEmail(mCurrentChat.getChatEmail()));
        else if(mCurrentChat.chatType.equals("Group"))
        {
            mCurrentMyChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(mCurrentChat.getChatEmail());
            groupParticipantsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mCurrentChat.getChatEmail()).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS);
        }


        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("mediaFiles");

         mListViewMessage = (ListView) findViewById(R.id.list_view_message);

        mChatDetailsRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(mCurrentChat.chatId);
        mArrayListGroupParticipants = new ArrayList<User>();

        mAllNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS);
    }



    public void sendMessage(String messageText,String mediaFileURL,String mediaFileType)
    {
        addSystemGeneratedMessage();

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
        if(mCurrentChat.getChatType().equals("Personal"))
        {
            statusDelivered = getResources().getString(R.string.message_status_delivered);
            visibleTo = mCurrentUser.getEmail() + "," + mCurrentChat.getChatEmail();
            updateNotification(mCurrentChat.chatEmail,lastMessage,mCurrentUser.getName());
        }
        else if(mCurrentChat.getChatType().toString().equals("Group"))
        {
            statusDelivered = mCurrentUser.getEmail();
            for(int i=0;i<mArrayListGroupParticipants.size();i++)
            {
                visibleTo = visibleTo + mArrayListGroupParticipants.get(i).getEmail()+",";

                //send notification to all the participants except to myself
                if(!mArrayListGroupParticipants.get(i).getEmail().equals(statusDelivered))
                   updateNotification(mArrayListGroupParticipants.get(i).getEmail(),lastMessage,mCurrentChat.chatName);
            }

        }

        String messageId = mCurrentChatMessagesRef.push().getKey();



        Message message = new Message(messageText, messageId, statusDelivered, senderFirstName, senderEmail,visibleTo, timestampMessageSentAt, mediaFileURL,mediaFileType);

        HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
        timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);






        ChatDetails chatDetails = new ChatDetails(lastMessage,mNoOfMessagesAvailable + 1,timestampLastUpdate);
        mChatDetailsRef.setValue(chatDetails);


        mCurrentChatMessagesRef.child(messageId).setValue(message);

    }

    public void chooseMediaFile() {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Dialog chooseMediaFileDialog = new Dialog(this);
        chooseMediaFileDialog.setTitle("Choose your action");
        chooseMediaFileDialog.setContentView(R.layout.dialog_choose_mediafile);

        ImageButton imageButtonImageUpload = (ImageButton) chooseMediaFileDialog.findViewById(R.id.image_button_choose_imagefile);
        ImageButton imageButtonVideoUpload = (ImageButton) chooseMediaFileDialog.findViewById(R.id.image_button_choose_videofile);
        ImageButton imageButtonAudioUpload = (ImageButton) chooseMediaFileDialog.findViewById(R.id.image_button_choose_audiofile);

        imageButtonImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                chooseMediaFileDialog.dismiss();

            }
        });


        imageButtonVideoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a Video "), RC_VIDEO_PICKER);
                chooseMediaFileDialog.dismiss();

            }
        });


        imageButtonAudioUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_AUDIO_PICKER);
                chooseMediaFileDialog.dismiss();

            }
        });

        chooseMediaFileDialog.show();
    }

    public void uploadAndDownloadMediaFile(final Intent data,final String fileType)
    {

        final Application myChatApplication = this.getApplication();
        ((MyChatApplication) myChatApplication).setIntentSelectedMediaFile(data);


       /* Uri selectedImageUri = data.getData();

        // Get a reference to store file at chat_photos/<FILENAME>
        StorageReference selectedPhotoRef = mFirebaseStorageReference.child(selectedImageUri.getLastPathSegment());

        // Upload file to Firebase Storage
        UploadTask uploadTask = selectedPhotoRef.putFile(selectedImageUri);
*/
        Intent intentToselectedMediaFile = new Intent(ChatDetailsActivity.this,SelectedMediafileActivity.class);
        intentToselectedMediaFile.putExtra("mediaFileType",fileType);
        //intentToselectedMediaFile.putExtra("mediaFileLocation",downloadUrl.toString());
        startActivityForResult(Intent.createChooser(intentToselectedMediaFile, "Complete action using"), RC_TEXT_PICKER_FOR_MEDIAFILE);


/*
        uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // When the image has successfully uploaded, we get its download URL
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Intent intentToselectedMediaFile = new Intent(ChatDetailsActivity.this,SelectedMediafileActivity.class);
                intentToselectedMediaFile.putExtra("mediaFileType",fileType);
                intentToselectedMediaFile.putExtra("mediaFileLocation",downloadUrl.toString());
                startActivityForResult(Intent.createChooser(intentToselectedMediaFile, "Complete action using"), RC_TEXT_PICKER_FOR_MEDIAFILE);
                //startActivity(intentToselectedMediaFile);
                //finish();

                //sendMessage(null, downloadUrl.toString(), fileType);

            }
        });*/
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.makeMeOnOrOffline(mEncodedEmail, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentMyChatRef.child("noOfMessagesIHaveSeen").setValue(mNoOfMessagesAvailable);
        Utils.makeMeOnOrOffline(mEncodedEmail, false);

        //Update the chatDetails
        String myFirstName = Utils.getFirstName(mCurrentUser.getName());
        if( mCurrentChatDetails.getWhoAreTyping() != null && mCurrentChatDetails.getWhoAreTyping().contains(myFirstName))
        {
            mCurrentChatDetails.setWhoAreTyping(mCurrentChatDetails.getWhoAreTyping().replace(","+myFirstName,""));
            mCurrentChatDetailsRef.setValue(mCurrentChatDetails);
        }

    }

    public void addSystemGeneratedMessage()
    {
       /* HashMap<String, Object> timestampCurrentTime = new HashMap<String, Object>();
        timestampCurrentTime.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
*/
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String lastUpdateDay = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
              new Date(mCurrentChatDetails.getTimestampLastUpdateLong()));

        String today = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                new Date(timestamp.getTime()));

        if((lastUpdateDay.equals(today) && mCurrentChatDetails.getTotalNoOfMessagesAvailable()==0) ||
                 !lastUpdateDay.equals(today)) {

            String senderFirstName = "SystemGeneratedMessage";
            String senderEmail = "SystemGeneratedMessage";
            HashMap<String, Object> timestampMessageSentAt = new HashMap<String, Object>();
            timestampMessageSentAt.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


            String statusDelivered = getResources().getString(R.string.message_status_delivered);
            String visibleTo = "All";


            String messageId = mCurrentChatMessagesRef.push().getKey();

            Message systemGeneratedMessage = new Message(today, messageId, statusDelivered, senderFirstName, senderEmail, visibleTo, timestampMessageSentAt, null, null);
            mCurrentChatMessagesRef.child(messageId).setValue(systemGeneratedMessage);
        }
    }


    public void updateNotification(String friendEmail,final String notificationText,final String chatName)
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
                        if(friendNotification.getNotificationFromChatId().equals(mCurrentChat.chatId))
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages.");
                        }
                        else if(friendNotification.getNotificationFromChatId().contains(mCurrentChat.chatId))
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages from "+friendNotification.getNotificationFromNoOfChats()+" chats.");
                        }
                        else
                        {
                            friendNotification.setNoOfNewMessages(friendNotification.getNoOfNewMessages() + 1);
                            friendNotification.setNotificationFromChatName("More than 1");
                            friendNotification.setNotificationFromChatId(friendNotification.getNotificationFromChatId()+","+mCurrentChat.chatId);
                            friendNotification.setNotificationFromNoOfChats(friendNotification.getNotificationFromNoOfChats() + 1);
                            friendNotification.setNotificationText(friendNotification.getNoOfNewMessages() + " new messages from "+friendNotification.getNotificationFromNoOfChats()+" chats.");

                        }
                        mNotificationTo.setValue(friendNotification);
                    }
                }
                else
                {
                    MyChatNotification friendNotification = new MyChatNotification(notificationText,mCurrentChat.chatId,chatName,1,1);
                    mNotificationTo.setValue(friendNotification);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setOnlineOrOffLine()
    {
        if(mCurrentChat.getChatType().equals("Group"))
        {
            mTextViewFriendLastSeen.setText("");
            return;
        }
        if (mCurrentChat.isOnline()) {

            mTextViewFriendLastSeen.setText("Online");
            mTextViewFriendLastSeen.setTextColor(Color.parseColor("#000000"));
        }
        else {
            String lastSeenTime = Utils.SIMPLE_DATE_FORMAT.format(
                    new Date(mCurrentChat.getTimestampLastSeenLong()));
            mTextViewFriendLastSeen.setText(lastSeenTime);
            mTextViewFriendFirstName.setText(mCurrentChat.getChatName());
        }
    }

}
