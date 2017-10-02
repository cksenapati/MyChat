package com.example.android.mychat.ui.chatDetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by chandan on 08-03-2017.
 */
public class ChatDetailsAdapter extends FirebaseListAdapter<Message> {

    String mEncodedEmail;
    User mCurrentUser;
    Chat mCurrentChat;

    /*ViewGroup mParentView;
    int mPosition;*/

    public ChatDetailsAdapter(Activity activity, Class<Message> modelClass, int modelLayout,
                              Query ref,String encodedEmail,User currentUser,Chat currentChat) {
        super(activity, modelClass, modelLayout, ref);
        this.mEncodedEmail = encodedEmail;
        this.mCurrentUser = currentUser;
        this.mCurrentChat = currentChat;
        this.mActivity = activity;

         }


    @Override
    protected void populateView(final View view, final Message message) {

        String messageSentAt = Utils.SIMPLE_TIME_FORMAT.format(
                new Date(message.getTimestampMessageSentAtLong()));

        LinearLayout linearLayoutSingleMessage = (LinearLayout) view.findViewById(R.id.linear_layout_single_message);
        LinearLayout linearLayoutMessageDetails = (LinearLayout) view.findViewById(R.id.linear_layout_message_details);


        TextView textViewSenderFirstName =  (TextView) view.findViewById(R.id.text_view_sender_first_name);
        TextView textViewMessageText = (TextView) view.findViewById(R.id.text_view_message_text);
        TextView textViewMessageSentAt = (TextView) view.findViewById(R.id.text_view_message_sent_at);
        final TextView textViewMessageStatus = (TextView) view.findViewById(R.id.text_view_message_status);
        ImageView imageViewPhoto = (ImageView) view.findViewById(R.id.image_view_photo);
        ImageView imageViewUploadedAudio = (ImageView) view.findViewById(R.id.image_view_uploaded_audio);
        final VideoView videoViewUploadedVideo = (VideoView)  view.findViewById(R.id.video_view_uploaded_video);
        final ImageButton imageButtonPlay = (ImageButton) view.findViewById(R.id.image_button_play);
        RelativeLayout relativeLayoutVideo = (RelativeLayout) view.findViewById(R.id.relative_layout_video);


        Firebase messageRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS).child(mCurrentChat.chatId).child(message.getMessageId());


        try{
        String visibleTo = message.getVisibleTo();
        if(!visibleTo.contains(mCurrentUser.getEmail()) && !message.getSenderEmail().equals("SystemGeneratedMessage"))
        {
            textViewMessageSentAt.setVisibility(View.GONE);
            textViewMessageStatus.setVisibility(View.GONE);
            textViewSenderFirstName.setVisibility(View.GONE);
            imageViewUploadedAudio.setVisibility(View.GONE);
            relativeLayoutVideo.setVisibility(View.GONE);
            imageViewPhoto.setVisibility(View.GONE);

            view.setVisibility(View.GONE);
            return;
        }
        }
        catch (Exception ex)
        {

        }

        int marginSmallValueInPixels = (int) mActivity.getResources().getDimension(R.dimen.margin_small);
        int marginLargeValueInPixels = (int) mActivity.getResources().getDimension(R.dimen.margin_large);

        textViewMessageSentAt.setText("   "+messageSentAt);


