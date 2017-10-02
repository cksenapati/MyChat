package com.example.android.mychat.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.User;
import com.example.android.mychat.services.GetNotification;
import com.example.android.mychat.ui.addFriends.addFriendsFragment;
import com.example.android.mychat.ui.allChats.allChatsFragment;
import com.example.android.mychat.ui.login.LoginActivity;
import com.example.android.mychat.ui.myProfile.MyProfileActivity;
import com.example.android.mychat.ui.newGroup.AddParticipantsActivity;
import com.example.android.mychat.ui.passTime.DoYouKnowActivity;
import com.example.android.mychat.ui.searchFriends.searchFriendsFragment;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.auth.AuthUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    //Authentication variables
    private String mUsername;
    private String mEncodedEmail;
    public static final String ANONYMOUS = "anonymous";
    public User mCurrentUser;
    EditText mEditTextSearch;
    ImageView mImageViewCancelSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);


        initializeScreen();

        Intent intent = getIntent();
        if(intent != null)
        {
            mEncodedEmail = ((MyChatApplication) this.getApplication()).getEncodedEmail();   /*intent.getStringExtra("encodedEmail");*/
            mCurrentUser =  ((MyChatApplication) this.getApplication()).getCurrentUser();    /*(User) intent.getSerializableExtra("currentUser");*/
        }

        mImageViewCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextSearch.setText("");
                mEditTextSearch.setVisibility(View.GONE);
                mImageViewCancelSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextSearch.getWindowToken(), 0);
            }
        });

        cleanNotifications();
    }




    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.app_name);
        Utils.makeMeOnOrOffline(mEncodedEmail, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.makeMeOnOrOffline(mEncodedEmail, false);
        Intent notificationIntent = new Intent(this, GetNotification.class);
        stopService(notificationIntent);
    }

    /**
     * Override onOptionsItemSelected to use main_menu instead of BaseActivity menu
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem myProfile = menu.findItem(R.id.action_my_profile);
        MenuItem newGroup = menu.findItem(R.id.action_new_group);
        MenuItem doYouknow = menu.findItem(R.id.action_do_you_know);
        MenuItem search = menu.findItem(R.id.action_search);

        logout.setVisible(true);
        myProfile.setVisible(true);
        newGroup.setVisible(true);
        doYouknow.setVisible(true);
        search.setVisible(true);


        Drawable drawable = search.getIcon();
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        }


        return true;
    }

    /**
     * Override onOptionsItemSelected to add action_settings only to the MainActivity
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_logout :
                Utils.makeMeOnOrOffline(mEncodedEmail,false);
                AuthUI.getInstance().signOut(this);

                Intent notificationService = new Intent(this,GetNotification.class);
                stopService(notificationService);

                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_my_profile :
                Intent myProfileIntent = new Intent(MainActivity.this , MyProfileActivity.class);

                myProfileIntent.putExtra("encodedEmail", mEncodedEmail);

                startActivity(myProfileIntent);
                return true;
            case R.id.action_new_group :
                Intent newGroupIntent = new Intent(MainActivity.this , AddParticipantsActivity.class);
                newGroupIntent.putExtra("encodedEmail", mEncodedEmail);
                newGroupIntent.putExtra("chatEmail", "");
                startActivity(newGroupIntent);
                return true;
            case R.id.action_do_you_know :
                Intent intentDoYouKnow = new Intent(MainActivity.this , DoYouKnowActivity.class);
                startActivity(intentDoYouKnow);
                return true;
            case R.id.action_search :

                if(mEditTextSearch.getVisibility() == View.VISIBLE) {
                    mEditTextSearch.setText("");
                    mEditTextSearch.setVisibility(View.GONE);
                    mImageViewCancelSearch.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTextSearch.getWindowToken(), 0);
                }
                else {
                    mEditTextSearch.setVisibility(View.VISIBLE);
                    mImageViewCancelSearch.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditTextSearch, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditTextSearch = (EditText) findViewById(R.id.edit_text_search_friends);

        mImageViewCancelSearch = (ImageView) findViewById(R.id.image_view_cancel_search);
        Drawable drawable = mImageViewCancelSearch.getDrawable();
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

        setSupportActionBar(toolbar);




        /**
         * Create SectionPagerAdapter, set it as adapter to viewPager with setOffscreenPageLimit(2)
         **/
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
    }


    /**
     * SectionPagerAdapter class that extends FragmentStatePagerAdapter to save fragments state
     */
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Use positions (0 and 1) to find and instantiate fragments with newInstance()
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    fragment = allChatsFragment.newInstance(mEncodedEmail,mCurrentUser);
                    break;
                case 1:
                    fragment = addFriendsFragment.newInstance(mEncodedEmail,mCurrentUser);
                    break;
                case 2:
                    fragment = searchFriendsFragment.newInstance(mEncodedEmail,mCurrentUser);
                    break;
                default:
                    fragment = allChatsFragment.newInstance(mEncodedEmail,mCurrentUser);
                    break;
            }

            return fragment;
        }


        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Set string resources as titles for each fragment by it's position
         *
         * @param position
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pager_title_all_chats);
                case 1: return getString(R.string.pager_title_add_friends);
                case 2:
                default:
                    return getString(R.string.pager_title_search_friends);
            }
        }
    }


    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void cleanNotifications()
    {
         Firebase mMyNotificationReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_NOTIFICATIONS).child(mEncodedEmail);
         mMyNotificationReference.setValue(null);
    }
}
