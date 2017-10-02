package com.example.android.mychat.ui.allChats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by chandan on 08-03-2017.
 */
public class AllChatsAdapter extends ArrayAdapter<Chat> {

    Activity mActivity;

    String mEncodedEmail;
    User mCurrentUser;
    String mActiveImagePushId;
    ReactableImage mActiveImage;
    String updatedReaction;

    Firebase friendsReactableActiveImageRef;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public AllChatsAdapter(Activity activity, ArrayList<Chat> myChats) {
        super(activity, 0, myChats);;

        /*this.mEncodedEmail = encodedEmail;
        this.mCurrentUser = currentUser;*/

        this.mEncodedEmail = ((MyChatApplication) activity.getApplication()).getEncodedEmail();
        this.mCurrentUser =  ((MyChatApplication) activity.getApplication()).getCurrentUser();

        this.mActivity = activity;
    }


    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        // Get the data item for this position
        final Chat singleChat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_single_chat, parent, false);
        }


        ImageView imageViewChatProfilePic = (ImageView) convertView.findViewById(R.id.image_view_chat_profile_pic);
        Glide.with(imageViewChatProfilePic.getContext())
                .load(singleChat.getChatPhotoURL())
                .into(imageViewChatProfilePic);
        imageViewChatProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(singleChat);
            }
        });


        TextView textViewChatName = (TextView) convertView.findViewById(R.id.chat_name_text_view);
        textViewChatName.setText(singleChat.getChatName());

        final TextView textViewChatLastMessage = (TextView) convertView.findViewById(R.id.chat_last_message_text_view);
        final TextView textViewChatLastMessageReceiveTime = (TextView) convertView.findViewById(R.id.chat_last_message_receive_time);
        final TextView textViewChatNoOfNewMessages = (TextView) convertView.findViewById(R.id.no_of_new_messages_text_view);

        Firebase currentChatDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(singleChat.chatId);

        currentChatDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatDetails chatDetails =  dataSnapshot.getValue(ChatDetails.class);
                if(chatDetails != null)
                {
                    if(chatDetails.getTimestampLastUpdateLong() != singleChat.getTimestampLastUpdateLong())
                    {
                        Firebase currentChat;
                        if(singleChat.chatType.equals("Personal"))
                            currentChat = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(Utils.encodeEmail(singleChat.chatEmail));
                        else
                            currentChat = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(singleChat.chatEmail);

                        currentChat.child(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE).setValue(chatDetails.getTimestampLastUpdate());
                        return;
                    }
                    if(chatDetails.getTotalNoOfMessagesAvailable() > singleChat.getNoOfMessagesIHaveSeen())
                    {
                        int noOfNewMessages = chatDetails.getTotalNoOfMessagesAvailable() - singleChat.getNoOfMessagesIHaveSeen();
                        textViewChatNoOfNewMessages.setVisibility(View.VISIBLE);
                        textViewChatNoOfNewMessages.setText(String.valueOf(noOfNewMessages));
                    }
                    else
                    {
                        textViewChatNoOfNewMessages.setVisibility(View.INVISIBLE);
                    }

                    String lastMessage = chatDetails.getLastMessage();
                    if(lastMessage.length() > 10)
                        textViewChatLastMessage.setText(lastMessage.substring(0, Math.min(lastMessage.length(), 10))+"...");
                    else
                        textViewChatLastMessage.setText(lastMessage);

                    String lastMessageUpdateTime = Utils.SIMPLE_DATE_FORMAT.format(
                            new Date(chatDetails.getTimestampLastUpdateLong()));
                    String lastMessageUpdateDay = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                            new Date(chatDetails.getTimestampLastUpdateLong()));
                    String lastMessageUpdateOnlyTime = Utils.SIMPLE_TIME_FORMAT.format(
                            new Date(chatDetails.getTimestampLastUpdateLong()));

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String today = Utils.SIMPLE_DATE_ONLY_FORMAT.format(
                            new Date(timestamp.getTime()));

                    if(lastMessageUpdateDay.equals(today))
                        textViewChatLastMessageReceiveTime.setText(lastMessageUpdateOnlyTime);
                    else
                        textViewChatLastMessageReceiveTime.setText(lastMessageUpdateTime);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        return convertView;
    }

   /* @Override
    protected void populateView(final View view, final Chat chat) {

        ImageView imageViewChatProfilePic = (ImageView) view.findViewById(R.id.image_view_chat_profile_pic);
        Glide.with(imageViewChatProfilePic.getContext())
                .load(chat.getChatPhotoURL())
                .into(imageViewChatProfilePic);
        imageViewChatProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageViewDialogFragment(chat);
            }
        });


        TextView textViewChatName = (TextView) view.findViewById(R.id.chat_name_text_view);
        textViewChatName.setText(chat.getChatName());

       final TextView textViewChatLastMessage = (TextView) view.findViewById(R.id.chat_last_message_text_view);
       final TextView textViewChatLastMessageReceiveTime = (TextView) view.findViewById(R.id.chat_last_message_receive_time);
        final TextView textViewChatNoOfNewMessages = (TextView) view.findViewById(R.id.no_of_new_messages_text_view);

        Firebase currentChatDetailsRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_CHAT_DETAILS).child(chat.chatId);

        currentChatDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatDetails chatDetails =  dataSnapshot.getValue(ChatDetails.class);
                if(chatDetails != null)
                {
                    if(chatDetails.getTimestampLastUpdateLong() != chat.getTimestampLastUpdateLong())
                    {
                        Firebase currentChat;
                        if(chat.chatType.equals("Personal"))
                           currentChat = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(Utils.encodeEmail(chat.chatEmail));
                        else
                            currentChat = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(chat.chatEmail);

                        currentChat.child(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE).setValue(chatDetails.getTimestampLastUpdate());
                        return;
                    }
                    if(chatDetails.getTotalNoOfMessagesAvailable() > chat.getNoOfMessagesIHaveSeen())
                    {
                        int noOfNewMessages = chatDetails.getTotalNoOfMessagesAvailable() - chat.getNoOfMessagesIHaveSeen();
                        textViewChatNoOfNewMessages.setVisibility(View.VISIBLE);
                        textViewChatNoOfNewMessages.setText(String.valueOf(noOfNewMessages));
                    }
                    else
                    {
                        textViewChatNoOfNewMessages.setVisibility(View.INVISIBLE);
                    }

                    String lastMessage = chatDetails.getLastMessage();
                    if(lastMessage.length() > 10)
                        textViewChatLastMessage.setText(lastMessage.substring(0, Math.min(lastMessage.length(), 10))+"...");
                    else
                        textViewChatLastMessage.setText(lastMessage);

                    String lastMessageUpdateTime = Utils.SIMPLE_DATE_FORMAT.format(
                            new Date(chatDetails.getTimestampLastUpdateLong()));
                    textViewChatLastMessageReceiveTime.setText(lastMessageUpdateTime);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }
*/
    public void showImageViewDialogFragment(final Chat chat) {
        //ChooseMediaFileDialogFragment chooseMediaFileDialogFragment = new ChooseMediaFileDialogFragment();
        // Show Alert DialogFragment
        //chooseMediaFileDialogFragment.show(getFragmentManager(), "Choose action option");

        final Firebase currentChat = new Firebase(Constants.FIREBASE_URL_MY_CHAT_MY_CHATS).child(mEncodedEmail).child(Utils.encodeEmail(chat.getChatEmail()));
        updatedReaction = chat.getReactionToProfilePic();



        final Dialog viewImageDialog = new Dialog(mActivity);
        viewImageDialog.setTitle(chat.getChatName());
        viewImageDialog.setContentView(R.layout.dialog_view_image);

        ImageView imageViewImageDialog = (ImageView) viewImageDialog.findViewById(R.id.image_view_image_dialog);
        LinearLayout linearLayoutReactions = (LinearLayout) viewImageDialog.findViewById(R.id.linear_layout_reactions);
        ImageView imageViewLikeImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_like);
        ImageView imageViewLoveImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_love);
        ImageView imageViewLaughImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_laugh);
        ImageView imageViewDislikeImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_dislike);



        if(chat.getChatType().equals("Personal"))
        {
            linearLayoutReactions.setVisibility(View.VISIBLE);

            getActiveImagePushId(chat);




            imageViewLikeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updatedReaction == null) {
                        updatedReaction = Constants.USER_REACTIONS_LIKE;
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LIKE);
                        mActiveImage.setNoOfLikes(mActiveImage.getNoOfLikes() + 1);
                        mActiveImage.setWhoLiked(mCurrentUser.getEmail()+",");
                        updateTheReactedImage(Constants.USER_REACTIONS_LIKE,viewImageDialog);

                    }
                    else if(updatedReaction.equals(Constants.USER_REACTIONS_LIKE)) {
                        updatedReaction = null;
                        chat.setReactionToProfilePic(null);
                        mActiveImage.setNoOfLikes(mActiveImage.getNoOfLikes() - 1);
                        mActiveImage.setWhoLiked(mActiveImage.getWhoLiked().replace(mCurrentUser.getEmail()+",",""));
                        updateTheReactedImage(null,viewImageDialog);

                    }
                    else
                    {
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LIKE);
                        updateTheReactionCount(updatedReaction,Constants.USER_REACTIONS_LIKE);
                        updateTheReactedImage(Constants.USER_REACTIONS_LIKE,viewImageDialog);

                    }

                     currentChat.setValue(chat);
                     friendsReactableActiveImageRef.setValue(mActiveImage);

                }
            });

            imageViewLoveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updatedReaction == null) {
                        updatedReaction = Constants.USER_REACTIONS_LOVE;
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LOVE);
                        mActiveImage.setNoOfLoves(mActiveImage.getNoOfLoves() + 1);
                        mActiveImage.setWhoLoved(mCurrentUser.getEmail()+",");
                        updateTheReactedImage(Constants.USER_REACTIONS_LOVE,viewImageDialog);

                    }
                    else if(updatedReaction.equals(Constants.USER_REACTIONS_LOVE)) {
                        updatedReaction = null;
                        chat.setReactionToProfilePic(null);
                        mActiveImage.setNoOfLoves(mActiveImage.getNoOfLoves() - 1);
                        mActiveImage.setWhoLoved(mActiveImage.getWhoLoved().replace(mCurrentUser.getEmail()+",",""));
                        updateTheReactedImage(null,viewImageDialog);

                    }
                    else
                    {
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LOVE);
                        updateTheReactionCount(updatedReaction,Constants.USER_REACTIONS_LOVE);
                        updateTheReactedImage(Constants.USER_REACTIONS_LOVE,viewImageDialog);

                    }

                    currentChat.setValue(chat);
                    friendsReactableActiveImageRef.setValue(mActiveImage);

                }
            });

            imageViewLaughImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updatedReaction == null) {
                        updatedReaction = Constants.USER_REACTIONS_LAUGH;
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LAUGH);
                        mActiveImage.setNoOfLaughs(mActiveImage.getNoOfLaughs() + 1);
                        mActiveImage.setWhoLaughed(mCurrentUser.getEmail()+",");
                        updateTheReactedImage(Constants.USER_REACTIONS_LAUGH,viewImageDialog);

                    }
                    else if(updatedReaction.equals(Constants.USER_REACTIONS_LAUGH)) {
                        updatedReaction = null;
                        chat.setReactionToProfilePic(null);
                        mActiveImage.setNoOfLaughs(mActiveImage.getNoOfLaughs() - 1);
                        mActiveImage.setWhoLaughed(mActiveImage.getWhoLaughed().replace(mCurrentUser.getEmail()+",",""));
                        updateTheReactedImage(null,viewImageDialog);

                    }
                    else
                    {
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_LAUGH);
                        updateTheReactionCount(updatedReaction,Constants.USER_REACTIONS_LAUGH);
                        updateTheReactedImage(Constants.USER_REACTIONS_LAUGH,viewImageDialog);
                    }

                    currentChat.setValue(chat);
                    friendsReactableActiveImageRef.setValue(mActiveImage);

                }
            });

            imageViewDislikeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(updatedReaction == null) {
                        updatedReaction = Constants.USER_REACTIONS_DISLIKE;
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_DISLIKE);
                        mActiveImage.setNoOfDislikes(mActiveImage.getNoOfDislikes() + 1);
                        mActiveImage.setWhoDisliked(mCurrentUser.getEmail()+",");
                        updateTheReactedImage(Constants.USER_REACTIONS_DISLIKE,viewImageDialog);

                    }
                    else if(updatedReaction.equals(Constants.USER_REACTIONS_DISLIKE)) {
                        updatedReaction = null;
                        chat.setReactionToProfilePic(null);
                        mActiveImage.setNoOfDislikes(mActiveImage.getNoOfDislikes() - 1);
                        mActiveImage.setWhoDisliked(mActiveImage.getWhoDisliked().replace(mCurrentUser.getEmail()+",",""));
                        updateTheReactedImage(null,viewImageDialog);

                    }
                    else
                    {
                        chat.setReactionToProfilePic(Constants.USER_REACTIONS_DISLIKE);
                        updateTheReactionCount(updatedReaction,Constants.USER_REACTIONS_DISLIKE);
                        updateTheReactedImage(Constants.USER_REACTIONS_DISLIKE,viewImageDialog);

                    }

                    currentChat.setValue(chat);
                    friendsReactableActiveImageRef.setValue(mActiveImage);
                }
            });


            if(updatedReaction != null)
            {
                updateTheReactedImage(updatedReaction,viewImageDialog);
            }

        }
        else
        {
            linearLayoutReactions.setVisibility(View.GONE);
        }



        Glide.with(imageViewImageDialog.getContext())
                .load(chat.getChatPhotoURL())
                .into(imageViewImageDialog);

        imageViewImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageIntent = new Intent(mActivity, ViewImageActivity.class);
                viewImageIntent.putExtra("imageURL", chat.getChatPhotoURL());
                viewImageIntent.putExtra("activityTitle", chat.getChatName());
                mActivity.startActivity(viewImageIntent);
                viewImageDialog.dismiss();
            }
        });



        viewImageDialog.show();
    }

   public void updateTheReactionCount(String oldReaction, String newReaction)
   {
       updatedReaction = newReaction;

       if(oldReaction.equals(Constants.USER_REACTIONS_LIKE)) {
           mActiveImage.setNoOfLikes(mActiveImage.getNoOfLikes() - 1);
           mActiveImage.setWhoLiked(mActiveImage.getWhoLiked().replace(mCurrentUser.getEmail()+",",""));
       }
       else if(oldReaction.equals(Constants.USER_REACTIONS_LOVE)){
           mActiveImage.setNoOfLoves(mActiveImage.getNoOfLoves() - 1);
           mActiveImage.setWhoLoved(mActiveImage.getWhoLoved().replace(mCurrentUser.getEmail()+",",""));
       }
       else if(oldReaction.equals(Constants.USER_REACTIONS_LAUGH)) {
           mActiveImage.setNoOfLaughs(mActiveImage.getNoOfLaughs() - 1);
           mActiveImage.setWhoLaughed(mActiveImage.getWhoLaughed().replace(mCurrentUser.getEmail()+",",""));
       }
       else if(oldReaction.equals(Constants.USER_REACTIONS_DISLIKE)) {
           mActiveImage.setNoOfDislikes(mActiveImage.getNoOfDislikes() - 1);
           mActiveImage.setWhoDisliked(mActiveImage.getWhoDisliked().replace(mCurrentUser.getEmail()+",",""));
       }


       if(newReaction.equals(Constants.USER_REACTIONS_LIKE)) {
           mActiveImage.setNoOfLikes(mActiveImage.getNoOfLikes() + 1);
           if(mActiveImage.getWhoLiked() == null)
               mActiveImage.setWhoLiked(mCurrentUser.getEmail()+",");
           else
               mActiveImage.setWhoLiked(mActiveImage.getWhoLiked()+mCurrentUser.getEmail()+",");
       }
       else if(newReaction.equals(Constants.USER_REACTIONS_LOVE)) {
           mActiveImage.setNoOfLoves(mActiveImage.getNoOfLoves() + 1);
           if(mActiveImage.getWhoLoved() == null)
               mActiveImage.setWhoLoved(mCurrentUser.getEmail()+",");
           else
               mActiveImage.setWhoLoved(mActiveImage.getWhoLoved()+mCurrentUser.getEmail()+",");
       }
       else if(newReaction.equals(Constants.USER_REACTIONS_LAUGH)) {
           mActiveImage.setNoOfLaughs(mActiveImage.getNoOfLaughs() + 1);
           if(mActiveImage.getWhoLaughed() == null)
               mActiveImage.setWhoLaughed(mCurrentUser.getEmail()+",");
           else
               mActiveImage.setWhoLaughed(mActiveImage.getWhoLaughed()+mCurrentUser.getEmail()+",");
       }
       else if(newReaction.equals(Constants.USER_REACTIONS_DISLIKE)) {
           mActiveImage.setNoOfDislikes(mActiveImage.getNoOfDislikes() + 1);
           if(mActiveImage.getWhoDisliked() == null)
               mActiveImage.setWhoDisliked(mCurrentUser.getEmail()+",");
           else
               mActiveImage.setWhoDisliked(mActiveImage.getWhoDisliked()+mCurrentUser.getEmail()+",");
       }
   }

   public void getActiveImagePushId(final Chat chat)
   {
       final Firebase friendsReactableImagesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child( Utils.encodeEmail(chat.getChatEmail()));

       Firebase friendsReactableActiveImagePushIdRef = friendsReactableImagesRef.child("activeProfilePicPushId");
       friendsReactableActiveImagePushIdRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   mActiveImagePushId = dataSnapshot.getValue(String.class);
               }
               else
               {
                   HashMap<String, Object> timestampUploaded = new HashMap<String, Object>();
                   timestampUploaded.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                   mActiveImagePushId = friendsReactableImagesRef.push().getKey();
                   final ReactableImage profilePic = new ReactableImage(mActiveImagePushId,"profilePic_1",chat.getChatPhotoURL(),0,0,0,0,timestampUploaded);

                   friendsReactableImagesRef.child("totalNoOfProfilePics").setValue(1);
                   friendsReactableImagesRef.child("activeProfilePicPushId").setValue(mActiveImagePushId);
                   friendsReactableImagesRef.child(mActiveImagePushId).setValue(profilePic);
               }

               getActiveImage(chat);
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });
   }

   public void getActiveImage(Chat chat)
   {
       final Firebase friendsReactableImagesRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child( Utils.encodeEmail(chat.getChatEmail()));
       friendsReactableActiveImageRef = friendsReactableImagesRef.child(mActiveImagePushId);
       friendsReactableActiveImageRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   mActiveImage = dataSnapshot.getValue(ReactableImage.class);
               }
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });
   }

   public void updateTheReactedImage(String reaction,Dialog viewImageDialog)
   {

       ImageView imageViewLikeImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_like);
       ImageView imageViewLoveImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_love);
       ImageView imageViewLaughImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_laugh);
       ImageView imageViewDislikeImage = (ImageView) viewImageDialog.findViewById(R.id.image_view_dislike);

       if(reaction == null)
       {
           imageViewLikeImage.setImageResource(R.drawable.ic_like);
           imageViewLoveImage.setImageResource(R.drawable.ic_love);
           imageViewLaughImage.setImageResource(R.drawable.ic_laugh);
           imageViewDislikeImage.setImageResource(R.drawable.ic_dislike);
           return;

       }

       try {
           if(reaction.equals(Constants.USER_REACTIONS_LIKE)) {
               imageViewLikeImage.setImageResource(R.drawable.ic_like_selected);
               imageViewLoveImage.setImageResource(R.drawable.ic_love);
               imageViewLaughImage.setImageResource(R.drawable.ic_laugh);
               imageViewDislikeImage.setImageResource(R.drawable.ic_dislike);

           }
           else if(reaction.equals(Constants.USER_REACTIONS_LOVE))
           {
               imageViewLikeImage.setImageResource(R.drawable.ic_like);
               imageViewLoveImage.setImageResource(R.drawable.ic_love_selected);
               imageViewLaughImage.setImageResource(R.drawable.ic_laugh);
               imageViewDislikeImage.setImageResource(R.drawable.ic_dislike);

           }
           else if(reaction.equals(Constants.USER_REACTIONS_LAUGH))
           {
               imageViewLikeImage.setImageResource(R.drawable.ic_like);
               imageViewLoveImage.setImageResource(R.drawable.ic_love);
               imageViewLaughImage.setImageResource(R.drawable.ic_laugh_selected);
               imageViewDislikeImage.setImageResource(R.drawable.ic_dislike);

           }
           else if(reaction.equals(Constants.USER_REACTIONS_DISLIKE))
           {
               imageViewLikeImage.setImageResource(R.drawable.ic_like);
               imageViewLoveImage.setImageResource(R.drawable.ic_love);
               imageViewLaughImage.setImageResource(R.drawable.ic_laugh);
               imageViewDislikeImage.setImageResource(R.drawable.ic_dislike_selected);

           }


       }
       catch (Exception ex)
       {

       }
   }

}
