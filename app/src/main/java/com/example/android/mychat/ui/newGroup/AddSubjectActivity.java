package com.example.android.mychat.ui.newGroup;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.Message;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class AddSubjectActivity extends AppCompatActivity {

    String mEncodedEmail;
    ListView mListViewgroupParticipants;
    EditText mGroupName;
    ImageView mGroupProfilePic;
    FloatingActionButton mFloatingActionButtonCreateGroup;


    ArrayList<User> mArrayListGroupParticipants;
    String mGroupProfilePicDownloadableURL;

    //Firebase mGroupChatsRef;
    Firebase mAllChatsRef;
    Firebase mMyChatsRef;
    Firebase mGroupDetailsRef;
    Firebase mGroupParticipantsRef;
    Firebase mChatDetailsRef;
    Firebase mGroupAdminsRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail =  intent.getStringExtra("encodedEmail");
            mArrayListGroupParticipants = (ArrayList<User>) intent.getSerializableExtra("groupParticipants");
        }
        initializeScreen();


        GroupParticipantsAdapter adapter = new GroupParticipantsAdapter(this, mArrayListGroupParticipants);
        // Attach the adapter to a ListView
        mListViewgroupParticipants.setAdapter(adapter);



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

        mListViewgroupParticipants = (ListView) findViewById(R.id.list_view_group_participants);
        mGroupName = (EditText) findViewById(R.id.edit_text_group_name);
        mGroupProfilePic = (ImageView) findViewById(R.id.image_view_group_profile_pic);
        mFloatingActionButtonCreateGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);

        //mGroupChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_CHATS);
        mAllChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS);
        mMyChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
        mGroupDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS);
        mChatDetailsRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS);
        mGroupProfilePicDownloadableURL = "https://firebasestorage.googleapis.com/v0/b/mychat-309a2.appspot.com/o/icons%2FgroupProfilePic.png?alt=media&token=eb190b9c-6c56-417e-a5e5-67c581c5bd54";

        Glide.with(mGroupProfilePic.getContext())
                .load(mGroupProfilePicDownloadableURL)
                .into(mGroupProfilePic);

    }

    public void createGroup(View v)
    {
        //For group chat...chatEmail is the email of the creates

        if(mGroupName.getText() == null || mGroupName.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this,"provide group name",Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = mMyChatsRef.child(mEncodedEmail).push().getKey();

        String groupName = mGroupName.getText().toString().toUpperCase();
        String chatEmail = groupId;
        String chatType = "Group";
        String lastMessage = "Welcome all";
        String chatId = mAllChatsRef.push().getKey();
        String chatPhotoURL = mGroupProfilePicDownloadableURL;
        int noOfMessagesIHaveSeen = 0;
        boolean online = false;
        HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
        timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        HashMap<String, Object> timestampLastSeen = new HashMap<String, Object>();
        timestampLastSeen.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        Chat groupChat = new Chat(groupName,chatEmail,chatType,chatId,chatPhotoURL,noOfMessagesIHaveSeen,online,timestampLastUpdate,timestampLastSeen);

        ChatDetails chatDetails = new ChatDetails("",0,timestampLastUpdate);
        mChatDetailsRef.child(chatId).setValue(chatDetails);

        mAllChatsRef.child(chatId).setValue(lastMessage);



        mGroupParticipantsRef = mGroupDetailsRef.child(groupId).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS);
        //mGroupChatsRef.child(groupId).setValue(groupChat);
        mMyChatsRef.child(mEncodedEmail).child(groupId).setValue(groupChat);
        for ( int i = 0;  i < mArrayListGroupParticipants.size(); i++){
            User participant = mArrayListGroupParticipants.get(i);
            if(participant != null)
            {
                mMyChatsRef.child(Utils.encodeEmail(participant.getEmail())).child(groupId).setValue(groupChat);
                mGroupParticipantsRef.child(Utils.encodeEmail(participant.getEmail())).setValue(participant);
            }
        }

        //Add Admin of the group
        mGroupAdminsRef = mGroupDetailsRef.child(groupId).child(Constants.FIREBASE_PROPERTY_ADMINS);
        mGroupAdminsRef.setValue(Utils.decodeEmail(mEncodedEmail));

        Toast.makeText(this,"Group created successfully.",Toast.LENGTH_SHORT).show();
        AddParticipantsActivity.addParticipantsActivity.finish();
        this.finish();


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
}
