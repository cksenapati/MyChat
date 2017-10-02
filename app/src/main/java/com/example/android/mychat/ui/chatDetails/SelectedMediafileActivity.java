package com.example.android.mychat.ui.chatDetails;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import android.widget.MediaController;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.R.id.message;

public class SelectedMediafileActivity extends AppCompatActivity {

    ImageView mImageViewSelectedImage;
    VideoView mVideoViewSelectedVideo;
    ImageButton mImageButtonAudioPlay;
    ImageButton mImageButtonVideoPlay;
    RelativeLayout mRelativeLayoutAudio;
    RelativeLayout mRelativeLayoutVideo;
    EditText mEditTextUserTextInputs;
    Button mButtonSend;
    ProgressBar mProgressBar;

    String mMediaFileType;
    String mMediaFileLocation;

    Intent data;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_mediafile);

        initializeScreen();

        final Intent intent = getIntent();
        if(intent != null)
        {
            mMediaFileType = intent.getStringExtra("mediaFileType");
            //mMediaFileLocation = intent.getStringExtra("mediaFileLocation");

            final Application myChatApplication = this.getApplication();
            data =  ((MyChatApplication) myChatApplication).getIntentSelectedMediaFile();


            Uri selectedImageUri = data.getData();
            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference selectedPhotoRef = mFirebaseStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            UploadTask uploadTask = selectedPhotoRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // When the image has successfully uploaded, we get its download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mMediaFileLocation = downloadUrl.toString();
                    displaySelectedFile();
                }
            });


        }

        //initializeScreen();

        //displaySelectedFile();

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageTest;
                try {
                     messageTest = mEditTextUserTextInputs.getText().toString();
                     if(messageTest.equals(""))
                         messageTest = null;

                }catch (Exception ex)
                {
                    messageTest = null;
                }

                // Clear input box
                mEditTextUserTextInputs.setText("");

                Intent intentToChatDetails = new Intent(SelectedMediafileActivity.this,ChatDetailsActivity.class);
                intentToChatDetails.putExtra("messageText",messageTest);
                intentToChatDetails.putExtra("mediaFileLocation",mMediaFileLocation);
                intentToChatDetails.putExtra("mediaFileType",mMediaFileType);
                setResult(Activity.RESULT_OK,intentToChatDetails);
                finish();
            }
        });

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mImageViewSelectedImage = (ImageView) findViewById(R.id.image_view_selected_image);
        mVideoViewSelectedVideo = (VideoView) findViewById(R.id.video_view_selected_video);
        mImageButtonAudioPlay = (ImageButton) findViewById(R.id.image_button_audio_play);
        mImageButtonVideoPlay = (ImageButton) findViewById(R.id.image_button_video_play);
        mRelativeLayoutAudio = (RelativeLayout) findViewById(R.id.relative_layout_audio);
        mRelativeLayoutVideo = (RelativeLayout) findViewById(R.id.relative_layout_video);
        mEditTextUserTextInputs = (EditText) findViewById(R.id.edit_text_write_message);
        mButtonSend = (Button) findViewById(R.id.button_send_message);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_image_loading);


        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("mediaFiles");

    }

    public  void displaySelectedFile()
    {
        if(mMediaFileType.equals("imageFile"))
        {
            mImageViewSelectedImage.setVisibility(View.VISIBLE);
            mRelativeLayoutAudio.setVisibility(View.GONE);
            mRelativeLayoutVideo.setVisibility(View.GONE);

            Glide.with(mImageViewSelectedImage.getContext())
                    .load(mMediaFileLocation)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mImageViewSelectedImage);

        }
        else if(mMediaFileType.equals("videoFile"))
        {
            mImageViewSelectedImage.setVisibility(View.GONE );
            mRelativeLayoutAudio.setVisibility(View.GONE);
            mRelativeLayoutVideo.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            try {
                // Start the MediaController
                MediaController mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(mVideoViewSelectedVideo);
                // Get the URL from String VideoURL
                Uri video = Uri.parse(mMediaFileLocation);
                mVideoViewSelectedVideo.setMediaController(mediacontroller);
                mVideoViewSelectedVideo.setVideoURI(video);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            mImageButtonVideoPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mVideoViewSelectedVideo.isPlaying())
                    {
                        mVideoViewSelectedVideo.pause();
                    }
                    else {
                        mVideoViewSelectedVideo.requestFocus();
                        mVideoViewSelectedVideo.start();
                    }
                }
            });

        }
        else if(mMediaFileType.equals("audioFile"))
        {
            mImageViewSelectedImage.setVisibility(View.GONE );
            mRelativeLayoutAudio.setVisibility(View.VISIBLE);
            mRelativeLayoutVideo.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            mImageButtonAudioPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMediaFileLocation));
                    intent.setDataAndType(Uri.parse(mMediaFileLocation), "audio/*");
                    startActivity(intent);
                }
            });
        }
    }

}