        if(message.getSenderEmail().equals(Utils.decodeEmail(mEncodedEmail)))
        {
            linearLayoutSingleMessage.setPadding(marginLargeValueInPixels, 0, marginSmallValueInPixels, marginSmallValueInPixels);
            linearLayoutSingleMessage.setGravity(Gravity.RIGHT);
            linearLayoutMessageDetails.setGravity(Gravity.RIGHT);

            int colourCode = ContextCompat.getColor(mActivity, R.color.primary_light);
            linearLayoutMessageDetails.setBackgroundColor(Color.parseColor("#BEDDA2"));

            messageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Message currentMessage = dataSnapshot.getValue(Message.class);
                    if (currentMessage != null) {
                        textViewMessageStatus.setVisibility(View.VISIBLE);
                        int statusColourCode;
                        if (currentMessage.getStatus().equals(mActivity.getResources().getString(R.string.message_status_delivered))) {
                            textViewMessageStatus.setText("  \u2713");
                            statusColourCode = ContextCompat.getColor(mActivity, R.color.dark_grey);
                            textViewMessageStatus.setTextColor(statusColourCode);
                        } else if (currentMessage.getStatus().equals(mActivity.getResources().getString(R.string.message_status_seen))) {
                            textViewMessageStatus.setText("  \u2713" + "\u2713");
                            statusColourCode = ContextCompat.getColor(mActivity, R.color.primary_dark);
                            textViewMessageStatus.setTextColor(Color.parseColor("#2591A6"));
                        }
                        else
                        {
                            textViewMessageStatus.setText("  \u2713" + "\u2713");
                            statusColourCode = ContextCompat.getColor(mActivity, R.color.primary_dark);
                            textViewMessageStatus.setTextColor(statusColourCode);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        else if(message.getSenderEmail().equals("SystemGeneratedMessage"))
        {
            linearLayoutSingleMessage.setPadding(marginLargeValueInPixels, 0, marginLargeValueInPixels, marginSmallValueInPixels);
            linearLayoutSingleMessage.setGravity(Gravity.CENTER);
            linearLayoutMessageDetails.setGravity(Gravity.CENTER);

            int colourCode = ContextCompat.getColor(mActivity, R.color.primary);
            linearLayoutMessageDetails.setBackgroundColor(colourCode);

            String newDate = message.getMessageText();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String today = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                    new Date(timestamp.getTime()));

            if(newDate.equals(today))
                textViewMessageText.setText("Today");
            else
               textViewMessageText.setText(newDate);

            textViewMessageSentAt.setVisibility(View.GONE);
            textViewMessageStatus.setVisibility(View.GONE);
            textViewSenderFirstName.setVisibility(View.GONE);
            imageViewUploadedAudio.setVisibility(View.GONE);
            relativeLayoutVideo.setVisibility(View.GONE);
            imageViewPhoto.setVisibility(View.GONE);

            return;
        }
        else
        {
            textViewMessageStatus.setVisibility(View.GONE);

            linearLayoutSingleMessage.setPadding(marginSmallValueInPixels, 0, marginLargeValueInPixels, marginSmallValueInPixels);
            linearLayoutSingleMessage.setGravity(Gravity.LEFT);
            linearLayoutMessageDetails.setGravity(Gravity.LEFT);

            int colourCode = ContextCompat.getColor(mActivity, R.color.tw__solid_white);
            linearLayoutMessageDetails.setBackgroundColor(colourCode);

            if(mCurrentChat.chatType.equals("Personal"))
                message.setStatus(mActivity.getResources().getString(R.string.message_status_seen));
            else if(mCurrentChat.chatType.equals("Group")) {
                if(!message.getStatus().contains(mCurrentUser.getEmail()))
                   message.setStatus(message.getStatus() + "," + mCurrentUser.getEmail());
            }
            messageRef.setValue(message);
        }


        if(mCurrentChat.chatType.equals("Personal")) {
            textViewSenderFirstName.setVisibility(View.GONE);
        }
        else if(mCurrentChat.chatType.equals("Group")) {
            if(message.getSenderEmail().equals(Utils.decodeEmail(mEncodedEmail)))
            {
                textViewSenderFirstName.setVisibility(View.GONE);
            }
            else {
                textViewSenderFirstName.setVisibility(View.VISIBLE);
                textViewSenderFirstName.setText(message.getSentBy());
            }
        }


        boolean hasMediaFile = message.getMediaFileURL() != null;
        if (hasMediaFile) {

            String fileType = message.getMediaFileType();


            if(fileType.equals("videoFile"))
            {
                imageViewUploadedAudio.setVisibility(View.GONE);
                imageViewPhoto.setVisibility(View.GONE);
                relativeLayoutVideo.setVisibility(View.VISIBLE);

                videoViewUploadedVideo.setVideoURI(Uri.parse(message.getMediaFileURL()));
                videoViewUploadedVideo.seekTo(100);
            }
            else if(fileType.equals("audioFile"))
            {
                relativeLayoutVideo.setVisibility(View.GONE);
                imageViewPhoto.setVisibility(View.GONE);
                imageViewUploadedAudio.setVisibility(View.VISIBLE);
            }
            else if(fileType.equals("imageFile"))
            {
                imageViewUploadedAudio.setVisibility(View.GONE);
                relativeLayoutVideo.setVisibility(View.GONE);
                imageViewPhoto.setVisibility(View.VISIBLE);

                Glide.with(imageViewPhoto.getContext())
                        .load(message.getMediaFileURL())
                        .into(imageViewPhoto);
                imageViewPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                        viewImageIntent.putExtra("imageURL", message.getMediaFileURL());
                        viewImageIntent.putExtra("activityTitle", mCurrentChat.getChatName());
                        mActivity.startActivity(viewImageIntent);
                    }
                });
            }


        } else {
            imageViewUploadedAudio.setVisibility(View.GONE);
            relativeLayoutVideo.setVisibility(View.GONE);
            imageViewPhoto.setVisibility(View.GONE);
        }

        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMediaFileURL()));
                intent.setDataAndType(Uri.parse(message.getMediaFileURL()), "video/*");
                mActivity.startActivity(intent);
            }
        });

        videoViewUploadedVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("video", "setOnErrorListener ");
                return true;
            }
        });

        /*imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMediaFileURL()));
                intent.setDataAndType(Uri.parse(message.getMediaFileURL()), "image*//*");
                mActivity.startActivity(intent);
            }
        });*/

        imageViewUploadedAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMediaFileURL()));
                intent.setDataAndType(Uri.parse(message.getMediaFileURL()), "audio/*");
                mActivity.startActivity(intent);
            }
        });

        boolean hasMessageText = message.getMessageText() != null;
        if (hasMessageText) {
            textViewMessageText.setVisibility(View.VISIBLE);
            textViewMessageText.setText(message.getMessageText());
        }
        else
        {
            textViewMessageText.setVisibility(View.GONE);
        }





    }


    public void showActionsDialog(final Message message ) {

        final Dialog actionOnMessageDialog = new Dialog(mActivity);
        actionOnMessageDialog.setContentView(R.layout.dialog_choose_action_on_message);

        final TextView textViewDeleteMessage = (TextView) actionOnMessageDialog.findViewById(R.id.text_view_delete);
        TextView textViewForwardMessage = (TextView) actionOnMessageDialog.findViewById(R.id.text_view_forward);


        textViewDeleteMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMessage(message);
                    actionOnMessageDialog.dismiss();
                }
            });


        textViewForwardMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionOnMessageDialog.dismiss();
            }
        });


        actionOnMessageDialog.show();
    }

    public void deleteMessage(Message message)
    {
        Firebase messageShouldBeVisibleToRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS).child(mCurrentChat.chatId).child(message.getMessageId()).child(Constants.FIREBASE_PROPERTY_VISIBLE_TO);

        String visibleTo = message.getVisibleTo();
        if(visibleTo.contains(mCurrentUser.getEmail())) {
            visibleTo = visibleTo.replace(mCurrentUser.getEmail(), "");
            messageShouldBeVisibleToRef.setValue(visibleTo);
        }

    }

    /*@Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {

        mParentView = viewGroup;
        mPosition = position;

        return super.getView(position, view, viewGroup);
    }*/
}
