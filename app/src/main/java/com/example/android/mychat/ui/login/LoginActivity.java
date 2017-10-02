package com.example.android.mychat.ui.login;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.ReactableImage;
import com.example.android.mychat.models.User;
import com.example.android.mychat.services.GetNotification;
import com.example.android.mychat.ui.MainActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.example.android.mychat.MyChatApplication;

import java.util.HashMap;

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth mFirebaseAuth;
    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 2;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;
    protected GoogleApiClient mGoogleApiClient;

    private FirebaseAuth.AuthStateListener mAuthListener;
    public User mCurrentUser;
    public Boolean previouslyAuthenticated = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);

        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String encodedEmail = Utils.encodeEmail(user.getEmail());

                    currentUser(encodedEmail);

                    previouslyAuthenticated = true;
                    SignInButton signInButton = (SignInButton)findViewById(R.id.login_with_google);
                    signInButton.setVisibility(View.GONE);

                }
            }
        };

    }



    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        while (previouslyAuthenticated){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }




    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        initializeBackground(linearLayoutLoginActivity);
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        /* Setup Google Sign In */
        setupGoogleSignIn();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    protected void initializeBackground(LinearLayout linearLayout) {
        /**
         * Set different background image for landscape and portrait layouts
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayout.setBackgroundResource(R.drawable.background_loginscreen_land);
        } else {
            linearLayout.setBackgroundResource(R.drawable.background_loginscreen);
        }
    }





    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }




    /**
     * GOOGLE SIGN IN CODE
     *
     * This code is mostly boiler plate from
     * https://developers.google.com/identity/sign-in/android/start-integrating
     * and
     * https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java
     *
     * The big picture steps are:
     * 1. User clicks the sign in with Google button
     * 2. An intent is started for sign in.
     *      - If the connection fails it is caught in the onConnectionFailed callback
     *      - If it finishes, onActivityResult is called with the correct request code.
     * 3. If the sign in was successful, set the mGoogleAccount to the current account and
     * then call get GoogleOAuthTokenAndLogin
     * 4. getGoogleOAuthTokenAndLogin launches an AsyncTask to get an OAuth2 token from Google.
     * 5. Once this token is retrieved it is available to you in the onPostExecute method of
     * the AsyncTask. **This is the token required by Firebase**
     */


    /* Sets up the Google Sign In Button : https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton */
    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton)findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();

    }




    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                mGoogleAccount = result.getSignInAccount();
                getGoogleOAuthTokenAndLogin();
            }
            else
            {
                if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    showErrorToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
                } else {
                    showErrorToast("Error handling the sign in: " + result.getStatus().getStatusMessage());
                }
                mAuthProgressDialog.dismiss();
            }

        }

    }



    /**
     * Gets the GoogleAuthToken and logs in.
     */
    private void getGoogleOAuthTokenAndLogin() {
        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String userName = task.getResult().getUser().getDisplayName();
                            String userEmail = task.getResult().getUser().getEmail();
                            String userPhotoUrl = task.getResult().getUser().getPhotoUrl().toString();

                            addNewUser(userName, userEmail, userPhotoUrl);
                            currentUser(Utils.encodeEmail(userEmail));

                        }
                    }
                });
    }

    public void addNewUser(String userName,String userEmail,final String userPhotoUrl) {
        final  String userEncodedEmail = Utils.encodeEmail(userEmail);

        final HashMap<String, Object> timestampJoined = new HashMap<String, Object>();
        timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final User user = new User(userName.toUpperCase(), userEmail, userPhotoUrl, timestampJoined);

        final Firebase currentUserReference = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(userEncodedEmail);
        final Firebase currentUserReactableImageRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_REACTABLE_IMAGES).child(userEncodedEmail);

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                            /* If nothing is there ...*/
                if (dataSnapshot.getValue() == null) {
                    currentUserReference.setValue(user);

                    String activeProfilePicPushId = currentUserReactableImageRef.push().getKey();
                    final ReactableImage profilePic = new ReactableImage(activeProfilePicPushId,"profilePic_1",userPhotoUrl,0,0,0,0,timestampJoined);

                    currentUserReactableImageRef.child("totalNoOfProfilePics").setValue(1);
                    currentUserReactableImageRef.child("activeProfilePicPushId").setValue(activeProfilePicPushId);
                    currentUserReactableImageRef.child(activeProfilePicPushId).setValue(profilePic);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                showErrorToast(firebaseError.getMessage());
            }
        });
    }

    public void saveUserData(String encodedEmail,User currentUser)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor spe = sp.edit();

        spe.putString("encodedEmail",encodedEmail).apply();
        //spe.putString("currentUser", currentUser).apply();

    }

    private void currentUser(String encodedEmail)
    {

        Firebase mCurrentUserRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_USERS).child(encodedEmail);

        final Application myChatApplication = this.getApplication();

        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    mCurrentUser = user;


                    ((MyChatApplication) myChatApplication).setCurrentUser(mCurrentUser);
                    ((MyChatApplication) myChatApplication).setEncodedEmail(Utils.encodeEmail(mCurrentUser.getEmail()));


                    Intent notificationIntent = new Intent(LoginActivity.this, GetNotification.class);
                    notificationIntent.putExtra("encodedEmail",Utils.encodeEmail(mCurrentUser.getEmail()));
                    startService(notificationIntent);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("encodedEmail",Utils.encodeEmail(mCurrentUser.getEmail()));
                    intent.putExtra("currentUser",mCurrentUser);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }
}