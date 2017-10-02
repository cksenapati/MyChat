package com.example.android.mychat.ui.chatDetails;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.Message;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MessageDetailsActivity extends AppCompatActivity {

    int count;

    Message mMessage;
    String mChatId;
    String mChatType;
    User mCurrentUser;
    Chat mCurrentChat;

    TextView mTextViewMessageText;
    ImageView mImageViewAudio;
    ImageView mImageViewUploadedImage;
    RelativeLayout mRelativeLayoutVideo;
    VideoView mVideoViewUploadedVideo;
    ListView mListViewSeenBy;

    Firebase mMessageReference;

    ArrayList<String> mArrayListSeenByEmails;
    ArrayList<User> mArrayListSeenByUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        Intent intent = getIntent();
        if(intent != null)
        {
            mMessage = (Message) intent.getSerializableExtra("message");
        }

        mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();
        mCurrentChat = ((MyChatApplication) this.getApplication()).getCurrentChat();
        mChatId = mCurrentChat.getChatId();

        initializeScreen();

        if(mMessage.getMessageText() != null)
        {
            mTextViewMessageText.setVisibility(View.VISIBLE);
            mTextViewMessageText.setText(mMessage.getMessageText());
        }
        else if(mMessage.getMediaFileType().equals("videoFile"))
        {
            mRelativeLayoutVideo.setVisibility(View.VISIBLE);
            mVideoViewUploadedVideo.setVideoURI(Uri.parse(mMessage.getMediaFileURL()));
            mVideoViewUploadedVideo.seekTo(100);
        }
        else if(mMessage.getMediaFileType().equals("imageFile"))
        {
            mImageViewUploadedImage.setVisibility(View.VISIBLE);
            Glide.with(mImageViewUploadedImage.getContext())
                    .load(mMessage.getMediaFileURL())
                    .into(mImageViewUploadedImage);
        }
        else if(mMessage.getMediaFileType().equals("audioFile"))
        {
            mImageViewAudio.setVisibility(View.VISIBLE);
        }

        mMessageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   mMessage = dataSnapshot.getValue(Message.class);

                   if(mMessage != null)
                   {
                       mArrayListSeenByEmails.clear();

                       if(mCurrentChat.getChatType().equals("Personal"))
                       {

                           if(mMessage.getStatus().equals("Seen"))
                           {
                               String user1 = mCurrentUser.getEmail();
                               String user2 = Utils.decodeEmail( mCurrentChat.getChatEmail());
                               mArrayListSeenByEmails.add(user1);
                               mArrayListSeenByEmails.add(user2);
                           }
                           else {
                               String sender = mMessage.getSenderEmail();
                               mArrayListSeenByEmails.add(sender);
                           }
                       }
                       else if (mCurrentChat.getChatType().equals("Group"))
                       {
                           String[] user = mMessage.getStatus().split(",");
                           for (int i=0; i< user.length; i++)
                           {
                               if(user[i].contains("@"))
                                   mArrayListSeenByEmails.add(user[i]);
                           }
                       }

                       getUsers();

                   }
               }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }


    public void getUsers()
    {
        mArrayListSeenByUsers.clear();

        Firebase AllUsers = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS);
        for(count = 0; count< mArrayListSeenByEmails.size(); count++)
        {
            Firebase seenBy = AllUsers.child(Utils.encodeEmail(mArrayListSeenByEmails.get(count)));
            seenBy.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {


                        User seenBy = dataSnapshot.getValue(User.class);
                        if(seenBy != null)
                            mArrayListSeenByUsers.add(seenBy);
                    }

                    if(count == mArrayListSeenByEmails.size())
                        updateListview();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

    }

    public void updateListview()
    {

        SeenByAdapter adapter = new SeenByAdapter(this, mArrayListSeenByUsers);
        mListViewSeenBy.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Message info");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextViewMessageText = (TextView) findViewById(R.id.text_view_message);
        mImageViewAudio = (ImageView) findViewById(R.id.image_view_uploaded_audio);
        mImageViewUploadedImage = (ImageView) findViewById(R.id.image_view_photo);
         mRelativeLayoutVideo = (RelativeLayout) findViewById(R.id.relative_layout_video);
        mVideoViewUploadedVideo = (VideoView) findViewById(R.id.video_view_uploaded_video);
        mListViewSeenBy = (ListView)findViewById(R.id.list_view_seen_by);

        mArrayListSeenByEmails = new ArrayList<String> ();
        mArrayListSeenByUsers = new ArrayList<User>();

        mMessageReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_CHATS).child(mChatId).child(mMessage.getMessageId());


    }

}
