package com.example.android.mychat.ui.newGroup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
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

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chandan on 08-03-2017.
 */
public class AllFriendsAdapter extends FirebaseListAdapter<User> {


    String mChatEmail;
    ArrayList<User> mArrayListGroupParticipants;
    Firebase mGroupParticipantsRef;


    public AllFriendsAdapter(Activity activity, Class<User> modelClass, int modelLayout,
                             Query ref,String chatEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mChatEmail = chatEmail;
        this.mActivity = activity;
        mArrayListGroupParticipants = new ArrayList<User>();

        if(!mChatEmail.equals(""))
            getAllParticipants();
    }



    @Override
    protected void populateView(final View view, final User friend) {

        int colourCode = ContextCompat.getColor(mActivity, R.color.tw__light_gray);


        if(!mChatEmail.equals(""))
        {
            for (int i = 0; i < mArrayListGroupParticipants.size(); i++) {
                User user = mArrayListGroupParticipants.get(i);
                if (user.getEmail().equals(friend.getEmail())) {
                    //view.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
                    view.setBackgroundColor(colourCode);
                    view.setEnabled(false);
                    view.setOnClickListener(null);
                    break;
                }
            }
        }


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

    public void getAllParticipants()
    {
        mGroupParticipantsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(mChatEmail).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS);
        mGroupParticipantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        User participant = snapshot.getValue(User.class);
                        if(participant != null)
                           mArrayListGroupParticipants.add(participant);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
