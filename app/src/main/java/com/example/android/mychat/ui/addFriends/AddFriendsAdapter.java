package com.example.android.mychat.ui.addFriends;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.TicTacToe;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.ViewMediaFile.ViewImageActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.util.HashMap;

/**
 * Created by chandan on 05-03-2017.
 */
public class AddFriendsAdapter extends FirebaseListAdapter<User> {

    String mEncodedEmail;
    String mStatus;
    User mCurrentUser;
    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public AddFriendsAdapter(Activity activity, Class<User> modelClass, int modelLayout,
                             Query ref, String encodedEmail,User currentUser,String status) {
        super(activity, modelClass, modelLayout, ref);
        this.mEncodedEmail = encodedEmail;
        this.mCurrentUser = currentUser;
        this.mStatus = status;
        this.mActivity = activity;
    }


    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(final View view, final User user) {

        ImageView imageViewProfilePic = (ImageView) view.findViewById(R.id.image_view_profile_pic);
        //imageViewProfilePic.setImageURI(Uri.parse(user.getPhotoURL()));
        Glide.with(imageViewProfilePic.getContext())
                .load(user.getPhotoURL())
                .into(imageViewProfilePic);
        imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(user);
            }
        });


         /* Get friends email textview and set it's text to user.email() */
        TextView textViewFriendEmail = (TextView) view.findViewById(R.id.text_view_autocomplete_email);
        textViewFriendEmail.setText(Utils.decodeEmail(user.getEmail()));

        TextView textViewFriendName = (TextView) view.findViewById(R.id.text_view_autocomplete_name);
        textViewFriendName.setText(user.getName());

        LinearLayout linearLayoutAction = (LinearLayout) view.findViewById(R.id.linear_layout_action);


        Button buttonRequest = (Button) view.findViewById(R.id.button_request);
       final Button buttonReject = (Button) view.findViewById(R.id.button_reject);



        if(mStatus.equals("PendingRequests"))
        {
            int colourCode = ContextCompat.getColor(mActivity, R.color.primary);

            buttonRequest.setText("Accept");
            //buttonRequest.setBackgroundColor(colourCode);
            buttonRequest.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);

            colourCode = ContextCompat.getColor(mActivity, R.color.tw__composer_red);
            linearLayoutAction.setVisibility(View.VISIBLE);
            buttonRequest.setVisibility(View.VISIBLE);
            buttonReject.setVisibility(View.VISIBLE);
            buttonReject.setText("Reject");
            //buttonReject.setBackgroundColor(colourCode);
            buttonReject.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);

        }
        else if(mStatus.equals("SentRequests"))
        {
            buttonRequest.setVisibility(View.GONE);

            int colourCode = ContextCompat.getColor(mActivity, R.color.tw__composer_red);
            linearLayoutAction.setVisibility(View.VISIBLE);
            buttonReject.setVisibility(View.VISIBLE);
            buttonReject.setText("Cancel Request");
            //buttonReject.setBackgroundColor(colourCode);
            buttonReject.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);

        }
        else if(mStatus.equals("myFriends"))
        {
            buttonRequest.setVisibility(View.GONE);
            buttonReject.setVisibility(View.GONE);
            linearLayoutAction.setVisibility(View.GONE);
        }


        /**
         * Set the onClickListener to a single list item
         * If selected email is not friend already and if it is not the
         * current user's email, we add selected user to current user's friends
         */
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Firebase currentUserFriendsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(mEncodedEmail);
                    Firebase currentUserPendingRequestRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(mEncodedEmail);
                    Firebase currentUserSentRequestRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(Utils.encodeEmail(user.getEmail()));
                    Firebase otherUserFriendsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(Utils.encodeEmail(user.getEmail()));
                    Firebase MyChatRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);

                    final Firebase addFriendRequestsRef = currentUserFriendsRef.child(Utils.encodeEmail(user.getEmail()));
                    final Firebase sentRequestsRef = currentUserSentRequestRef.child(mEncodedEmail);
                    final Firebase pendingRequestsRef = currentUserPendingRequestRef.child(Utils.encodeEmail(user.getEmail()));
                    final Firebase otherUserAddFriendListRef = otherUserFriendsRef.child(mEncodedEmail);
                    final Firebase UserChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS);
                    final Firebase currentUserMyChatRef = MyChatRef.child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));
                    final Firebase otherUserMyChatRef = MyChatRef.child(Utils.encodeEmail(user.getEmail())).child(mEncodedEmail);
                    final Firebase mChatDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS);
                    final Firebase mAllGamesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_GAMES);



                addFriendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User friendUser = dataSnapshot.getValue(User.class);

                        if (friendUser == null) {
                            addFriendRequestsRef.setValue(user);
                            otherUserAddFriendListRef.setValue(mCurrentUser);

                            sentRequestsRef.setValue(null);
                            pendingRequestsRef.setValue(null);


                            HashMap<String, Object> timestampLastUpdate = new HashMap<String, Object>();
                            timestampLastUpdate.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                            HashMap<String, Object> timestampLastSeen = new HashMap<String, Object>();
                            timestampLastSeen.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


                            String chatId = UserChatsRef.push().getKey();
                            UserChatsRef.child(chatId).setValue("Hii all");

                            ChatDetails chatDetails = new ChatDetails("",0,timestampLastUpdate);
                            mChatDetailsRef.child(chatId).setValue(chatDetails);

                            String chatNameForOtherUser =Utils.getFirstName(mCurrentUser.getName());
                            String chatNameForCurronUser =Utils.getFirstName(user.getName());

                            Chat chatForOtherUser = new Chat(chatNameForOtherUser,mCurrentUser.getEmail(),"Personal",chatId,mCurrentUser.getPhotoURL(),0,true,timestampLastUpdate,timestampLastSeen);
                            Chat chatForCurrentUser = new Chat(chatNameForCurronUser,user.getEmail(),"Personal",chatId,user.getPhotoURL(),0,false,timestampLastUpdate,timestampLastSeen);

                            currentUserMyChatRef.setValue(chatForCurrentUser);
                            otherUserMyChatRef.setValue(chatForOtherUser);


                            int[][] gameTable = {{0,0,0},{0,0,0},{0,0,0}};
                            TicTacToe ticTacToe = new TicTacToe(mCurrentUser.getEmail(),user.getEmail(),0,0,mCurrentUser.getEmail(),gameTable);
                            mAllGamesRef.child(chatId).child(Constants.FIREBASE_PROPERTY_TICTACTOE).setValue(ticTacToe);


                            Toast.makeText(mActivity,
                                    mActivity.getResources().getString(R.string.toast_you_became_friend_of)+user.getName(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



               view.setVisibility(View.GONE);
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firebase sentRequestsRef;
                Firebase pendingRequestsRef;

                if(buttonReject.getText().equals("Reject"))
                {
                     sentRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(Utils.encodeEmail(user.getEmail())).child(mEncodedEmail);
                     pendingRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));


                    sentRequestsRef.setValue(null);
                    pendingRequestsRef.setValue(null);
                }
                else if(buttonReject.getText().equals("Cancel Request"))
                {
                    sentRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));
                    pendingRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(Utils.encodeEmail(user.getEmail())).child(mEncodedEmail);

                    sentRequestsRef.setValue(null);
                    pendingRequestsRef.setValue(null);
                }

                //view.setVisibility(View.GONE);
            }
        });

    }


    public void showImageViewDialogFragment(final User user) {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setTitle(Utils.getFirstName(user.getName()));
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewImageDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_image_dialog);
        Glide.with(imageViewImageDialog.getContext())
                .load(user.getPhotoURL())
                .into(imageViewImageDialog);

        imageViewImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                viewImageIntent.putExtra("imageURL", user.getPhotoURL());
                viewImageIntent.putExtra("activityTitle", Utils.getFirstName(user.getName()));
                mActivity.startActivity(viewImageIntent);
                viewImageDialog.dismiss();
            }
        });



        viewImageDialog.show();
    }



}
