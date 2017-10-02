package com.example.android.mychat.ui.myProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {

    ImageView mImageViewMyProfilePic;
    TextView mTextViewMyFullName;
    TextView mTextViewMyEmail;

    String mMyEmail;
    String mMyFullName;
    String mMyProfilePicURL;
    String mEncodedEmail;

    User mCurrentUser;

    public Firebase mCurrentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setTitle("My Profile");



        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail = intent.getStringExtra("encodedEmail");

        }

        initializeScreen();

        mImageViewMyProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyProfilePicURL != null) {
                    Intent viewImageIntent = new Intent(MyProfileActivity.this, UpdateProfilePicActivity.class);

                    startActivity(viewImageIntent);
                }

            }
        });

        mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                if (mCurrentUser != null) {

                    mMyProfilePicURL = mCurrentUser.getPhotoURL();
                    mMyEmail = mCurrentUser.getEmail();
                    mMyFullName = mCurrentUser.getName();

                    Glide.with(mImageViewMyProfilePic.getContext())
                            .load(mMyProfilePicURL)
                            .into(mImageViewMyProfilePic);

                    mTextViewMyEmail.setText("Email : " + mMyEmail);
                    mTextViewMyFullName.setText("Name : " + mMyFullName);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



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

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("MyProfile");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mImageViewMyProfilePic = (ImageView) findViewById(R.id.image_view_my_profile_pic);
        mTextViewMyEmail = (TextView) findViewById(R.id.text_view_my_email);
        mTextViewMyFullName = (TextView) findViewById(R.id.text_view_my_full_name);

        mCurrentUserRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(mEncodedEmail);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.makeMeOnOrOffline(mEncodedEmail, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.makeMeOnOrOffline(mEncodedEmail, false);

    }



}
