package com.example.android.mychat.ui.userDetails;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.Message;
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

import java.util.Date;

/**
 * Created by chandan on 08-03-2017.
 */
public class ChatMediaFilesAdapter extends FirebaseListAdapter<Message> {

    Chat mCurrentChat;


    public ChatMediaFilesAdapter(Activity activity, Class<Message> modelClass, int modelLayout,
                                 Query ref, Chat currentChat) {
        super(activity, modelClass, modelLayout, ref);
        this.mCurrentChat = currentChat;
        this.mActivity = activity;
    }


    @Override
    protected void populateView(final View view, final Message message) {


        ImageView imageViewUploadedImage = (ImageView) view.findViewById(R.id.image_view_uploaded_image);
        final VideoView videoViewUploadedVideo = (VideoView)  view.findViewById(R.id.video_view_uploaded_video);


        boolean hasMediaFile = message.getMediaFileURL() != null;
        if (hasMediaFile) {

            String fileType = message.getMediaFileType();


            if(fileType.equals("videoFile"))
            {
                imageViewUploadedImage.setVisibility(View.GONE);
                videoViewUploadedVideo.setVisibility(View.VISIBLE);

                videoViewUploadedVideo.setVideoURI(Uri.parse(message.getMediaFileURL()));
                videoViewUploadedVideo.seekTo(100);
            }

            else if(fileType.equals("imageFile"))
            {
                videoViewUploadedVideo.setVisibility(View.GONE);
                imageViewUploadedImage.setVisibility(View.VISIBLE);

                Glide.with(imageViewUploadedImage.getContext())
                        .load(message.getMediaFileURL())
                        .into(imageViewUploadedImage);
                imageViewUploadedImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                        viewImageIntent.putExtra("imageURL", message.getMediaFileURL());
                        viewImageIntent.putExtra("activityTitle", mCurrentChat.getChatName());
                        mActivity.startActivity(viewImageIntent);
                    }
                });
            }


        }
        else {
            videoViewUploadedVideo.setVisibility(View.GONE);
            imageViewUploadedImage.setVisibility(View.GONE);
        }





    }



}
