package com.example.android.mychat.ui.myProfile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ChatDetails;
import com.example.android.mychat.models.ReactableImage;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by chandan on 08-03-2017.
 */
public class MyProfilePicsAdapter extends ArrayAdapter<ReactableImage> {



    public MyProfilePicsAdapter(Activity activity, ArrayList<ReactableImage> allMyPics) {
        super(activity, 0, allMyPics);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReactableImage reactableImage = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_reactable_pic, parent, false);
        }

        ImageView imageViewReactablePic = (ImageView) convertView.findViewById(R.id.image_view_profile_pic);
        Glide.with(imageViewReactablePic.getContext())
                .load(reactableImage.getPhotoURL())
                .into(imageViewReactablePic);


        String picUploadedOn = Utils.SIMPLE_DATE_FORMAT.format(
                new Date(reactableImage.getTimestampUploadedLong()));
        TextView textViewPicUploadedOn = (TextView) convertView.findViewById(R.id.text_view_pic_uploaded_on);
        textViewPicUploadedOn.setText("Uploaded On : "+picUploadedOn);



        TextView textViewNoOfLikes = (TextView) convertView.findViewById(R.id.text_view_no_of_people_liked);
        textViewNoOfLikes.setText(String.valueOf(reactableImage.getNoOfLikes()));

        TextView textViewNoOfLaughs = (TextView) convertView.findViewById(R.id.text_view_no_of_people_laughed);
        textViewNoOfLaughs.setText(String.valueOf(reactableImage.getNoOfLaughs()));

        TextView textViewNoOfLoves = (TextView) convertView.findViewById(R.id.text_view_no_of_people_loved);
        textViewNoOfLoves.setText(String.valueOf(reactableImage.getNoOfLoves()));

        TextView textViewNoOfDislikes = (TextView) convertView.findViewById(R.id.text_view_no_of_people_disliked);
        textViewNoOfDislikes.setText(String.valueOf(reactableImage.getNoOfDislikes()));

        return convertView;

    }

    /*    @Override
    protected void populateView(final View view, final ReactableImage reactableImage) {

        ImageView imageViewReactablePic = (ImageView) view.findViewById(R.id.image_view_profile_pic);
        Glide.with(imageViewReactablePic.getContext())
                .load(reactableImage.getPhotoURL())
                .into(imageViewReactablePic);


        String picUploadedOn = Utils.SIMPLE_DATE_FORMAT.format(
                new Date(reactableImage.getTimestampUploadedLong()));
        TextView textViewPicUploadedOn = (TextView) view.findViewById(R.id.text_view_pic_uploaded_on);
        textViewPicUploadedOn.setText("Uploaded On : "+picUploadedOn);



        TextView textViewNoOfLikes = (TextView) view.findViewById(R.id.text_view_no_of_people_liked);
        textViewNoOfLikes.setText(reactableImage.getNoOfLikes());

        TextView textViewNoOfLaughs = (TextView) view.findViewById(R.id.text_view_no_of_people_laughed);
        textViewNoOfLaughs.setText(reactableImage.getNoOfLaughs());

        TextView textViewNoOfLoves = (TextView) view.findViewById(R.id.text_view_no_of_people_loved);
        textViewNoOfLoves.setText(reactableImage.getNoOfLoves());

        TextView textViewNoOfDislikes = (TextView) view.findViewById(R.id.text_view_no_of_people_disliked);
        textViewNoOfDislikes.setText(reactableImage.getNoOfDislikes());
    }
*/


}
