package com.example.android.mychat.ui.searchFriends;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.ViewMediaFile.ViewImageActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;


/**
 * Populates the list_view_friends_autocomplete inside AddFriendActivity
 */
public class AutocompleteFriendAdapter extends FirebaseListAdapter<User> {

    String mEncodedEmail;
    User mCurrentUser;
    public ListView mListViewFriendsAutoComplite;


    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public AutocompleteFriendAdapter(Activity activity, Class<User> modelClass, int modelLayout,
                                     Query ref, String encodedEmail,User currentUser) {
        super(activity, modelClass, modelLayout, ref);
        this.mEncodedEmail = encodedEmail;
        this.mCurrentUser = currentUser;
        this.mActivity = activity;

    }



    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(View view, final User user) {

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
        linearLayoutAction.setVisibility(View.VISIBLE);

        final Button buttonRequest = (Button) view.findViewById(R.id.button_request);
        buttonRequest.setVisibility(View.VISIBLE);
        int status = checkRequestStatus(buttonRequest, user);

        if(status == 0)
        {
            view.setVisibility(View.GONE);
            return;
        }
        else
            view.setVisibility(View.VISIBLE);



        /**
         * Set the onClickListener to a single list item
         * If selected email is not friend already and if it is not the
         * current user's email, we add selected user to current user's friends
         */
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * If selected user is not current user proceed
                 */

                if(buttonRequest.getText().toString().equals("Send Request"))
                {
                    Firebase currentUserSendRequestRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(mEncodedEmail);
                    Firebase receiversPendingRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(Utils.encodeEmail(user.getEmail()));

                    final Firebase sentRequestsRef = currentUserSendRequestRef.child(Utils.encodeEmail(user.getEmail()));
                    final Firebase receiveRequestsRef = receiversPendingRequestsRef.child(mEncodedEmail);

                    sentRequestsRef.setValue(user);
                    receiveRequestsRef.setValue(mCurrentUser);


                    Toast.makeText(mActivity,
                            mCurrentUser.getName(),
                            Toast.LENGTH_LONG).show();

                    buttonRequest.setText("Request Sent");
                    int colourCode = ContextCompat.getColor(mActivity, R.color.tw__light_gray);
                    buttonRequest.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);

                }


            }
        });

    }


    /** Checks if the friend you try to add is the current user **/
    private boolean isNotCurrentUser(User user) {
        if (user.getEmail().equals(Utils.decodeEmail(mEncodedEmail))) {
            /* Toast appropriate error message if the user is trying to add themselves  */
            Toast.makeText(mActivity,
                    mActivity.getResources().getString(R.string.toast_you_cant_add_yourself),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /** Checks if the friend you try to add is already added, given a dataSnapshot of a user **/
    private boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, User user) {
        if (dataSnapshot.getValue(User.class) != null) {
            /* Toast appropriate error message if the user is already a friend of the user */
            String friendError = String.format(mActivity.getResources().
                            getString(R.string.toast_is_already_your_friend),
                    user.getName());

            Toast.makeText(mActivity,
                    friendError,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private int checkRequestStatus(final Button button ,User user)
    {

        final int colourCode = ContextCompat.getColor(mActivity, R.color.tw__light_gray);

        if(mEncodedEmail.equals(Utils.encodeEmail(user.getEmail())))
        {
            button.setText("Can't");
            button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
            return 0;
        }
        Firebase currentUserAllFriendsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS).child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));
        currentUserAllFriendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User friend = dataSnapshot.getValue(User.class);
                if (friend != null) {
                    button.setText("Friend");
                    button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
                    return;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                return;
            }
        });

        Firebase currentUserPendingRequestsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS).child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));
        currentUserPendingRequestsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User friend = dataSnapshot.getValue(User.class);
                if (friend != null) {
                    button.setText("Request Pending");
                    button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
                    return;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                return;
            }
        });

        Firebase currentUserSentRequestsReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS).child(mEncodedEmail).child(Utils.encodeEmail(user.getEmail()));
        currentUserSentRequestsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User friend = dataSnapshot.getValue(User.class);
                if (friend != null) {
                    button.setText("Request Sent");
                    button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
                    return;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                return;
            }
        });


        button.setText("Send Request");
        int primaryColourCode = ContextCompat.getColor(mActivity, R.color.primary);
        button.getBackground().setColorFilter(primaryColourCode, PorterDuff.Mode.MULTIPLY);
        return 1 ;
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
