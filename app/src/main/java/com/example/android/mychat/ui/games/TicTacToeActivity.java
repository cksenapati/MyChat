package com.example.android.mychat.ui.games;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.models.Chat;
import com.example.android.mychat.models.TicTacToe;
import com.example.android.mychat.models.User;
import com.example.android.mychat.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class TicTacToeActivity extends AppCompatActivity {

    Chat mCurrentChat;
    User mCurrentUser;
    TicTacToe mTicTacToe;

    int result = 0;



    ImageView mImageViewMyProfilePic;
    ImageView mImageViewMyFriendPic;
    ImageView mImageViewMyPawn;
    ImageView mImageViewFriendPawn;
    TextView mTextViewMyPoint;
    TextView mTextViewFriendsPoint;
    TextView mTextViewMyTurn;
    TextView mTextViewFriendTurn;
    TextView mTextViewGameResult;
    TextView mTextViewPlayAgain;
    LinearLayout mLinearLayoutGameBoard;

    Firebase mTicTacToeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        initializeScreen();

        mTicTacToeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists())
              {
                  mTicTacToe = dataSnapshot.getValue(TicTacToe.class);
                  if(mTicTacToe != null)
                  {
                      if(mTicTacToe.getUser1().equals(mCurrentUser.getEmail()))
                      {
                          mTextViewMyPoint.setText(String.valueOf(mTicTacToe.getPoint1()));
                          mTextViewFriendsPoint.setText(String.valueOf(mTicTacToe.getPoint2()));
                          mImageViewMyPawn.setImageResource(R.drawable.tictactoe_circle);
                          mImageViewFriendPawn.setImageResource(R.drawable.tictactoe_cross);
                      }
                      else
                      {
                          mTextViewMyPoint.setText(String.valueOf(mTicTacToe.getPoint2()));
                          mTextViewFriendsPoint.setText(String.valueOf(mTicTacToe.getPoint1()));
                          mImageViewFriendPawn.setImageResource(R.drawable.tictactoe_circle);
                          mImageViewMyPawn.setImageResource(R.drawable.tictactoe_cross);
                      }

                      if(mTicTacToe.getWhoseTurn().equals(mCurrentUser.getEmail()))
                      {
                          mTextViewMyTurn.setVisibility(View.VISIBLE);
                          mTextViewFriendTurn.setVisibility(View.GONE);
                          enableButtonsTurnWise(true);
                      }
                      else
                      {
                          mTextViewMyTurn.setVisibility(View.GONE);
                          mTextViewFriendTurn.setVisibility(View.VISIBLE);
                          enableButtonsTurnWise(false);
                      }

                      displayGameBoard();

                  }
              }
              else
              {
                  int[][] gameTable = {{0,0,0},{0,0,0},{0,0,0}};
                  TicTacToe ticTacToe = new TicTacToe(mCurrentUser.getEmail(),mCurrentChat.getChatEmail(),0,0,mCurrentUser.getEmail(),gameTable);
                  mTicTacToeRef.setValue(ticTacToe);
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

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        *//* Add back button to the action bar *//*
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
*/
        mCurrentChat = ((MyChatApplication) this.getApplication()).getCurrentChat();
        mCurrentUser = ((MyChatApplication) this.getApplication()).getCurrentUser();


        mImageViewMyProfilePic = (ImageView) findViewById(R.id.image_view_my_profile_pic);
        mImageViewMyFriendPic = (ImageView) findViewById(R.id.image_view_friend_profile_pic);
        mImageViewMyPawn = (ImageView) findViewById(R.id.image_view_my_pawn);
        mImageViewFriendPawn = (ImageView) findViewById(R.id.image_view_friends_pawn);
        mTextViewMyPoint = (TextView) findViewById(R.id.text_view_my_point);
        mTextViewFriendsPoint = (TextView) findViewById(R.id.text_view_friends_point);
        mTextViewMyTurn = (TextView) findViewById(R.id.text_view_my_turn);
        mTextViewFriendTurn = (TextView) findViewById(R.id.text_view_friends_turn);
        mTextViewGameResult = (TextView) findViewById(R.id.text_view_game_result);
        mTextViewPlayAgain = (TextView) findViewById(R.id.text_view_play_again);

        mLinearLayoutGameBoard = (LinearLayout) findViewById(R.id.linear_layout_game_board);


        Glide.with(mImageViewMyProfilePic.getContext())
                .load(mCurrentUser.getPhotoURL())
                .into(mImageViewMyProfilePic);

        Glide.with(mImageViewMyFriendPic.getContext())
                .load(mCurrentChat.getChatPhotoURL())
                .into(mImageViewMyFriendPic);


        mTicTacToeRef = new Firebase(Constants.FIREBASE_URL_MY_CHAT_ALL_GAMES).child(mCurrentChat.chatId).child(Constants.FIREBASE_PROPERTY_TICTACTOE);

    }

    public void enableButtonsTurnWise(boolean isMyTurn)
    {
           for (int i = 0; i < 3; i++) {
                LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(i);

                for (int j = 0; j < 3; j++) {
                    ImageButton imageButton = (ImageButton) linearLayout.getChildAt(j);
                    imageButton.setEnabled(isMyTurn);
                }
            }

    }

    public void displayGameBoard()
    {
        for(int i=0;i<3;i++)
        {
            LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(i);

            for(int j=0;j<3;j++)
            {
                ImageButton button = (ImageButton) linearLayout.getChildAt(j);

                if(mTicTacToe.getGameTable()[i][j] == 1)
                {
                    button.setImageResource(R.drawable.tictactoe_circle);
                    button.setEnabled(false);
                }
                else if (mTicTacToe.getGameTable()[i][j] == 2)
                {
                    button.setImageResource(R.drawable.tictactoe_cross);
                    button.setEnabled(false);
                }
                else
                {
                    int colourCode = ContextCompat.getColor(this, R.color.grey);

                    button.setImageResource(0);
                    button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);

                }
            }
        }

        checkGame();
    }

    public void onImageButtonClick(View v) {

        int[][] gameTable = mTicTacToe.getGameTable();
        int pawn=0;
        String nextTurn;

        if(mTicTacToe.getUser1().equals(mCurrentUser.getEmail())) {
            pawn = 1;
            nextTurn = mTicTacToe.getUser2();
        }
        else {
            pawn = 2;
            nextTurn = mTicTacToe.getUser1();

        }

        switch (v.getId()) {
            case R.id.button_00:
                gameTable[0][0]=pawn;
                break;
            case R.id.button_01:
                gameTable[0][1]=pawn;
                break;
            case R.id.button_02:
                gameTable[0][2]=pawn;
                break;
            case R.id.button_10:
                gameTable[1][0]=pawn;
                break;
            case R.id.button_11:
                gameTable[1][1]=pawn;
                break;
            case R.id.button_12:
                gameTable[1][2]=pawn;
                break;
            case R.id.button_20:
                gameTable[2][0]=pawn;
                break;
            case R.id.button_21:
                gameTable[2][1]=pawn;
                break;
            case R.id.button_22:
                gameTable[2][2]=pawn;
                break;

        }

        mTicTacToeRef.child("gameTable").setValue(gameTable);
        mTicTacToeRef.child("whoseTurn").setValue(nextTurn);

    }

    public void checkGame()
    {
        int countNonZeroIndex = 0;

        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(mTicTacToe.getGameTable()[i][j] != 0)
                    countNonZeroIndex++;

            }
        }

        mTextViewGameResult.setVisibility(View.INVISIBLE);
        result = 0;

        if(countNonZeroIndex < 9)
            mTextViewPlayAgain.setVisibility(View.INVISIBLE);


        if(countNonZeroIndex < 5)
            return;


        for(int i=0;i<3;i++)
        {
            result = checkRow(i);
            if(result != 0) {
                displayResult(result);
                return;
            }
        }
        for(int i=0;i<3;i++)
        {
            result = checkColumn(i);
            if(result != 0) {
                displayResult(result);
                return;
            }
        }

        result = checkLeftToRightDiagonal();
        if(result != 0) {
            displayResult(result);
            return;
        }

        result = checkRightToLeftDiagonal();
        if(result != 0) {
            displayResult(result);
            return;
        }

        if(countNonZeroIndex == 9)
            displayResult(0);
    }

    private int checkRow(int row)
    {

        if(mTicTacToe.getGameTable()[row][0] == mTicTacToe.getGameTable()[row][1] &&
                mTicTacToe.getGameTable()[row][1] == mTicTacToe.getGameTable()[row][2] && mTicTacToe.getGameTable()[row][0] != 0)
        {
            int colourCode = ContextCompat.getColor(this, R.color.primary);

            LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(row);
            for(int j=0;j<3;j++) {
                ImageButton button = (ImageButton) linearLayout.getChildAt(j);
                button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
            }


            return mTicTacToe.getGameTable()[row][0];
        }
        else
          return 0;
    }

    private int checkColumn(int column)
    {

        if(mTicTacToe.getGameTable()[0][column] == mTicTacToe.getGameTable()[1][column] &&
                mTicTacToe.getGameTable()[1][column] == mTicTacToe.getGameTable()[2][column] && mTicTacToe.getGameTable()[0][column] != 0)
        {
            int colourCode = ContextCompat.getColor(this, R.color.primary);

            for(int j=0;j<3;j++) {
                LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(j);
                ImageButton button = (ImageButton) linearLayout.getChildAt(column);

                button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
            }

            return mTicTacToe.getGameTable()[0][column];
        }
        else
            return 0;
    }

    private int checkLeftToRightDiagonal()
    {
        if(mTicTacToe.getGameTable()[0][0] == mTicTacToe.getGameTable()[1][1] &&
                mTicTacToe.getGameTable()[1][1] == mTicTacToe.getGameTable()[2][2]  && mTicTacToe.getGameTable()[1][1] != 0)
        {
            int colourCode = ContextCompat.getColor(this, R.color.primary);

            for(int j=0;j<3;j++) {
                LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(j);
                ImageButton button = (ImageButton) linearLayout.getChildAt(j);

                button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
            }

            return mTicTacToe.getGameTable()[0][0];
        }
        else
            return 0;
    }

    private int checkRightToLeftDiagonal()
    {
        if(mTicTacToe.getGameTable()[0][2] == mTicTacToe.getGameTable()[1][1] &&
                mTicTacToe.getGameTable()[1][1] == mTicTacToe.getGameTable()[2][0] && mTicTacToe.getGameTable()[1][1] != 0)
        {
            int colourCode = ContextCompat.getColor(this, R.color.primary);
            int k = 2;

            for(int j=0;j<3;j++) {
                LinearLayout linearLayout = (LinearLayout) mLinearLayoutGameBoard.getChildAt(j);
                ImageButton button = (ImageButton) linearLayout.getChildAt(k);

                button.getBackground().setColorFilter(colourCode, PorterDuff.Mode.MULTIPLY);
                k--;
            }

            return mTicTacToe.getGameTable()[1][1];
        }
        else
            return 0;
    }

    private  void displayResult(int woner)
    {
        mTextViewGameResult.setVisibility(View.VISIBLE);

        if(woner == 0)
        {
            mTextViewGameResult.setText("Game Drawn");
        }
        else if(woner == 1)
        {
            if(mTicTacToe.getUser1().equals(mCurrentUser.getEmail()))
            {
                mTextViewGameResult.setText("You won");
            }
            else
            {
                mTextViewGameResult.setText("Opponent Won");
            }

            mTicTacToeRef.child("whoseTurn").setValue(mTicTacToe.getUser1());

        }
        else
        {
            if(mTicTacToe.getUser2().equals(mCurrentUser.getEmail()))
            {
                mTextViewGameResult.setText("You Won");
            }
            else
            {
                mTextViewGameResult.setText("Opponent Won");
            }

            mTicTacToeRef.child("whoseTurn").setValue(mTicTacToe.getUser2());

        }


        enableButtonsTurnWise(false);
        mTextViewMyTurn.setVisibility(View.GONE);
        mTextViewFriendTurn.setVisibility(View.GONE);

        mTextViewPlayAgain.setVisibility(View.VISIBLE);
    }

    public void onPlayAgainClick(View v)
    {
        int[][] gameTable = {{0,0,0},{0,0,0},{0,0,0}};
        mTicTacToeRef.child("gameTable").setValue(gameTable);

        if(result == 1) {
            int point1 = mTicTacToe.getPoint1();
            mTicTacToeRef.child("point1").setValue(point1+1);
        }
        else if(result == 2)
        {
            int point2 = mTicTacToe.getPoint1();
            mTicTacToeRef.child("point2").setValue(point2+1);
        }

    }
}
