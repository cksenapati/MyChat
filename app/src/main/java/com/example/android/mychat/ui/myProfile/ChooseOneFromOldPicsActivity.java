package com.example.android.mychat.ui.myProfile;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ReactableImage;
import com.example.android.mychat.models.User;
import com.example.android.mychat.ui.allChats.AllChatsAdapter;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.android.mychat.ui.allChats.allChatsFragment.mEncodedEmail;

public class ChooseOneFromOldPicsActivity extends AppCompatActivity {


    String mEncodedEmail;
    User mCurrentUser;
    String mActivePushId;
    ArrayList<ReactableImage> mArrayListMyProfilePics;

    MyProfilePicsAdapter mMyProfilePicsAdapter;
    ListView mListViewMyProfilePics;

    Firebase mMyProfilePicsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_one_from_old_pics);

        mEncodedEmail = ((MyChatApplication) this.getApplication()).getEncodedEmail();
        mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        mMyProfilePicsRef.child("activeProfilePicPushId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mActivePushId = dataSnapshot.getValue(String.class);
                    getAllProfilePics();
                }
                else {
                    Intent intentToUpdateProfilePic = new Intent(ChooseOneFromOldPicsActivity.this,UpdateProfilePicActivity.class);
                    startActivity(intentToUpdateProfilePic);
                    finish();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mListViewMyProfilePics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReactableImage selectedImage = mMyProfilePicsAdapter.getItem(position);
                if (selectedImage != null) {

                    if(selectedImage.getPushId().equals(mActivePushId))
                    {
                        Toast.makeText(ChooseOneFromOldPicsActivity.this,"This is your current profilePic.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intentToUpdateProfilePic = new Intent(ChooseOneFromOldPicsActivity.this,UpdateProfilePicActivity.class);
                    intentToUpdateProfilePic.putExtra("pushIdOfThePicToBeUpdated",selectedImage.getPushId());
                    intentToUpdateProfilePic.putExtra("profilePicURL",selectedImage.getPhotoURL());
                    startActivity(intentToUpdateProfilePic);
                    finish();
                }
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
        setTitle("Choose one");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mListViewMyProfilePics = (ListView) findViewById(R.id.list_view_old_profile_pics);
        mArrayListMyProfilePics = new ArrayList<ReactableImage>();


        mMyProfilePicsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child(mEncodedEmail);
    }

    public void getAllProfilePics()
    {
        mMyProfilePicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        try {
                            ReactableImage eachImage = snapshot.getValue(ReactableImage.class);
                            if(eachImage != null)
                                mArrayListMyProfilePics.add(eachImage);
                        }catch (Exception ex){}

                    }

                    if(mArrayListMyProfilePics.size()>0) {
                        mMyProfilePicsAdapter = new MyProfilePicsAdapter(ChooseOneFromOldPicsActivity.this, mArrayListMyProfilePics);
                        mListViewMyProfilePics.setAdapter(mMyProfilePicsAdapter);
                    }
                    else {
                        Intent intentToUpdateProfilePic = new Intent(ChooseOneFromOldPicsActivity.this,UpdateProfilePicActivity.class);
                        startActivity(intentToUpdateProfilePic);
                        finish();

                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
