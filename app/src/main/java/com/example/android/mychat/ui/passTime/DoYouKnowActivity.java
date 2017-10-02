package com.example.android.mychat.ui.passTime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.MyChatApplication;
import com.example.android.mychat.R;
import com.example.android.mychat.ui.chatDetails.ChatDetailsActivity;
import com.example.android.mychat.ui.games.TicTacToeActivity;
import com.example.android.mychat.utils.Constants;
import com.example.android.mychat.utils.Utils;
import com.google.gson.JsonObject;
import static org.apache.commons.lang3.StringEscapeUtils.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static android.R.attr.max;

public class DoYouKnowActivity extends AppCompatActivity {

    String question;
    String correctAnswer;
    String wrongAnswer1;
    String wrongAnswer2;
    String wrongAnswer3;

    TextView mTextViewQuestion;
    Button mButtonLockMyAns;
    RadioGroup mRadioGroup;
    RadioButton mRadioButtonOption1;
    RadioButton mRadioButtonOption2;
    RadioButton mRadioButtonOption3;
    RadioButton mRadioButtonOption4;
    ProgressBar mProgressBar;
    LinearLayout mLinearLayoutBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_you_know);

        initializeScreen();


        createThread();

        mButtonLockMyAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int givenAnswerId = mRadioGroup.getCheckedRadioButtonId();

                if(givenAnswerId == -1)
                {
                    Toast.makeText(DoYouKnowActivity.this,"Select one option.",Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton givenAnswerRadioButton = (RadioButton) findViewById(givenAnswerId);
                if(givenAnswerRadioButton.getText().equals(correctAnswer)){
                    Toast.makeText(DoYouKnowActivity.this,"Correct Answer.",Toast.LENGTH_SHORT).show();
                    createThread();
                }
                else
                    Toast.makeText(DoYouKnowActivity.this,"Wrong Answer.",Toast.LENGTH_SHORT).show();
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_doyouknow, menu);


        MenuItem actionFilter = menu.findItem(R.id.action_filter_questions);

        actionFilter.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_filter_questions :
                Intent intentFilterQuestion = new Intent(this , FilterQuestionsActivity.class);
                startActivity(intentFilterQuestion);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    public void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("DoYouKnow");

        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextViewQuestion = (TextView) findViewById(R.id.text_view_question);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group_options);
        mButtonLockMyAns = (Button) findViewById(R.id.button_lock_my_ans);
        mRadioButtonOption1 = (RadioButton) findViewById(R.id.radio_button_option1);
        mRadioButtonOption2 = (RadioButton) findViewById(R.id.radio_button_option2);
        mRadioButtonOption3 = (RadioButton) findViewById(R.id.radio_button_option3);
        mRadioButtonOption4 = (RadioButton) findViewById(R.id.radio_button_option4);
        mProgressBar = (ProgressBar)  findViewById(R.id.progress_bar_fetching_data);
        mLinearLayoutBody = (LinearLayout) findViewById(R.id.linear_layout_body);
    }

    public void createThread()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String questionCategory = prefs.getString("selected_category","0");
        String difficultyLevel = prefs.getString("selected_difficulty_level","0");

        // Create URL object
        String openTDbBaseUrl = Constants.OPENTDB_BASE_URL;
        String singleQuestionURL = openTDbBaseUrl+"amount=1";



        //https://opentdb.com/api.php?amount=10&category=32&difficulty=easy
        if(!questionCategory.equals("0") && !questionCategory.equals("-1"))
        {
            singleQuestionURL = singleQuestionURL+"&category="+questionCategory;
        }
        if(!difficultyLevel.equals("0") && !difficultyLevel.equals("-1"))
        {
            singleQuestionURL = singleQuestionURL+"&difficulty="+difficultyLevel;
        }

        doYouKnowAsyncTask task = new doYouKnowAsyncTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,singleQuestionURL);

    }

    public void getQuestion(String singleQuestionURL)
    {


        URL url = null;
        try {
             url = new URL(singleQuestionURL);
        } catch (MalformedURLException e) {

        }


        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = Utils.makeHttpRequest(url);
        }
        catch (IOException e) {
        }


        extractFeatureFromJson(jsonResponse);
    }

    private void extractFeatureFromJson(String jsonObjectURL) {

        wrongAnswer1 = "";
        wrongAnswer2 = "";
        wrongAnswer3 = "";

        if (TextUtils.isEmpty(jsonObjectURL)) {
            return ;
        }


         try
        {

            JSONObject rootJsonObject = new JSONObject(jsonObjectURL);

            String responseCode = rootJsonObject.getString("response_code");
            if(!responseCode.equals("0"))
            {
                createThread();
                return;
            }
            JSONArray arrayOfQuestions = rootJsonObject.getJSONArray("results");

            //We always request for 1 question
            JSONObject eachQuestionObject = arrayOfQuestions.getJSONObject(0);

            question = eachQuestionObject.getString("question");
            correctAnswer = eachQuestionObject.getString("correct_answer");
            JSONArray arrayOfWrongAnswers = eachQuestionObject.getJSONArray("incorrect_answers");
            wrongAnswer1 =arrayOfWrongAnswers.getString(0);
            wrongAnswer2 =arrayOfWrongAnswers.getString(1);
            wrongAnswer3 =arrayOfWrongAnswers.getString(2);

            question = StringEscapeUtils.unescapeHtml4(question);
            correctAnswer= StringEscapeUtils.unescapeHtml4(correctAnswer);
            wrongAnswer1 = StringEscapeUtils.unescapeHtml4(wrongAnswer1 );
            wrongAnswer2 = StringEscapeUtils.unescapeHtml4(wrongAnswer2 );
            wrongAnswer3 = StringEscapeUtils.unescapeHtml4(wrongAnswer3 );



        }
        catch (Exception e)
        {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
    }

    public  void displayData()
    {
        mTextViewQuestion.setText(question);

        mRadioGroup.clearCheck();


        int randomNumber = new Random().nextInt((4 - 1) + 1) + 1;
        switch (randomNumber)
        {
            case 1: mRadioButtonOption1.setText(correctAnswer);
                    mRadioButtonOption2.setText(wrongAnswer1);
                    mRadioButtonOption3.setText(wrongAnswer2);
                    mRadioButtonOption4.setText(wrongAnswer3);
                    break;
            case 2: mRadioButtonOption2.setText(correctAnswer);
                    mRadioButtonOption1.setText(wrongAnswer1);
                    mRadioButtonOption3.setText(wrongAnswer2);
                    mRadioButtonOption4.setText(wrongAnswer3);
                    break;
            case 3: mRadioButtonOption3.setText(correctAnswer);
                    mRadioButtonOption1.setText(wrongAnswer1);
                    mRadioButtonOption2.setText(wrongAnswer2);
                    mRadioButtonOption4.setText(wrongAnswer3);
                    break;
            case 4: mRadioButtonOption4.setText(correctAnswer);
                    mRadioButtonOption1.setText(wrongAnswer1);
                    mRadioButtonOption2.setText(wrongAnswer2);
                    mRadioButtonOption3.setText(wrongAnswer3);
                    break;
        }

        if(mRadioButtonOption1.getText() == null || mRadioButtonOption1.getText().equals(""))
            mRadioButtonOption1.setVisibility(View.GONE);
        else
            mRadioButtonOption1.setVisibility(View.VISIBLE);

        if(mRadioButtonOption2.getText() == null || mRadioButtonOption2.getText().equals(""))
            mRadioButtonOption2.setVisibility(View.GONE);
        else
            mRadioButtonOption2.setVisibility(View.VISIBLE);

        if(mRadioButtonOption3.getText() == null || mRadioButtonOption3.getText().equals(""))
            mRadioButtonOption3.setVisibility(View.GONE);
        else
            mRadioButtonOption3.setVisibility(View.VISIBLE);

        if(mRadioButtonOption4.getText() == null || mRadioButtonOption4.getText().equals(""))
            mRadioButtonOption4.setVisibility(View.GONE);
        else
            mRadioButtonOption4.setVisibility(View.VISIBLE);


    }

    private class doYouKnowAsyncTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mLinearLayoutBody.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... urls) {
            if(urls.length <1 || urls[0] == null)
                return null ;

            getQuestion(urls[0]);
            return "success";
        }

        @Override
        protected void onPostExecute(String string)
        {
            if(string == null)
                return;
            else
                displayData();
            mProgressBar.setVisibility(View.GONE);
            mLinearLayoutBody.setEnabled(true);

        }
    }


}

