package com.example.android.mychat.ui.newGroup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.ViewMediaFile.ViewImageActivity;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

/**
 * Created by chandan on 08-03-2017.
 */

public class GroupParticipantsAdapter extends ArrayAdapter<User> {
    public GroupParticipantsAdapter(Activity activity, ArrayList<User> groupParticipants) {
        super(activity, 0, groupParticipants);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final User participant = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_friend, parent, false);
        }


        ImageView imageViewFriendProfilePic = (ImageView) convertView.findViewById(R.id.image_view_friend_profile_pic);
        Glide.with(imageViewFriendProfilePic.getContext())
                .load(participant.getPhotoURL())
                .into(imageViewFriendProfilePic);
        imageViewFriendProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(participant);
            }
        });


        TextView textViewFriendName = (TextView) convertView.findViewById(R.id.text_view_friend_name);
        textViewFriendName.setText(participant.getName());

        TextView textViewFriendEmail = (TextView) convertView.findViewById(R.id.text_view_friend_email);
        textViewFriendEmail.setText(participant.getEmail());

        return convertView;
    }

    public void showImageViewDialogFragment(final User friend) {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Dialog viewImageDialog = new Dialog(getContext());
        viewImageDialog.setTitle(Utils.getFirstName(friend.getName()));
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewImageDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_image_dialog);
        Glide.with(imageViewImageDialog.getContext())
                .load(friend.getPhotoURL())
                .into(imageViewImageDialog);

        imageViewImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageIntent = new Intent(getContext(), ViewImageActivity.class);
                viewImageIntent.putExtra("imageURL", friend.getPhotoURL());
                viewImageIntent.putExtra("activityTitle", Utils.getFirstName(friend.getName()));
                getContext().startActivity(viewImageIntent);
                viewImageDialog.dismiss();
            }
        });



        viewImageDialog.show();
    }
}

/*

public class GroupParticipantsAdapter extends FirebaseListAdapter<User> {

    ArrayList<User> mArrayListGroupParticipants;

    */
/**
     * Public constructor that initializes private instance variables when adapter is created
     *//*

    public GroupParticipantsAdapter(Activity activity, Class<User> modelClass, int modelLayout,
                                    Query ref,ArrayList<User> groupParticipants) {
        super(activity, modelClass, modelLayout, ref);
        this.mArrayListGroupParticipants = groupParticipants;
        this.mActivity = activity;
    }


    */
/**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     *//*

    @Override
    protected void populateView(final View view, final User friend) {

        if(!mArrayListGroupParticipants.contains(friend))
        {
            view.setVisibility(View.GONE);
            return;
        }

        view.setVisibility(View.VISIBLE);
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


}
*/
