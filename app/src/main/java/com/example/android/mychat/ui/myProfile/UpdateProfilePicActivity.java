package com.example.android.mychat.ui.myProfile;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.ReactableImage;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfilePicActivity extends AppCompatActivity {

    private ProgressDialog mAuthProgressDialog;

    ImageView mImageViewShowImage;
    private static final int RC_PHOTO_PICKER =  1;
    String mImageURL;
    User mCurrentUser;
    ReactableImage mSelectedImage;
    String mEncodedEmail;
    String mPersonalOrGroup;
    String mActiveImagePushId;
    String mSelectedImagePushId;
    int mTotalNoOfProfilePics;

    public Firebase mCurrentUserProfilePicURLRef;
    public Firebase mCurrentUserRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);


        mEncodedEmail = ((MyChatApplication) this.getApplication()).getEncodedEmail();
        mCurrentUser = ((MyChatApplication) this.getApplication()).getCurrentUser();

        initializeScreen();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_loading);
        final Application myChatApplication = this.getApplication();

        mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                if (mCurrentUser != null) {

                    //Save updated current user at application level
                    ((MyChatApplication) myChatApplication).setCurrentUser(mCurrentUser);

                    mImageURL = mCurrentUser.getPhotoURL();
                    Glide.with(mImageViewShowImage.getContext())
                            .load(mImageURL)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(mImageViewShowImage);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.getStringExtra("pushIdOfThePicToBeUpdated") != null) {
                String pushIdOfThePicToBeUpdated = intent.getStringExtra("pushIdOfThePicToBeUpdated");
                String profilePicURL = intent.getStringExtra("profilePicURL");

                if (pushIdOfThePicToBeUpdated.equals(""))
                    mSelectedImagePushId = null;
                else {
                    mSelectedImagePushId = pushIdOfThePicToBeUpdated;
                    updateProfilePic(profilePicURL);
                    /*Glide.with(mImageViewShowImage.getContext())
                            .load(profilePicURL)
                            .into(mImageViewShowImage);*/
                }
            }
        }




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_viewimage, menu);


        MenuItem editProfilePic = menu.findItem(R.id.action_edit_list_name);


        editProfilePic.setVisible(true);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_edit_list_name :
                showUploadOrUpdateOptions();
                return true;


            default: return super.onOptionsItemSelected(item);
        }

    }

    public void showUploadOrUpdateOptions()
    {
        final Dialog showPickOptionsDialog = new Dialog(this);
        showPickOptionsDialog.setTitle("Choose your action");
        showPickOptionsDialog.setContentView(R.layout.dialog_pic_upload_or_update);

        ImageButton imageButtonPickNewPic = (ImageButton) showPickOptionsDialog.findViewById(R.id.image_button_pick_new_pic);
        ImageButton imageButtonPickFromOldPics = (ImageButton) showPickOptionsDialog.findViewById(R.id.image_button_pic_from_old_pics);

        imageButtonPickNewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                imagePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(imagePickerIntent, "Complete action using"), RC_PHOTO_PICKER);

                showPickOptionsDialog.dismiss();

            }
        });


        imageButtonPickFromOldPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToChooseOneFromOldPics = new Intent(UpdateProfilePicActivity.this,ChooseOneFromOldPicsActivity.class);
                startActivity(intentToChooseOneFromOldPics);
                showPickOptionsDialog.dismiss();

            }
        });

        showPickOptionsDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER)
        {
            if(resultCode == RESULT_OK){

                Uri selectedImageUri = data.getData();


                StorageReference selectedPhotoRef = mFirebaseStorageReference.child(selectedImageUri.getLastPathSegment());
                UploadTask uploadTask = selectedPhotoRef.putFile(selectedImageUri);
                uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        updateProfilePic(downloadUrl.toString());

                    }
                });
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Unable to pick this file
                Toast.makeText(this, "Unable to pick this image file", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }

    }

    public void initializeScreen() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        setTitle("ProfilePic");
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUserRef =  new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(mEncodedEmail);
        mCurrentUserProfilePicURLRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(mEncodedEmail).child("photoURL");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("profilePics");

        mImageViewShowImage = (ImageView) findViewById(R.id.image_view_show_full_image);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);

        mAuthProgressDialog.show();
        mAuthProgressDialog.dismiss();

    }

    public void updateProfilePic(String downloadableProfilePicURL)
    {
        /*final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_loading);
        progressBar.setVisibility(View.VISIBLE);
*/
        mCurrentUserProfilePicURLRef.setValue(downloadableProfilePicURL);

        //Update the reactabeImages according to selected old image or upload of new image
        getActiveImagePushId(downloadableProfilePicURL);

        updateProfilePicAtUserFriends(downloadableProfilePicURL);
        updateProfilePicAtMyChats(downloadableProfilePicURL);
        updateProfilePicAtGroupDetails(downloadableProfilePicURL);
        updateProfilePicAtPendingRequest(downloadableProfilePicURL);
        updateProfilePicAtSentRequest(downloadableProfilePicURL);

        /*Glide.with(mImageViewShowImage.getContext())
                .load(downloadableProfilePicURL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mImageViewShowImage);*/
    }

    public void updateProfilePicAtUserFriends(final String downloadableProfilePicURL)
    {
        final Firebase userFriendsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_FRIENDS);
        Firebase myFriends = userFriendsRef.child(mEncodedEmail);
        myFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    return;
                }

                for (Map.Entry<String, Object> entry : ((Map<String, Object>) dataSnapshot.getValue()).entrySet()) {

                    //Get single friend
                    Map singleFriend = (Map) entry.getValue();

                    //Get email field
                    String friendEmil = singleFriend.get("email").toString();

                    Firebase myProfilePicInFriendListOfMyFriend = userFriendsRef.child(Utils.encodeEmail(friendEmil)).child(mEncodedEmail).child("photoURL");
                    myProfilePicInFriendListOfMyFriend.setValue(downloadableProfilePicURL);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void updateProfilePicAtMyChats(final String downloadableProfilePicURL)
    {
        final Firebase myChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
        Firebase myChats = myChatsRef.child(mEncodedEmail);
        myChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat eachChat = snapshot.getValue(Chat.class);

                    if(eachChat.getChatType().equals("Personal"))
                    {
                        Firebase meInFriendChatListRef = myChatsRef.child(Utils.encodeEmail(eachChat.getChatEmail())).child(mEncodedEmail).child("chatPhotoURL");
                        meInFriendChatListRef.setValue(downloadableProfilePicURL);
                        if(mSelectedImagePushId == null)
                        {
                            Firebase friendReactionToMyProfilePicRef = myChatsRef.child(Utils.encodeEmail(eachChat.getChatEmail())).child(mEncodedEmail).child("reactionToProfilePic");
                            friendReactionToMyProfilePicRef.setValue(null);
                        }
                        else
                          resetReactionToMyProfilePic(eachChat);
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public  void resetReactionToMyProfilePic(final Chat chat)
    {
         Firebase myChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
        final Firebase friendReactionToMyProfilePicRef = myChatsRef.child(Utils.encodeEmail(chat.getChatEmail())).child(mEncodedEmail).child("reactionToProfilePic");


        Firebase activeImageRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child(mEncodedEmail).child(mSelectedImagePushId);
        activeImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    ReactableImage activeimage = dataSnapshot.getValue(ReactableImage.class);

                    if(activeimage.getWhoLiked() != null && activeimage.getWhoLiked().contains(chat.getChatEmail()))
                        friendReactionToMyProfilePicRef.setValue(Constants.USER_REACTIONS_LIKE);
                    else if(activeimage.getWhoLaughed() != null && activeimage.getWhoLaughed().contains(chat.getChatEmail()))
                        friendReactionToMyProfilePicRef.setValue(Constants.USER_REACTIONS_LAUGH);
                    else if(activeimage.getWhoLoved() != null && activeimage.getWhoLoved().contains(chat.getChatEmail()))
                        friendReactionToMyProfilePicRef.setValue(Constants.USER_REACTIONS_LOVE);
                    else if(activeimage.getWhoDisliked() != null && activeimage.getWhoDisliked().contains(chat.getChatEmail()))
                        friendReactionToMyProfilePicRef.setValue(Constants.USER_REACTIONS_DISLIKE);
                    else
                        friendReactionToMyProfilePicRef.setValue(null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    public void updateProfilePicAtPendingRequest(final String downloadableProfilePicURL)
    {
        final Firebase pendingRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS);
        final Firebase sentRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS);
        Firebase mySentRequests = sentRequestsRef.child(mEncodedEmail);
        mySentRequests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    return;
                }

                for (Map.Entry<String, Object> entry : ((Map<String,Object>) dataSnapshot.getValue()).entrySet()){

                    //Get single friend
                    Map requestedUser = (Map) entry.getValue();

                    //Get email field
                    String requestedUserEmail = requestedUser.get("email").toString();

                    Firebase myProfilePicInPendingRequestList = pendingRequestsRef.child(Utils.encodeEmail(requestedUserEmail)).child(mEncodedEmail).child("photoURL");
                    myProfilePicInPendingRequestList.setValue(downloadableProfilePicURL);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void updateProfilePicAtSentRequest(final String downloadableProfilePicURL)
    {
        final Firebase sentRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_SENT_REQUESTS);

        final Firebase pendingRequestsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_USER_PENDING_REQUESTS);
        Firebase myPendingRequests = pendingRequestsRef.child(mEncodedEmail);
        myPendingRequests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    return;
                }
                for (Map.Entry<String, Object> entry : ((Map<String,Object>) dataSnapshot.getValue()).entrySet()){

                    //Get single friend
                    Map requestPendingFromUser = (Map) entry.getValue();

                    //Get email field
                    String requestPendingFromUserEmail = requestPendingFromUser.get("email").toString();

                    Firebase myProfilePicInSentRequestList = sentRequestsRef.child(Utils.encodeEmail(requestPendingFromUserEmail)).child(mEncodedEmail).child("photoURL");
                    myProfilePicInSentRequestList.setValue(downloadableProfilePicURL);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public void getActiveImagePushId(final String downloadableProfilePicURL)
    {

        final Firebase friendsReactableImagesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child( Utils.encodeEmail(mCurrentUser.getEmail()));

        Firebase friendsReactableActiveImagePushIdRef = friendsReactableImagesRef.child("activeProfilePicPushId");
        friendsReactableActiveImagePushIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists())
                {
                    mActiveImagePushId = dataSnapshot.getValue(String.class);
                    if(mSelectedImagePushId == null) {
                        updateActiveImageAtReactableImages(downloadableProfilePicURL);
                    }
                    else {
                        friendsReactableImagesRef.child("activeProfilePicPushId").setValue(mSelectedImagePushId);
                    }
                }
                else
                {

                    HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
                    timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    mActiveImagePushId = friendsReactableImagesRef.push().getKey();
                    final ReactableImage profilePic = new ReactableImage(mActiveImagePushId,"profilePic_1",downloadableProfilePicURL,0,0,0,0,timestampUploaded);

                    friendsReactableImagesRef.child("totalNoOfProfilePics").setValue(1);
                    friendsReactableImagesRef.child("activeProfilePicPushId").setValue(mActiveImagePushId);
                    friendsReactableImagesRef.child(mActiveImagePushId).setValue(profilePic);
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void updateActiveImageAtReactableImages(final String downloadableProfilePicURL)
    {
        final Firebase myReactableImagesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child( Utils.encodeEmail(mCurrentUser.getEmail()));

        Firebase myTotalNoOfReactabeImagesRef = myReactableImagesRef.child("totalNoOfProfilePics");
        myTotalNoOfReactabeImagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    mTotalNoOfProfilePics = dataSnapshot.getValue(Integer.class);

                    HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
                    timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    mActiveImagePushId = myReactableImagesRef.push().getKey();
                    final ReactableImage profilePic = new ReactableImage(mActiveImagePushId,"profilePic_"+(mTotalNoOfProfilePics+1),downloadableProfilePicURL,0,0,0,0,timestampUploaded);

                    myReactableImagesRef.child("totalNoOfProfilePics").setValue(mTotalNoOfProfilePics+1);
                    myReactableImagesRef.child("activeProfilePicPushId").setValue(mActiveImagePushId);
                    myReactableImagesRef.child(mActiveImagePushId).setValue(profilePic);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    public void updateProfilePicAtGroupDetails(final String downloadableProfilePicURL)
    {
        final Firebase allMyChatsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS);
        Firebase myChatsRef = allMyChatsRef.child(mEncodedEmail);
        myChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    return;
                }

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Chat eachChat = snapshot.getValue(Chat.class);
                    if(eachChat.getChatType().equals("Group"))
                    {
                        Firebase myProfilePicAsParticipantRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_GROUP_DETAILS).child(eachChat.chatEmail).child(Constants.FIREBASE_PROPERTY_PARTICIPANTS).child(mEncodedEmail).child("photoURL");
                        myProfilePicAsParticipantRef.setValue(downloadableProfilePicURL);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}

