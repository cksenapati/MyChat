package com.example.android.mychat.ui.userDetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.ViewMediaFile.ViewImageActivity;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by chandan on 08-03-2017.
 */
public class GroupParticipantsAdapter extends FirebaseListAdapter<User> {

    String mGroupEmail;
    String mCurrentUserEmail;
    boolean mIsCurrentUserAdmin;
    final  Firebase groupAdminsRef;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public GroupParticipantsAdapter(Activity activity, Class<User> modelClass, int modelLayout,
                                    Query ref,String groupEmail, String currentUserEmail) {
        super(activity, modelClass, modelLayout, ref);

        this.mGroupEmail = groupEmail;
        this.mActivity = activity;
        this.mCurrentUserEmail = currentUserEmail;

        groupAdminsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mGroupEmail).child(Constants.FIREBASE_PROPERTY_ADMINS);
        groupAdminsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String allAdmin = dataSnapshot.getValue(String.class);
                    if(allAdmin != null)
                    {
                        if(allAdmin.contains(mCurrentUserEmail))
                            mIsCurrentUserAdmin = true;
                        else
                            mIsCurrentUserAdmin = false;
                    }
                    else
                        mIsCurrentUserAdmin = false;
                }
                else
                    mIsCurrentUserAdmin = false;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(final View view, final User friend) {



        ImageView imageViewFriendProfilePic = (ImageView) view.findViewById(R.id.image_view_friend_profile_pic);
        Glide.with(imageViewFriendProfilePic.getContext())
                .load(friend.getPhotoURL())
                .into(imageViewFriendProfilePic);
        imageViewFriendProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(friend);
            }
        });


        TextView textViewFriendName = (TextView) view.findViewById(R.id.text_view_friend_name);
        textViewFriendName.setText(friend.getName());


        TextView textViewFriendEmail = (TextView) view.findViewById(R.id.text_view_friend_email);
        textViewFriendEmail.setText(friend.getEmail());

       final int colourCode = ContextCompat.getColor(mActivity, R.color.primary);
       final TextView textViewExtraText = (TextView) view.findViewById(R.id.text_view_extra_text);
        groupAdminsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String allAdmin = dataSnapshot.getValue(String.class);
                    if(allAdmin != null)
                    {
                        if(allAdmin.contains(friend.getEmail()))
                        {
                            textViewExtraText.setVisibility(View.VISIBLE);
                            textViewExtraText.setTextColor(colourCode);
                            textViewExtraText.setText("Admin");
                        }
                        else
                        {
                            textViewExtraText.setVisibility(View.GONE);
                        }
                    }

                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        final  ImageView imageViewAddFriend = (ImageView) view.findViewById(R.id.image_view_add_friend);
       /* view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showParticipantActionDialog(friend);
                return false;
            }
        });


    }

    public void showImageViewDialogFragment(final User friend) {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setTitle(Utils.getFirstName(friend.getName()));
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewImageDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_image_dialog);
        Glide.with(imageViewImageDialog.getContext())
                .load(friend.getPhotoURL())
                .into(imageViewImageDialog);

        imageViewImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                viewImageIntent.putExtra("imageURL", friend.getPhotoURL());
                viewImageIntent.putExtra("activityTitle", Utils.getFirstName(friend.getName()));
                mActivity.startActivity(viewImageIntent);
                viewImageDialog.dismiss();
            }
        });



        viewImageDialog.show();
    }

    public void showParticipantActionDialog(final User friend ) {

        final Dialog participantDialog = new Dialog(mActivity);
        participantDialog.setContentView(R.layout.dialog_choose_action_on_participant);

        final TextView textViewMakeOrRemoveAsAdmin = (TextView) participantDialog.findViewById(R.id.text_view_make_or_remove_admin);
        TextView textViewViewUserDetails = (TextView) participantDialog.findViewById(R.id.text_view_view_user_details);

        textViewViewUserDetails.setText("view "+ friend.getName());
        if(mIsCurrentUserAdmin)
        {
            textViewMakeOrRemoveAsAdmin.setVisibility(View.VISIBLE);
            groupAdminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String allAdmins = dataSnapshot.getValue(String.class);
                        if (allAdmins != null) {
                            if (allAdmins.contains(friend.getEmail()))
                                textViewMakeOrRemoveAsAdmin.setText("Remove from Admin");
                            else
                                textViewMakeOrRemoveAsAdmin.setText("Make Admin");
                        }
                        else
                            textViewMakeOrRemoveAsAdmin.setVisibility(View.GONE);

                    }
                    else
                        textViewMakeOrRemoveAsAdmin.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            textViewMakeOrRemoveAsAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeOrRemoveAsAdmin(friend.getEmail());
                    participantDialog.dismiss();
                }
            });
        }
        else
        {
            textViewMakeOrRemoveAsAdmin.setVisibility(View.GONE);
        }


        textViewViewUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                participantDialog.dismiss();
            }
        });


        participantDialog.show();
    }

    public void makeOrRemoveAsAdmin(final String participantEmail)
    {
        groupAdminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String allAdmins = dataSnapshot.getValue(String.class);
                    if(allAdmins != null)
                    {
                        if(allAdmins.contains(participantEmail))
                           allAdmins = allAdmins.replace(participantEmail,"");
                        else
                            allAdmins = allAdmins + "," +participantEmail;

                        groupAdminsRef.setValue(allAdmins);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
