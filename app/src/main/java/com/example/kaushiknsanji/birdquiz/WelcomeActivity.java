/*
 * Copyright 2017 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kaushiknsanji.birdquiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The first Activity class of the Quiz app that shows the layout "R.layout.activity_welcome"
 * where the user is presented with a brief info and a button to begin the quiz
 *
 * @author Kaushik N Sanji
 */
public class WelcomeActivity extends AppCompatActivity
        implements View.OnClickListener,
        QuestionNumberPickerDialogFragment.QuestionNumberPickedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    //Bundle Key Constant for saving/restoring
    private static final String TOTAL_NUMBER_OF_QUESTIONS_INT_KEY = "TotalNumberOfQuestions";
    //Stores total number of questions in the resources
    private int mTotalNumberOfQuestions;
    //Stores the Views that would be accessed frequently
    private TextView mInfoTextView;
    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.i(TAG, "onCreate: Started");

        //Retrieving the Views that will be accessed frequently: START
        mInfoTextView = findViewById(R.id.info_text_id);
        mTitleTextView = findViewById(R.id.title_text_id);
        //Retrieving the Views that will be accessed frequently: END

        if (savedInstanceState == null) {

            //Retrieving the list of questions from Array resource "question_array"
            String[] questionArray = getResources().getStringArray(R.array.question_array);
            mTotalNumberOfQuestions = questionArray.length;

            //Setting the Info Text
            setInfoTextView();

            Log.i(TAG, "onCreate: Total Number of Questions found: " + mTotalNumberOfQuestions);
        }

        //Retrieving the current orientation
        int screenOrientation = getResources().getConfiguration().orientation;

        //Setting the title text according to screen Orientation
        setTitleTextView(screenOrientation);

        //Setting the listener on the "Begin Quiz" Button (R.id.begin_quiz_button_id)
        Button beginQuizButton = findViewById(R.id.begin_quiz_button_id);
        beginQuizButton.setOnClickListener(this);

    }

    //Called by the Activity before Stop, to save the activity's state in the Bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Saving the total number of questions retrieved
        outState.putInt(TOTAL_NUMBER_OF_QUESTIONS_INT_KEY, mTotalNumberOfQuestions);

    }

    //Called by the Activity after Start when the activity is being reinitialized
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Retrieving the total number of questions
        mTotalNumberOfQuestions = savedInstanceState.getInt(TOTAL_NUMBER_OF_QUESTIONS_INT_KEY);

        //Reinitializing the Info Text
        setInfoTextView();
    }

    /**
     * Method that sets the text on TextView "R.id.info_text_id"
     */
    private void setInfoTextView() {
        //Retrieving the text to be shown
        String infoTextStr = getString(R.string.info_text, mTotalNumberOfQuestions);
        //Parsing for HTML formatting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mInfoTextView.setText(Html.fromHtml(infoTextStr, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mInfoTextView.setText(Html.fromHtml(infoTextStr));
        }

        //Setting the TypeFace to be used
        Typeface infoTextFace = Typeface.createFromAsset(getAssets(), "fonts/quintessential_regular.ttf");
        mInfoTextView.setTypeface(infoTextFace);
    }

    /**
     * Method that sets the Title Text "R.id.title_text_id" based on Screen Orientation
     *
     * @param screenOrientation The Integer value of the current Screen Orientation
     */
    private void setTitleTextView(int screenOrientation) {
        String titleTextStr = "";

        //Retrieving the Title Text based on the current Screen Orientation
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            titleTextStr = getString(R.string.title_text, "\n");
        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            titleTextStr = getString(R.string.title_text, " ");
        }
        mTitleTextView.setText(titleTextStr); //Setting the text

        //Setting the TypeFace to be used
        Typeface titleTextFace = Typeface.createFromAsset(getAssets(), "fonts/rothenbg.ttf");
        mTitleTextView.setTypeface(titleTextFace);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.begin_quiz_button_id) {
            onBeginQuizButtonClicked();
        }

    }

    /**
     * Method invoked when the user clicks on the Button "R.id.begin_quiz_button_id"
     */
    private void onBeginQuizButtonClicked() {
        //Initializing the Question Number Picker Dialog
        QuestionNumberPickerDialogFragment questionNumberPickerDialogFragment = QuestionNumberPickerDialogFragment.newInstance(1, mTotalNumberOfQuestions);
        //Displaying the dialog to capture the user input
        questionNumberPickerDialogFragment.show(getFragmentManager(), "QuestionNumberPicker");
    }

    /**
     * Callback Method of {@link QuestionNumberPickerDialogFragment.QuestionNumberPickedListener}
     * invoked when the user clicks on the SET Button
     *
     * @param selectedValue The Number picked by the user through the Number Picker
     */
    @Override
    public void onDialogPositiveClick(int selectedValue) {

        //Preparing the Intent call for {@link .QuizActivity}
        Intent quizActivityIntent = new Intent(this, QuizActivity.class);
        quizActivityIntent.putExtra(getString(R.string.total_question_opt_value), selectedValue);

        //Starting the {@link .QuizActivity}
        startActivity(quizActivityIntent);

        //Exiting the current activity once done
        finish();

    }

    /**
     * Callback Method of {@link QuestionNumberPickerDialogFragment.QuestionNumberPickedListener}
     * invoked when the user clicks on the CANCEL Button
     */
    @Override
    public void onDialogNegativeClick() {
        Toast.makeText(this, R.string.number_picker_cancel_toast_text, Toast.LENGTH_SHORT).show();
    }

}

