package com.example.android.mychat.ui.allChats;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.ui.newGroup.AddParticipantsActivity;
import com.example.android.mychat.ui.newGroup.AllFriendsAdapter;
import com.example.android.mychat.ui.newGroup.GroupParticipantsAdapter;
import com.example.android.mychat.ui.searchFriends.AutocompleteFriendAdapter;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link allChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class allChatsFragment extends Fragment {


    public static User mCurrentUser;
    public static String mEncodedEmail;

    public ListView mListViewMyChats;
    private Firebase mMyChatsReference;
    private AllChatsAdapter mAllChatsAdapter;
    EditText mEditTextSearch;

    public ArrayList<Chat> mArrayListMyChats;
    public ArrayList<Chat> mArrayListMyFilteredChats;

    public allChatsFragment() {
        // Required empty public constructor
    }


    public static allChatsFragment newInstance(String encodedEmail, User currentUser) {
        allChatsFragment fragment = new allChatsFragment();
        Bundle args = new Bundle();
        mCurrentUser = currentUser;
        mEncodedEmail = encodedEmail;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Initalize UI elements
         */
        View rootView = inflater.inflate(R.layout.fragment_all_chats, container, false);
        initializeScreen(rootView);




        mMyChatsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail);
        mMyChatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mArrayListMyChats.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        Chat eachChat = snapshot.getValue(Chat.class);
                        if(eachChat != null)
                        {
                            mArrayListMyChats.add(eachChat);
                        }

                    }

                    if(mArrayListMyChats.size() > 1)
                      Collections.sort(mArrayListMyChats);
                    mAllChatsAdapter = new AllChatsAdapter(getActivity(), mArrayListMyChats);
                    // Attach the adapter to a ListView
                    mListViewMyChats.setAdapter(mAllChatsAdapter);

                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textEntered = mEditTextSearch.getText().toString().toUpperCase();

               /* if (mAllChatsAdapter != null)
                    mAllChatsAdapter.clear();*/

                if (textEntered.equals("") || textEntered.length() < 1) {
                    mAllChatsAdapter = new AllChatsAdapter(getActivity(), mArrayListMyChats);
                    mListViewMyChats.setAdapter(mAllChatsAdapter);

                } else {
                    mArrayListMyFilteredChats.clear();
                    for(int i=0;i<mArrayListMyChats.size();i++)
                    {
                        if(mArrayListMyChats.get(i).getChatName().contains(textEntered))
                            mArrayListMyFilteredChats.add(mArrayListMyChats.get(i));
                    }

                    if(mArrayListMyFilteredChats.size() > 1)
                     Collections.sort(mArrayListMyFilteredChats);
                    mAllChatsAdapter = new AllChatsAdapter(getActivity(), mArrayListMyFilteredChats);
                    mListViewMyChats.setAdapter(mAllChatsAdapter);
                }

            }

        });




        /**
         * Set interactive bits, such as click events and adapters
         */
        mListViewMyChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = mAllChatsAdapter.getItem(position);
                if (chat != null) {
                    /* Get the list ID using the adapter's get ref method to get the Firebase
                     * ref and then grab the key.
                     */

                    final Application myChatApplication = getActivity().getApplication();
                    ((MyChatApplication) myChatApplication).setCurrentChat(chat);

                    Intent intent = new Intent(getActivity(), ChatDetailsActivity.class);
                    intent.putExtra("encodedEmail", mEncodedEmail);
                    intent.putExtra("currentUser", mCurrentUser);
                    intent.putExtra("currentChat", chat);

                    startActivity(intent);
                }
            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Link layout elements from XML
     */
    private void initializeScreen(View rootView) {
        mListViewMyChats = (ListView) rootView.findViewById(R.id.list_view_my_chats);
         mEditTextSearch = (EditText) getActivity().findViewById(R.id.edit_text_search_friends);
        mArrayListMyChats = new ArrayList<Chat>();
        mArrayListMyFilteredChats = new ArrayList<Chat>();
    }



}
