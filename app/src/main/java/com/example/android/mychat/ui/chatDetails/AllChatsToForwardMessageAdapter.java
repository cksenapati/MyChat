package com.example.android.mychat.ui.chatDetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

/**
 * Created by chandan on 08-03-2017.
 */
public class AllChatsToForwardMessageAdapter extends FirebaseListAdapter<Chat> {




    public AllChatsToForwardMessageAdapter(Activity activity, Class<Chat> modelClass, int modelLayout,
                                           Query ref) {
        super(activity, modelClass, modelLayout, ref);


    }



    @Override
    protected void populateView(final View view, final Chat chat) {




        ImageView imageViewFriendProfilePic = (ImageView) view.findViewById(R.id.image_view_friend_profile_pic);
        Glide.with(imageViewFriendProfilePic.getContext())
                .load(chat.getChatPhotoURL())
                .into(imageViewFriendProfilePic);
        imageViewFriendProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(chat);
            }
        });


        TextView textViewFriendName = (TextView) view.findViewById(R.id.text_view_friend_name);
        textViewFriendName.setText(chat.getChatName());


        TextView textViewFriendEmail = (TextView) view.findViewById(R.id.text_view_friend_email);
        if(chat.getChatType().equals("Personal"))
        {
            textViewFriendEmail.setText(chat.getChatEmail());
            textViewFriendEmail.setVisibility(View.VISIBLE);
        }
        else
            textViewFriendEmail.setVisibility(View.INVISIBLE);

    }

    public void showImageViewDialogFragment(final Chat chat) {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setTitle(Utils.getFirstName(chat.getChatName()));
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewImageDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_image_dialog);
        Glide.with(imageViewImageDialog.getContext())
                .load(chat.getChatPhotoURL())
                .into(imageViewImageDialog);

        imageViewImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                viewImageIntent.putExtra("imageURL", chat.getChatPhotoURL());
                viewImageIntent.putExtra("activityTitle", Utils.getFirstName(chat.getChatName()));
                mActivity.startActivity(viewImageIntent);
                viewImageDialog.dismiss();
            }
        });



        viewImageDialog.show();
    }


}
