package com.example.android.mychat.ui.addFriends;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.searchFriends.AutocompleteFriendAdapter;
import com.example.android.mychat.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link addFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class addFriendsFragment extends Fragment {


    public ListView mListViewPendingRequests;
    public ListView mListViewSentRequests;
    public ListView mListViewMyFriends;

    public TextView mTextViewPendingRequests;
    public TextView mTextViewSentRequests;
    public TextView mTextViewMyFriends;

    private Firebase mSentRequestsReference;
    private Firebase mPendingRequestsReference;
    private Firebase mMyFriendsReference;
    private AddFriendsAdapter mPendingFriendsAdapter;
    private AddFriendsAdapter mSentFriendsAdapter;
    private AddFriendsAdapter mMyFriendsAdapter;
    private static String mEncodedEmail;
    public static User mCurrentUser;

    public static addFriendsFragment newInstance(String encodedEmail,User currentUser) {
        addFriendsFragment fragment = new addFriendsFragment();
        Bundle args = new Bundle();
        mEncodedEmail = encodedEmail;
        mCurrentUser = currentUser;
        fragment.setArguments(args);
        return fragment;
    }
    public addFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.fragment_add_friends, container, false);
        initializeScreen(rootView);

        //final String status;

        //status = "PendingRequests";
        mPendingRequestsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(mEncodedEmail);
        mPendingRequestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mPendingFriendsAdapter = new AddFriendsAdapter(getActivity(), User.class,
                            R.layout.single_autocomplete_item, mPendingRequestsReference.orderByChild(Constants.FIREBASE_PROPERTY_EMAIL),
                            mEncodedEmail, mCurrentUser, "PendingRequests");

                    mListViewPendingRequests.setAdapter(mPendingFriendsAdapter);
                    mTextViewPendingRequests.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(mPendingFriendsAdapter != null)
                        mPendingFriendsAdapter.cleanup();
                    mTextViewPendingRequests.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //status = "SentRequests";
        mSentRequestsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(mEncodedEmail);
        mSentRequestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mSentFriendsAdapter = new AddFriendsAdapter(getActivity(), User.class,
                            R.layout.single_autocomplete_item, mSentRequestsReference.orderByChild(Constants.FIREBASE_PROPERTY_EMAIL),
                            mEncodedEmail, mCurrentUser, "SentRequests");

                    mListViewSentRequests.setAdapter(mSentFriendsAdapter);
                    mTextViewSentRequests.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(mSentFriendsAdapter != null)
                       mSentFriendsAdapter.cleanup();
                    mTextViewSentRequests.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

       // status = "myFriends";
        mMyFriendsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(mEncodedEmail);
        mMyFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMyFriendsAdapter = new AddFriendsAdapter(getActivity(), User.class,
                            R.layout.single_autocomplete_item, mMyFriendsReference.orderByChild(Constants.FIREBASE_PROPERTY_EMAIL),
                            mEncodedEmail, mCurrentUser, "myFriends");

                    mListViewMyFriends.setAdapter(mMyFriendsAdapter);
                    mTextViewMyFriends.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(mMyFriendsAdapter != null)
                        mMyFriendsAdapter.cleanup();
                    mTextViewMyFriends.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void initializeScreen(View rootView) {
        mListViewPendingRequests = (ListView) rootView.findViewById(R.id.list_view_pending_friend_requests);
        mListViewSentRequests = (ListView) rootView.findViewById(R.id.list_view_sent_friends_requests);
        mListViewMyFriends = (ListView) rootView.findViewById(R.id.list_view_my_friends);

        mTextViewPendingRequests = (TextView) rootView.findViewById(R.id.text_view_pending_requests);
        mTextViewSentRequests = (TextView) rootView.findViewById(R.id.text_view_sent_requests);
        mTextViewMyFriends = (TextView) rootView.findViewById(R.id.text_view_my_friends);

    }


}
