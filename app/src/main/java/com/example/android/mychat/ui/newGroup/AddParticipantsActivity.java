package com.example.android.mychat.ui.newGroup;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.ui.allChats.AllChatsAdapter;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.ui.myProfile.MyProfileActivity;
import com.example.android.mychat.ui.searchFriends.AutocompleteFriendAdapter;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class AddParticipantsActivity extends AppCompatActivity {

    public static Activity addParticipantsActivity;

    ListView mListViewMyFriends;
    EditText mEditTextSearchFriend;
    //ImageView mImageViewAddFriend;
    FloatingActionButton mFloatingActionButtonGoNext;

    private String mEncodedEmail;
    private String mChatEmail;
    private Firebase mMyFriendsReference;

    private Firebase mCurrentUserReference;
    private AllFriendsAdapter mAllFriendsAdapter;
    ArrayList<User> mArrayListGroupParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);

        addParticipantsActivity = this;

        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail =  intent.getStringExtra("encodedEmail");
            mChatEmail = intent.getStringExtra("chatEmail");
        }

        initializeScreen();

        mMyFriendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mAllFriendsAdapter = new AllFriendsAdapter(AddParticipantsActivity.this, User.class,
                            R.layout.item_single_friend, mMyFriendsReference.orderByChild(Constants.FIREBASE_PROPERTY_NAME), mChatEmail);

                    mListViewMyFriends.setAdapter(mAllFriendsAdapter);


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mEditTextSearchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textEntered = mEditTextSearchFriend.getText().toString().toUpperCase();

                if (mAllFriendsAdapter != null)
                    mAllFriendsAdapter.cleanup();

                if (textEntered.equals("") || textEntered.length() < 2) {
                    mListViewMyFriends.setAdapter(null);
                } else {
                    mAllFriendsAdapter = new AllFriendsAdapter(AddParticipantsActivity.this, User.class,
                            R.layout.item_single_friend, mMyFriendsReference.orderByChild(Constants.FIREBASE_PROPERTY_NAME).startAt(textEntered).endAt(textEntered + "~"), mChatEmail);

                    mListViewMyFriends.setAdapter(mAllFriendsAdapter);


                }

            }
        });

        mListViewMyFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User friend = mAllFriendsAdapter.getItem(position);

                if (friend != null) {
                    ImageView imageViewAddFriend = (ImageView) view.findViewById(R.id.image_view_add_friend);
                    if (imageViewAddFriend.getVisibility() == View.VISIBLE) {
                        imageViewAddFriend.setVisibility(View.GONE);

                        for (int i = 0; i < mArrayListGroupParticipants.size(); i++) {
                            User user = mArrayListGroupParticipants.get(i);
                            if (user.getEmail().equals(friend.getEmail())) {
                                mArrayListGroupParticipants.remove(i);
                                break;
                            }
                        }

                        //mArrayListGroupParticipants.remove(friend);
                    } else if (imageViewAddFriend.getVisibility() == View.GONE) {
                        imageViewAddFriend.setVisibility(View.VISIBLE);
                        mArrayListGroupParticipants.add(friend);

                    }

                }
            }
        });

        mFloatingActionButtonGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrayListGroupParticipants.size() <= 1) {
                    Toast.makeText(AddParticipantsActivity.this, "Atleast 1 participant required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mChatEmail.equals("")) {
                    Intent addSubjectIntent = new Intent(AddParticipantsActivity.this, AddSubjectActivity.class);
                    addSubjectIntent.putExtra("groupParticipants", mArrayListGroupParticipants);
                    addSubjectIntent.putExtra("encodedEmail", mEncodedEmail);
                    startActivity(addSubjectIntent);
                } else {
                    AddNewParticipants();
                }

            }
        });


        mCurrentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    mArrayListGroupParticipants.add(currentUser);
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
                if(mEditTextSearchFriend.getVisibility() == View.GONE) {
                    mEditTextSearchFriend.setVisibility(View.VISIBLE);

                }
                else {
                    mEditTextSearchFriend.setVisibility(View.GONE);
                    mAllFriendsAdapter = new AllFriendsAdapter(AddParticipantsActivity.this, User.class,
                            R.layout.item_single_friend, mMyFriendsReference.orderByChild(Constants.FIREBASE_PROPERTY_NAME),mChatEmail);

                    mListViewMyFriends.setAdapter(mAllFriendsAdapter);
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

        mListViewMyFriends = (ListView) findViewById(R.id.list_view_my_friends);
        //mImageViewAddFriend = (ImageView) findViewById(R.id.image_view_add_friend);
        mFloatingActionButtonGoNext = (FloatingActionButton) findViewById(R.id.fab_next);
        mMyFriendsReference =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(mEncodedEmail);
        mEditTextSearchFriend = (EditText) findViewById(R.id.edit_text_search_friends);

        mCurrentUserReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(mEncodedEmail);

        mArrayListGroupParticipants = new ArrayList<User>();

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

    public void AddNewParticipants()
    {
        Firebase currentChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(mChatEmail);
        currentChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Chat currentChat = dataSnapshot.getValue(Chat.class);
                    currentChat.setNoOfMessagesIHaveSeen(0);

                    if (currentChat != null) {
                        Firebase mMyChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
                        Firebase mGroupDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS);
                        Firebase mGroupParticipantsRef = mGroupDetailsRef.child(currentChat.chatEmail).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS);

                        for (int i = 0; i < mArrayListGroupParticipants.size(); i++) {
                            User participant = mArrayListGroupParticipants.get(i);
                            if (participant != null) {
                                mMyChatsRef.child(Utils.encodeEmail(participant.getEmail())).child(currentChat.chatEmail).setValue(currentChat);
                                mGroupParticipantsRef.child(Utils.encodeEmail(participant.getEmail())).setValue(participant);
                            }
                        }


                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Toast.makeText(this,"Participant(s) added successfully.",Toast.LENGTH_SHORT).show();
        this.finish();

    }
}
