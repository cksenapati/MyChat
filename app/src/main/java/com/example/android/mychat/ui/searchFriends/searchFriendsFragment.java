package com.example.android.mychat.ui.searchFriends;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.ui.newGroup.GroupParticipantsAdapter;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link searchFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class searchFriendsFragment extends Fragment {

    public EditText mEditTextSearchFriendEmail;
    public ListView mListViewFriendsAutoComplite;
    public TextView mTextViewRecommendedUsers;
    public Button mButtonSearchByName;
    public Button mButtonSearchByMailId;
    private Firebase mAllUsersReference;
    private Firebase mMyFriendsReference;
    private AutocompleteFriendAdapter mFriendsAutocompleteAdapter;
    private static String mEncodedEmail;
    public static  User mCurrentUser;

    ArrayList<String> mArrayListFriends;
    String mSearchBy;
    int primaryColourCode;
    int grayColourCode;

    public searchFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment searchFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static searchFriendsFragment newInstance(String encodedEmail,User currentUser) {
        searchFriendsFragment fragment = new searchFriendsFragment();
        Bundle args = new Bundle();
        mEncodedEmail = encodedEmail;
        mCurrentUser = currentUser;
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
        final View rootView = inflater.inflate(R.layout.fragment_search_friends, container, false);
        initializeScreen(rootView);


        /*suggestFriends();*/


        mEditTextSearchFriendEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });

        mButtonSearchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBy = getResources().getString(R.string.search_by_name_text);
                mButtonSearchByName.setBackgroundColor(primaryColourCode);
                mButtonSearchByMailId.setBackgroundColor(grayColourCode);
                search();
            }
        });

        mButtonSearchByMailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBy = getResources().getString(R.string.search_by_gmail_text);
                mButtonSearchByMailId.setBackgroundColor(primaryColourCode);
                mButtonSearchByName.setBackgroundColor(grayColourCode);
                search();
            }
        });


        // Inflate the layout for this fragment
        return rootView;


    }


    @Override
    public void onStart() {
        super.onStart();
    }


    /**
     * Updates the order of mListView onResume to handle sortOrderChanges properly
     */
    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void initializeScreen(View rootView) {

        mEditTextSearchFriendEmail = (EditText) rootView.findViewById(R.id.edit_text_search_friend_email);
        mListViewFriendsAutoComplite = (ListView)  rootView.findViewById(R.id.list_view_friends_autocomplete);
        mButtonSearchByName = (Button) rootView.findViewById(R.id.button_search_by_name);
        mButtonSearchByMailId = (Button) rootView.findViewById(R.id.button_search_by_mail_id);

        mAllUsersReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS);
        mMyFriendsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(mEncodedEmail);


        mSearchBy = getResources().getString(R.string.search_by_name_text);
        primaryColourCode = ContextCompat.getColor(getActivity(), R.color.primary);
        grayColourCode = ContextCompat.getColor(getActivity(), R.color.tw__light_gray);
        mButtonSearchByName.setBackgroundColor(primaryColourCode);

        mArrayListFriends = new ArrayList<String>();

        }

    public void search()
    {
        String textEntered = mEditTextSearchFriendEmail.getText().toString();

        if(textEntered.length() > 0) {

            if(mSearchBy.equals(getResources().getString(R.string.search_by_name_text))) {
                textEntered = textEntered.toUpperCase();
                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(getActivity(), User.class,
                        R.layout.single_autocomplete_item, mAllUsersReference.orderByChild(Constants.FIREBASE_PROPERTY_NAME).startAt(textEntered).endAt(textEntered + "~"),
                        mEncodedEmail, mCurrentUser);
            }
            else {
                textEntered = textEntered.toLowerCase();
                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(getActivity(), User.class,
                        R.layout.single_autocomplete_item, mAllUsersReference.orderByChild(Constants.FIREBASE_PROPERTY_EMAIL).startAt(textEntered).endAt(textEntered + "~"),
                        mEncodedEmail, mCurrentUser);
            }
            mListViewFriendsAutoComplite.setAdapter(mFriendsAutocompleteAdapter);

        }
        else
        {
            mListViewFriendsAutoComplite.setAdapter(null);
        }
    }


  /*  public  void suggestFriends()
    {

        mMyFriendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mArrayListFriends.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        User friend = snapshot.getValue(User.class);
                        mArrayListFriends.add(friend.getEmail());
                    }
                    showSuggestions();
                }
                else
                {
                    mArrayListFriends.clear();
                    showSuggestions();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    public void showSuggestions()
    {
        try
        {
            if (mArrayListFriends.size() == 0) {
                final Firebase suggestedFriendsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child("chandan10051994@gmail,com");

                mTextViewRecommendedUsers.setVisibility(View.VISIBLE);
                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(getActivity(), User.class,
                        R.layout.single_autocomplete_item, suggestedFriendsRef,
                        mEncodedEmail, mCurrentUser);
                mListViewFriendsAutoComplite.setAdapter(mFriendsAutocompleteAdapter);

            } else {
                final int randomValue = (new Random()).nextInt(mArrayListFriends.size());

                mTextViewRecommendedUsers.setVisibility(View.VISIBLE);
                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter(getActivity(), User.class,
                        R.layout.single_autocomplete_item, new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(Utils.encodeEmail(mArrayListFriends.get(randomValue))),
                        mEncodedEmail, mCurrentUser);
                mListViewFriendsAutoComplite.setAdapter(mFriendsAutocompleteAdapter);


            }
        }
        catch (Exception ex)
        {

        }
    }*/
}
