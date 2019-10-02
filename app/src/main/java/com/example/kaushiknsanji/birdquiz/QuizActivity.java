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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.LevelListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The Main and the second Activity class of the Quiz app that shows the layout "R.layout.activity_quiz"
 * where the user is presented with the Quiz screen having the questions and options to answer and score
 *
 * @author Kaushik N Sanji
 */
public class QuizActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        ImageDownloaderTaskFragment.ImageDownloaderListener,
        CountDownLatchFragment.CountDownLatchListener,
        FinalScoreDialogFragment.FinalScoreDialogListener {

    private static final String TAG = QuizActivity.class.getSimpleName();
    //Bundle Key Constants for saving/restoring
    private static final String QUESTION_STR_ARRAY_KEY = "QuestionArray";
    private static final String OPTION_STR_ARRAY_KEY = "OptionArray";
    private static final String HINT_STR_ARRAY_KEY = "HintArray";
    private static final String QUESTION_INDEX_ORDER_STR_ARRAY_KEY = "QuestionIndexOrderArray";
    private static final String OPTION_INDEX_ORDER_STR_KEY = "OptionIndexOrder";
    private static final String SELECTED_ANSWER_STR_LIST_KEY = "SelectedAnswerList";
    private static final String SELECTED_INDEX_ORDER_STR_KEY = "SelectedIndexOrder";
    private static final String CORRECT_ANSWER_STR_LIST_KEY = "CorrectAnswerList";
    private static final String TOTAL_QUESTIONS_INT_KEY = "TotalNumberOfQuestions";
    private static final String QUESTIONS_TO_LOAD_INT_KEY = "NoOfQuestionsToLoad";
    private static final String FIRST_TIME_LAUNCH_BOOL_KEY = "FirstTimeLaunch";
    private static final String CURRENT_QUESTION_NO_INT_KEY = "CurrentQuestionNo";
    private static final String CURRENT_USER_SCORE_INT_KEY = "CurrentUserScore";
    private static final String CURRENT_QUESTION_INDEX_INT_KEY = "CurrentQuestionIndex";
    private static final String FUTURE_QUESTION_INDEX_INT_KEY = "FutureQuestionIndex";
    private static final String HINT_BUTTON_STATE_BOOL_KEY = "HintButtonState";
    private static final String HINT_BUTTON_PRESSED_BOOL_KEY = "HintButtonPressed";
    private static final String TEXTUAL_QUESTION_BOOL_KEY = "IsTextualQuestion";
    private static final String TEXTUAL_USER_INPUT_STR_KEY = "TextualUserInput";
    private static final String ACTIVITY_STATE_STR_KEY = "ActivityState";
    private static final String SUBMIT_BUTTON_STATE_STR_KEY = "SubmitButtonState";
    //Bundle Key Constants for Option Container view "R.id.option_container_area_id"
    private static final String EXISTING_CHILD_COUNT_INT_KEY = "ExistingChildCount";
    private static final String EXISTING_CHILD_TYPE_STR_KEY = "ExistingChildType";
    //Stores the complete list of questions
    private String[] mQuestionArray;
    //Stores the list of Options for the current question
    private String[] mOptionArray;
    //Stores the list of Hints for the current question
    private String[] mHintArray;
    //Stores the current index list of questions to be loaded in String array
    private String[] mQuestionIndexOrderArray;
    //Stores the index list of options to be loaded for the current question in some order
    private String mOptionIndexOrderStr;
    //Stores the Answers selected by the user
    private ArrayList<String> mSelectedAnswerList;
    //Stores the index of the Answers selected by the user. (Used only for restoring)
    private String mSelectedIndexOrderStr;
    //Stores the list of correct Answers for the current question
    private ArrayList<String> mCorrectAnswerList;
    //Stores the Views that would be accessed frequently
    private TextView mQuestionNumberTextView;
    private TextView mTotalScoreTextView;
    private TextView mQuestionTextView;
    private TextView mHintTextView;
    private Button mSubmitButtonView;
    private Button mShowHintButtonView;
    private ImageButton mImageRedirectButtonView;
    private RelativeLayout mOptionContainerView;
    private LinearLayout mHintContentView;
    private ScrollView mScrollableContentView;
    private ImageView mHintImageView;
    private TextView mCountDownTextView;
    //Stores total number of questions in the resources
    private int mTotalNumberOfQuestions;
    //Stores the number of questions to load
    private int mNoOfQuestionsToLoad;
    //Stores the state of whether the app was launched for the first time
    //Defaulting to FALSE
    private boolean mFirstTimeLaunch = false;
    //Stores whether the onCreate was invoked when the app resumes
    //Defaulting to FALSE
    private boolean onCreateInvoked = false;
    //Stores the current question number being displayed. Defaulting to 0
    private int mCurrentQuestionNo = 0;
    //Stores the current question index from the list
    private int mCurrentQuestionIndex;
    //Stores the Next question index from the list. Defaulting to 0;
    private int mFutureQuestionIndex = 0;
    //Stores whether the Hint button is enabled(True)/disabled(False)
    private boolean mHintButtonState = false;
    //Stores whether the Hint button was pressed
    private boolean mHintButtonPressed = false;
    //Stores whether the question is text based or not (False when not)
    private boolean mIsTextualQuestion = false;
    //Stores the EditText view for the textual question
    private EditText mTextOptionView;
    //Stores the EditText view content for the textual question
    private String mTextualUserInputStr;
    //Stores the Image downloaded from a URL (Used as a Hint image)
    private Bitmap mDownloadedBitmap;
    //Stores the Image downloaded from a URL for the next question in advance
    private Bitmap mPrefetchedBitmap;
    //Fragment Tasks for downloading the hint images
    private ImageDownloaderTaskFragment mCurrentBitmapTaskFragment;
    private ImageDownloaderTaskFragment mFutureBitmapTaskFragment;
    //CountDownLatchFragment Fragment that manages the CountDownTimer
    private CountDownLatchFragment mCountDownLatchFragment;
    //Stores the activity state
    private String mActivityStateStr;
    //Monitors the RadioButtons added in the layout "R.id.option_container_area_id"
    private ArrayList<RadioButton> mVirtualRadioGrpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.i(TAG, "onCreate: Started");

        //Setting the onCreate flag to TRUE
        onCreateInvoked = true;

        if (savedInstanceState == null) {
            //If loading for the first time

            Log.i(TAG, "onCreate: Started, launching for the first time");

            //Retrieving the list of questions from Array resource "question_array"
            mQuestionArray = getResources().getStringArray(R.array.question_array);
            mTotalNumberOfQuestions = mQuestionArray.length;

            //Retrieving the number of questions to load
            Intent welcomeIntent = getIntent();
            mNoOfQuestionsToLoad = welcomeIntent.getIntExtra(getString(R.string.total_question_opt_value), 1);

            //Setting the state of launch to True
            mFirstTimeLaunch = true;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        //Initializing the Fragment for Current Download Task: START
        mCurrentBitmapTaskFragment = (ImageDownloaderTaskFragment) fragmentManager.findFragmentByTag(ImageDownloaderTaskFragment.TAG_CURRENT_TASK);
        if (mCurrentBitmapTaskFragment == null) {
            //When the Fragment for the Current question download task is added for the first time

            //Initializing the {@link ImageDownloaderTaskFragment}
            mCurrentBitmapTaskFragment = new ImageDownloaderTaskFragment();

            //Adding the Fragment to this Activity
            fragmentManager.beginTransaction().add(mCurrentBitmapTaskFragment, ImageDownloaderTaskFragment.TAG_CURRENT_TASK).commit();
        }
        //Initializing the Fragment for Current Download Task: END

        //Initializing the Fragment for Future Download Task: START
        mFutureBitmapTaskFragment = (ImageDownloaderTaskFragment) fragmentManager.findFragmentByTag(ImageDownloaderTaskFragment.TAG_FUTURE_TASK);
        if (mFutureBitmapTaskFragment == null) {
            //When the Fragment for the Future question download task is added for the first time

            //Initializing the {@link ImageDownloaderTaskFragment}
            mFutureBitmapTaskFragment = new ImageDownloaderTaskFragment();

            //Adding the Fragment to this Activity
            fragmentManager.beginTransaction().add(mFutureBitmapTaskFragment, ImageDownloaderTaskFragment.TAG_FUTURE_TASK).commit();
        }
        //Initializing the Fragment for Future Download Task: END

        //Initializing the Fragment for CountDownTimer: START
        mCountDownLatchFragment = (CountDownLatchFragment) fragmentManager.findFragmentByTag(CountDownLatchFragment.TAG);
        if (mCountDownLatchFragment == null) {
            //When the Fragment is being added for the first time

            //Initializing the {@link CountDownLatchFragment}
            mCountDownLatchFragment = new CountDownLatchFragment();

            //Adding the Fragment to this Activity
            fragmentManager.beginTransaction().add(mCountDownLatchFragment, CountDownLatchFragment.TAG).commit();

            //Calculating the timer value to be set (45 Seconds for each question)
            long millisUntilFinished = TimeUnit.SECONDS.toMillis(mNoOfQuestionsToLoad * 45);

            //Setting the CountDownTimer timer start value
            mCountDownLatchFragment.setStartTimeInMillis(millisUntilFinished);

        }
        //Initializing the Fragment for CountDownTimer: END

        //Retrieving the Views that will be accessed frequently: START
        mQuestionNumberTextView = findViewById(R.id.question_no_id);
        mTotalScoreTextView = findViewById(R.id.total_score_id);
        mQuestionTextView = findViewById(R.id.question_text_id);
        mHintTextView = findViewById(R.id.hint_text_box_id);
        mSubmitButtonView = findViewById(R.id.submit_button_id);
        mShowHintButtonView = findViewById(R.id.show_hint_button_id);
        mImageRedirectButtonView = findViewById(R.id.image_redirect_button_id);
        mOptionContainerView = findViewById(R.id.option_container_area_id);
        mScrollableContentView = findViewById(R.id.vscroll_content_area_id);
        mHintContentView = findViewById(R.id.hint_content_box_id);
        mHintImageView = findViewById(R.id.hint_image_id);
        mCountDownTextView = findViewById(R.id.count_down_text_id);
        //Retrieving the Views that will be accessed frequently: END

        //Initializing the RadioButton Monitor ArrayList
        mVirtualRadioGrpList = new ArrayList<>();

        //Adding Click Listeners on Buttons
        setClickListenersOnButtons();

    }

    //Called when the Activity is beginning to start
    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "onStart: Started");

        //Initializing components only when it was launched for the first time
        if (mFirstTimeLaunch) {
            init();
        }

    }

    //Called by the Activity after Start when the activity is being reinitialized
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG, "onRestoreInstanceState");

        mFirstTimeLaunch = savedInstanceState.getBoolean(FIRST_TIME_LAUNCH_BOOL_KEY);
        mCurrentQuestionNo = savedInstanceState.getInt(CURRENT_QUESTION_NO_INT_KEY);
        mCurrentQuestionIndex = savedInstanceState.getInt(CURRENT_QUESTION_INDEX_INT_KEY);
        mQuestionArray = savedInstanceState.getStringArray(QUESTION_STR_ARRAY_KEY);
        mQuestionIndexOrderArray = savedInstanceState.getStringArray(QUESTION_INDEX_ORDER_STR_ARRAY_KEY);
        mOptionArray = savedInstanceState.getStringArray(OPTION_STR_ARRAY_KEY);
        mHintArray = savedInstanceState.getStringArray(HINT_STR_ARRAY_KEY);
        mOptionIndexOrderStr = savedInstanceState.getString(OPTION_INDEX_ORDER_STR_KEY);
        mSelectedAnswerList = savedInstanceState.getStringArrayList(SELECTED_ANSWER_STR_LIST_KEY);
        mSelectedIndexOrderStr = savedInstanceState.getString(SELECTED_INDEX_ORDER_STR_KEY);
        mCorrectAnswerList = savedInstanceState.getStringArrayList(CORRECT_ANSWER_STR_LIST_KEY);
        mTotalNumberOfQuestions = savedInstanceState.getInt(TOTAL_QUESTIONS_INT_KEY);
        mNoOfQuestionsToLoad = savedInstanceState.getInt(QUESTIONS_TO_LOAD_INT_KEY);
        mFutureQuestionIndex = savedInstanceState.getInt(FUTURE_QUESTION_INDEX_INT_KEY);
        mHintButtonState = savedInstanceState.getBoolean(HINT_BUTTON_STATE_BOOL_KEY);
        mHintButtonPressed = savedInstanceState.getBoolean(HINT_BUTTON_PRESSED_BOOL_KEY);
        mIsTextualQuestion = savedInstanceState.getBoolean(TEXTUAL_QUESTION_BOOL_KEY);
        mTextualUserInputStr = savedInstanceState.getString(TEXTUAL_USER_INPUT_STR_KEY);
        mActivityStateStr = savedInstanceState.getString(ACTIVITY_STATE_STR_KEY);

        //Retrieving the score
        int currentUserScore = savedInstanceState.getInt(CURRENT_USER_SCORE_INT_KEY);

        //Retrieving the submit button state
        String submitButtonStateStr = savedInstanceState.getString(SUBMIT_BUTTON_STATE_STR_KEY);

        //reinitializing the screen components for current question
        reloadCurrentQuestion(currentUserScore, submitButtonStateStr);
    }

    //Called by the Activity when it is prepared to be shown
    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume: Started");

        //Setting the activity state to Active
        mActivityStateStr = QuizActivityState.ACTIVE.toString();

        //Retrieving the ProgressBar Dialog Fragment
        ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG_PROGRESS_DIALOG);

        //Updating the CountDownTimer: START
        FragmentManager fragmentManager = getSupportFragmentManager();
        mCountDownLatchFragment = (CountDownLatchFragment) fragmentManager.findFragmentByTag(CountDownLatchFragment.TAG);

        //Retrieving the remaining time in millis
        long millisUntilFinished = mCountDownLatchFragment.getRemainingTimeInMillis();

        //Updating the Timer Text field "R.id.count_down_text_id"
        updateTimerText(millisUntilFinished);

        if (mDownloadedBitmap != null) {
            //When the Current Image is already downloaded

            if (progressDialogFragment != null) {
                //When the ProgressBar Dialog is active, dismiss the Dialog and resume the timer
                dismissProgressDialog();
            } else if (mCountDownLatchFragment.getTimerState().equals(CountDownLatchFragment.TimerState.INACTIVE.toString())
                    && !mSubmitButtonView.getText().equals(getString(R.string.finish_button_text))) {
                //If the ProgressBar Dialog is Inactive and the state of the CountDownTimer is Inactive
                //with the Submit button Text not in FINISH
                //then resume the CountDownTimer
                mCountDownLatchFragment.resumeTimer();
            }
        }

        //Ending the Quiz if time has already elapsed
        if (millisUntilFinished == 0) {
            showScoreSummary(true);
        } else {
            //When the quiz time has not yet elapsed
            if (progressDialogFragment == null
                    && mCountDownLatchFragment.getTimerState().equals(CountDownLatchFragment.TimerState.ACTIVE.toString())) {
                //Updating the timer image to timer_start when there is no active progress dialog
                //and when the state of the timer is ACTIVE
                mCountDownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer, 0, 0, 0);
            }
        }
        //Updating the CountDownTimer: END

        if (progressDialogFragment == null
                && !onCreateInvoked
                && !mSubmitButtonView.getText().equals(getString(R.string.finish_button_text))) {
            //When the Progress Dialog is not active and the activity resumed from onStart (not onCreate)
            //with the Submit button Text not in FINISH
            //then display the below funny toast message
            Toast.makeText(this, R.string.app_resumed_toast_text, Toast.LENGTH_SHORT).show();
        }

    }

    //Called by the Activity when the activity loses focus
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        //Setting the activity state to Inactive
        mActivityStateStr = QuizActivityState.INACTIVE.toString();

        if (isFinishing()) {
            Log.i(TAG, "onPause: isFinishing");

            //Clearing the Bitmap Cache when the activity is finishing
            BitmapImageCache.clearCache();
        }

    }

    //Called by the Activity before Stop, to save the activity's state in the Bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");

        outState.putStringArray(QUESTION_STR_ARRAY_KEY, mQuestionArray);
        outState.putStringArray(OPTION_STR_ARRAY_KEY, mOptionArray);
        outState.putStringArray(HINT_STR_ARRAY_KEY, mHintArray);
        outState.putStringArray(QUESTION_INDEX_ORDER_STR_ARRAY_KEY, mQuestionIndexOrderArray);
        outState.putString(OPTION_INDEX_ORDER_STR_KEY, mOptionIndexOrderStr);
        outState.putStringArrayList(SELECTED_ANSWER_STR_LIST_KEY, mSelectedAnswerList);
        outState.putString(SELECTED_INDEX_ORDER_STR_KEY, mSelectedIndexOrderStr);
        outState.putStringArrayList(CORRECT_ANSWER_STR_LIST_KEY, mCorrectAnswerList);
        outState.putInt(TOTAL_QUESTIONS_INT_KEY, mTotalNumberOfQuestions);
        outState.putInt(QUESTIONS_TO_LOAD_INT_KEY, mNoOfQuestionsToLoad);
        outState.putBoolean(FIRST_TIME_LAUNCH_BOOL_KEY, mFirstTimeLaunch);
        outState.putInt(CURRENT_QUESTION_NO_INT_KEY, mCurrentQuestionNo);
        outState.putInt(CURRENT_USER_SCORE_INT_KEY, getUserScore());
        outState.putInt(CURRENT_QUESTION_INDEX_INT_KEY, mCurrentQuestionIndex);
        outState.putInt(FUTURE_QUESTION_INDEX_INT_KEY, mFutureQuestionIndex);
        outState.putBoolean(HINT_BUTTON_STATE_BOOL_KEY, mHintButtonState);
        outState.putBoolean(HINT_BUTTON_PRESSED_BOOL_KEY, mHintButtonPressed);
        outState.putBoolean(TEXTUAL_QUESTION_BOOL_KEY, mIsTextualQuestion);
        outState.putString(TEXTUAL_USER_INPUT_STR_KEY,
                (mTextOptionView == null) ? "" : mTextOptionView.getText().toString());
        outState.putString(ACTIVITY_STATE_STR_KEY, mActivityStateStr);
        outState.putString(SUBMIT_BUTTON_STATE_STR_KEY, mSubmitButtonView.getText().toString());

        super.onSaveInstanceState(outState);
    }

    //Called by the Activity when the activity is about to close
    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "onStop");

        //Resetting the flag to false
        onCreateInvoked = false;
    }

    /**
     * Method to initialize the view components for the first question
     */
    private void init() {
        Log.i(TAG, "init: Started");

        //Retrieving the questions order
        String questionOrderStr = randomize(mTotalNumberOfQuestions, mNoOfQuestionsToLoad);

        Log.i(TAG, "init: QuestionOrder: " + questionOrderStr);

        //Initializing the header text score
        mTotalScoreTextView.setText(getString(R.string.current_score_format, 0, mNoOfQuestionsToLoad));

        //Initializing the integer array of question order
        mQuestionIndexOrderArray = questionOrderStr.split(";");

        //Preparing the screen components for the first question
        loadNextQuestion();

        //Resetting the state of launch to False
        mFirstTimeLaunch = false;
    }

    /**
     * Method to randomize the order of questions/options
     *
     * @param length    is the max number of the questions/(or) options in a question
     * @param sublength is the number of the questions/(or) options in a question
     * @return String containing the index order of questions/options
     * where each index is separated by ';' and each index is unique
     */
    private String randomize(int length, int sublength) {
        //StringBuilder to build the random order of questions/options
        StringBuilder randomOrderBuilder = new StringBuilder();
        //Initializing Random number generator
        Random randomNumber = new Random();

        for (int i = 0; i < sublength; i++) {
            int number = randomNumber.nextInt(length);

            //Verifying whether the number was previously generated
            if (Arrays.asList(randomOrderBuilder.toString().split(";")).contains(String.valueOf(number))) {
                i--; //decrementing to repeat
                continue; //repeating the loop
            }

            randomOrderBuilder.append(number);
            if ((i + 1) < sublength) {
                //appending ';' till the last number is generated
                randomOrderBuilder.append(";");
            }

        }

        //returning the random numbers generated
        return randomOrderBuilder.toString();
    }

    /**
     * Method that reloads the screen components for the current question after configuration change
     *
     * @param currentUserScore     is the current Score of the User
     * @param submitButtonStateStr contains the text shown on Submit Button "R.id.submit_button_id"
     */
    private void reloadCurrentQuestion(int currentUserScore, String submitButtonStateStr) {
        //updating the question number being displayed
        mQuestionNumberTextView.setText(getString(R.string.current_question_number_format, mCurrentQuestionNo, mNoOfQuestionsToLoad));

        //updating the score
        mTotalScoreTextView.setText(getString(R.string.current_score_format, currentUserScore, mNoOfQuestionsToLoad));

        //reinitializing the Question
        initializeQuestion();

        //reinitializing the Current Question image if already downloaded
        if (mHintArray[0].startsWith("res")) {
            //When the path starts as "res", then the image is located under res directory

            int startIndex = mHintArray[0].lastIndexOf("/");
            int endIndex = mHintArray[0].lastIndexOf(".");
            int imageResourceId = getResources().getIdentifier(mHintArray[0].substring(startIndex + 1, endIndex), "drawable", getPackageName());
            mDownloadedBitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);

        } else if (mCurrentBitmapTaskFragment.getDownloadTaskState(mCurrentQuestionIndex)
                .equals(ImageDownloaderTaskFragment.TaskState.TASK_STATE_COMPLETED.toString())) {
            //When the path starts as "http", then the image will be downloaded by the fragment
            mDownloadedBitmap = mCurrentBitmapTaskFragment.getDownloadedBitmap(mCurrentQuestionIndex);
        }

        //reinitializing the Future Question image if already downloaded
        if (mFutureBitmapTaskFragment.getDownloadTaskState(mFutureQuestionIndex)
                .equals(ImageDownloaderTaskFragment.TaskState.TASK_STATE_COMPLETED.toString())) {
            mPrefetchedBitmap = mFutureBitmapTaskFragment.getDownloadedBitmap(mFutureQuestionIndex);
        }

        //reinitializing the Options: START
        if (mIsTextualQuestion) {
            //Adding the EditText view, when it is Textual Based Question
            if (mTextualUserInputStr != null && mTextualUserInputStr.length() > 0) {
                //When the User had already entered the text
                addNewEditTextOption(mOptionArray[0], mTextualUserInputStr);
            } else {
                //When the User had not entered any text
                addNewEditTextOption(mOptionArray[0], null);
            }

        } else {
            //When it is not a Textual Based Question

            //Retrieving the count of Answer Keys
            int noOfKeys = mCorrectAnswerList.size();

            //Retrieving options
            String[] optionIndexOrderArray = mOptionIndexOrderStr.split(";");

            //Retrieving the current screen orientation
            int screenOrientation = getResources().getConfiguration().orientation;

            Log.i(TAG, "reloadCurrentQuestion: screenOrientation: " + screenOrientation);

            if (noOfKeys > 1) {
                //When the Number of Answers is more than 1, then the question is CheckBox Option based
                addNewCheckBoxOptions(optionIndexOrderArray, screenOrientation);
            } else if (noOfKeys == 1) {
                //When the Number of Answers is equal to 1, then the question is RadioButton Option based
                addNewRadioButtonOptions(optionIndexOrderArray, screenOrientation);
            }

            //Highlighting the Selected Answers if any: START
            if (mSelectedAnswerList.size() > 0) {
                //Retrieving the Selected options
                String[] selectedOptionOrderArray = mSelectedIndexOrderStr.split(";");

                //Declaring the CompoundButton
                CompoundButton buttonView = null;

                //Clearing the value as it will be regenerated through listeners
                mSelectedIndexOrderStr = "";

                //Iterating and marking the selected options as checked
                for (String selectedOptionOrderStr : selectedOptionOrderArray) {
                    int optionIndex = Integer.parseInt(selectedOptionOrderStr);

                    switch (optionIndex) {
                        case 1:
                            buttonView = findViewById(R.id.first_option);
                            break;
                        case 2:
                            buttonView = findViewById(R.id.second_option);
                            break;
                        case 3:
                            buttonView = findViewById(R.id.third_option);
                            break;
                        case 4:
                            buttonView = findViewById(R.id.fourth_option);
                            break;
                    }

                    if (buttonView != null) {
                        buttonView.setChecked(true);
                    }
                }

            }
            //Highlighting the Selected Answers if any: END

        }
        //reinitializing the Options: END

        //Enabling the Hint button components if Hint button was enabled
        if (mHintButtonState) {
            enableHintButtonComponents();
        } else {
            disableHintButtonComponents();
        }

        //Reveal the Hint if Hint button was pressed
        if (mHintButtonPressed) {
            //Setting the Hint text in the view "R.id.hint_text_box_id"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mHintTextView.setText(Html.fromHtml(mHintArray[1], Html.FROM_HTML_MODE_LEGACY));
            } else {
                mHintTextView.setText(Html.fromHtml(mHintArray[1]));
            }
            //Revealing the Hint image if downloaded successfully
            if (mDownloadedBitmap != null) {
                //When the image was downloaded successfully
                mHintImageView.setImageBitmap(mDownloadedBitmap);
            }
        }

        //Updating the Submit Button Text
        mSubmitButtonView.setText(submitButtonStateStr);

        //Revealing the answers when the Submit button is not in Submit state
        if (!submitButtonStateStr.equalsIgnoreCase(getString(R.string.submit_button_text))) {
            revealAnswers();

            //When in Next/Finish state, show the Image as the question is already answered
            if (mDownloadedBitmap != null) {
                //When the image was downloaded successfully
                mHintImageView.setImageBitmap(mDownloadedBitmap);
            }

            //Disabling the Options when in Next/Finish state
            disableOptions();
        }

    }

    /**
     * Method that prepares the screen components for the next question
     */
    private void loadNextQuestion() {

        //initializing the current question index
        mCurrentQuestionIndex = Integer.parseInt(mQuestionIndexOrderArray[mCurrentQuestionNo]);

        //Displaying Progress Dialog for first question
        if (mCurrentQuestionNo == 0) {
            showProgressDialog(R.string.progress_dialog_initial_loading_text);
        }

        //incrementing question number
        mCurrentQuestionNo++;

        //updating the question number being displayed
        mQuestionNumberTextView.setText(getString(R.string.current_question_number_format, mCurrentQuestionNo, mNoOfQuestionsToLoad));

        //initializing the Question
        initializeQuestion();

        //initializing the Hints
        initializeHints();

        //initializing the Keys
        initializeKeys();

        //Clearing the RadioButton Monitor ArrayList
        mVirtualRadioGrpList.clear();

        //initializing the Options
        initializeOptions();

        //initializing the Selected Answers
        if (mSelectedAnswerList == null) {
            mSelectedAnswerList = new ArrayList<>();
        } else if (mSelectedAnswerList.size() > 0) {
            mSelectedAnswerList.clear();
        }
        mSelectedIndexOrderStr = "";

        //disabling the components related to hint button
        disableHintButtonComponents();

        //resetting the state of hint button pressed to False
        mHintButtonPressed = false;

        //reloading the default hint image
        mHintImageView.setImageResource(R.drawable.hidden_image);

        if (mCurrentQuestionNo < mNoOfQuestionsToLoad) {
            //Prefetching hint image of Next question
            prefetchNextHintImage();
        } else {
            //Setting to 0 when there is no Next question
            mFutureQuestionIndex = 0;
        }

    }

    /**
     * Method that initializes the question component "question_text_id"
     */
    private void initializeQuestion() {

        //Retrieving and updating the question
        String currentQuestionStr = mQuestionArray[mCurrentQuestionIndex];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mQuestionTextView.setText(Html.fromHtml(currentQuestionStr, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mQuestionTextView.setText(Html.fromHtml(currentQuestionStr));
        }

        Log.i(TAG, "initializeQuestion: mQuestionTextView: " + mQuestionTextView.getText());

    }

    /**
     * Method that initializes the Key Answers for the current Question
     */
    private void initializeKeys() {

        //Retrieving the Answer Keys Array
        String[] keysArray = getStringArrayResourceByName("key", mCurrentQuestionIndex);

        //Initializing the ArrayList
        if (mCorrectAnswerList == null) {
            mCorrectAnswerList = new ArrayList<>();
        } else if (mCorrectAnswerList.size() > 0) {
            mCorrectAnswerList.clear();
        }

        //loading the Answer Keys to the list
        mCorrectAnswerList.addAll(Arrays.asList(keysArray));
    }

    /**
     * Method that initializes the Options available for the current Question.
     * This dynamically generates components (Checkboxes, RadioButtons, EditText)
     * in the LinearLayout "option_container_area_id" based on the number of Options
     * and Keys available for the Question.
     */
    private void initializeOptions() {

        //Retrieving the Options Array
        mOptionArray = getStringArrayResourceByName("option", mCurrentQuestionIndex);

        int noOfOptions = mOptionArray.length; //Number of Options
        int noOfKeys = mCorrectAnswerList.size(); //Number of Answer Keys

        //Retrieving the properties of the Options Layout Container "R.id.option_container_area_id"
        Bundle bundleOptionsData = getOptionContainerProperties();

        //Retrieving the count of child views present
        int existingChildCount = bundleOptionsData.getInt(EXISTING_CHILD_COUNT_INT_KEY);

        //Retrieving the child component is CheckBox/RadioButton/EditText
        String existingTypeStr = "" + bundleOptionsData.getString(EXISTING_CHILD_TYPE_STR_KEY);

        if (noOfOptions > 1) {
            //When the Number of Options are more than 1 (else it is a text based question)

            //Setting the textual question boolean to False, and its component to null
            if (mIsTextualQuestion) {
                mIsTextualQuestion = false;
                mTextOptionView = null;
            }

            //Retrieving options in random order
            mOptionIndexOrderStr = randomize(noOfOptions, noOfOptions);
            String[] optionIndexOrderArray = mOptionIndexOrderStr.split(";");

            //Retrieving the current screen orientation
            int screenOrientation = getResources().getConfiguration().orientation;

            if (noOfKeys > 1) {
                //When Answer Keys are more than 1 then it is Checkbox based

                if (existingTypeStr.equals(CheckBox.class.getSimpleName())) {
                    //When the child views are CheckBox views, the views will be reused

                    //Modifying existing CheckBox views and adding new ones when required
                    reuseAddCheckBoxOptions(optionIndexOrderArray, screenOrientation, existingChildCount);

                } else {
                    //When the child views are Not CheckBox views /(or) no view present,
                    //then the views will be created

                    addNewCheckBoxOptions(optionIndexOrderArray, screenOrientation);

                }

            } else if (noOfKeys == 1) {
                //When Answer Keys are equal to 1 then it is RadioButton based

                if (existingTypeStr.equals(RadioButton.class.getSimpleName())) {
                    //When the child views are RadioButton views, the views will be reused

                    //Modifying existing RadioButton views and adding new ones when required
                    reuseAddRadioButtonOptions(optionIndexOrderArray, screenOrientation, existingChildCount);

                } else {
                    //When the child views are Not RadioButton views /(or) no view present,
                    //then the views will be created

                    addNewRadioButtonOptions(optionIndexOrderArray, screenOrientation);

                }

            }

        } else {
            //When the Number of Options are equal to 1, it is a text based question

            //Setting the textual question boolean to True
            mIsTextualQuestion = true;

            //Clearing the Previous User Input value (if any)
            mTextualUserInputStr = "";

            //Retrieving the hint text from the option
            String hintTextStr = mOptionArray[0];

            if (existingChildCount == 1
                    && existingTypeStr.equals(EditText.class.getSimpleName())) {
                //When the child count is 1 and is EditText

                View childView = mOptionContainerView.getChildAt(0);
                mTextOptionView = (EditText) childView;

                //Clearing the Text
                mTextOptionView.setText("");

                //Setting the hint text
                mTextOptionView.setHint(hintTextStr);

                //Ensuring the EditText is enabled
                mTextOptionView.setEnabled(true);

            } else {
                //When the Child View is not an EditText view,
                //Then the view will be created

                //Creating and adding the EditText Option field
                addNewEditTextOption(hintTextStr, null);

            }

        }

    }

    /**
     * Method that creates a EditText option, adds properties and attaches the layout "R.id.option_container_area_id"
     *
     * @param hintTextStr    is the Hint Text to be shown on the EditText
     * @param textContentStr is the Text Content to be shown on the EditText, if any. Can be Null
     */
    private void addNewEditTextOption(String hintTextStr, @Nullable String textContentStr) {

        //Removing all the child views present
        mOptionContainerView.removeAllViews();

        //Initializing EditText View
        mTextOptionView = new EditText(this);

        //Setting the Id
        mTextOptionView.setId(R.id.text_option);

        //Setting the hint text
        mTextOptionView.setHint(hintTextStr);

        //Setting the hint text tint
        mTextOptionView.setHintTextColor(ContextCompat.getColor(this, R.color.textOptionHintTextLightGreen500));

        //Setting the InputType
        mTextOptionView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        //Setting the background
        mTextOptionView.setBackground(ContextCompat.getDrawable(this, R.drawable.textual_input_box_shape));

        //Setting the TextColor
        mTextOptionView.setTextColor(Color.BLACK);

        //Setting the TextSize
        mTextOptionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.question_option_text_size));

        //Setting the Content Text if passed
        if (textContentStr != null) {
            mTextOptionView.setText(textContentStr);
        }

        //Setting the LayoutParams for the EditText
        RelativeLayout.LayoutParams editTextLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.text_option_top_margin);
        editTextLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.text_option_bottom_margin);
        editTextLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.text_option_left_margin);
        editTextLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.text_option_right_margin);

        //Setting the Gravity
        mTextOptionView.setGravity(Gravity.CENTER);

        //adding to the layout
        mOptionContainerView.addView(mTextOptionView, editTextLayoutParams);
    }

    /**
     * Method that re-uses the existing RadioButton Option Views and adds extra RadioButtons if required,
     * to the layout "R.id.option_container_area_id" based on the current screen orientation
     *
     * @param optionIndexOrderArray is the randomized order of option index generated
     * @param screenOrientation     is the Integer value of the current screen orientation
     * @param existingChildCount    is the Existing Count of Child Option Views before modifying the layout
     */
    private void reuseAddRadioButtonOptions(String[] optionIndexOrderArray, int screenOrientation, int existingChildCount) {

        int noOfOptions = optionIndexOrderArray.length;

        //Modifying existing RadioButtons and adding new ones when required: START
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //When the device was in portrait mode

            RadioGroup radioGroupView = (RadioGroup) mOptionContainerView.getChildAt(0);

            for (int i = 0; i < noOfOptions; i++) {
                //Declaring a RadioButton view
                RadioButton radioButton = null;

                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if (i < existingChildCount) {
                    //Modifying any existing ones

                    //Retrieving the existing RadioButton view
                    switch (i) {
                        case 0:
                            radioButton = radioGroupView.findViewById(R.id.first_option);
                            break;
                        case 1:
                            radioButton = radioGroupView.findViewById(R.id.second_option);
                            break;
                        case 2:
                            radioButton = radioGroupView.findViewById(R.id.third_option);
                            break;
                        case 3:
                            radioButton = radioGroupView.findViewById(R.id.fourth_option);
                            break;
                    }

                    if (radioButton != null) {
                        if (radioButton.isChecked()) {
                            //Clearing previous selection if any
                            radioButton.setChecked(false);
                        }

                        //Ensuring the RadioButton is enabled
                        radioButton.setEnabled(true);

                        //Setting Option Text and other Button properties
                        setOptionButtonProperties(radioButton, optionTextStr);
                    }

                } else {
                    //Adding new RadioButton views when required

                    //Creating the RadioButton Option
                    radioButton = createRadioButtonOption(optionTextStr);

                    //Setting RadioButton LayoutParams
                    RadioGroup.LayoutParams radBtnLayoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);

                    //Setting the RadioButton id and Margins: START
                    switch (i) {
                        case 0:
                            radioButton.setId(R.id.first_option);
                            break;
                        case 1:
                            radioButton.setId(R.id.second_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            break;
                        case 2:
                            radioButton.setId(R.id.third_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            break;
                        case 3:
                            radioButton.setId(R.id.fourth_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            break;
                    }
                    //Setting the RadioButton id and Margins: END

                    //adding to the RadioGroup
                    radioGroupView.addView(radioButton, radBtnLayoutParams);

                }

                Log.i(TAG, "reuseAddRadioButtonOptions: radioButton: " + (radioButton != null ? radioButton.getText() : null));
            }

            //Removing any extra child views
            if (noOfOptions < existingChildCount) {
                while (existingChildCount > noOfOptions) {
                    radioGroupView.removeViewAt(noOfOptions);
                    existingChildCount--;
                }
            }

        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //When the device was in landscape mode

            TableLayout tableLayout = mOptionContainerView.findViewById(R.id.option_table_layout);

            //Declaring a TableRow to be used
            TableRow tableRow = null;

            for (int i = 0; i < noOfOptions; i++) {
                //Declaring a RadioButton view
                RadioButton radioButton = null;

                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if (i < existingChildCount) {
                    //Modifying any existing ones

                    //Retrieving the existing RadioButton view
                    switch (i) {
                        case 0:
                            radioButton = tableLayout.findViewById(R.id.first_option);
                            break;
                        case 1:
                            radioButton = tableLayout.findViewById(R.id.second_option);
                            break;
                        case 2:
                            radioButton = tableLayout.findViewById(R.id.third_option);
                            break;
                        case 3:
                            radioButton = tableLayout.findViewById(R.id.fourth_option);
                            break;
                    }

                    if (radioButton != null) {
                        //Clearing previous selection if any
                        if (radioButton.isChecked()) {
                            radioButton.setChecked(false);
                        }

                        //Ensuring the RadioButton is enabled
                        radioButton.setEnabled(true);

                        //Setting Option Text and other Button properties
                        setOptionButtonProperties(radioButton, optionTextStr);

                        if (radioButton.getParent() instanceof TableRow) {
                            //Retrieving the TableRow
                            tableRow = (TableRow) radioButton.getParent();
                        }
                    }

                } else {
                    //Adding new RadioButton views when required

                    if ((tableRow != null ? tableRow.getChildCount() : 0) == 2) {
                        //Adding new TableRow for every two RadioButtons added
                        tableRow = new TableRow(this);
                        //Setting TableRow LayoutParams
                        TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        tableLayout.addView(tableRow, tableRowLayoutParams);
                    }

                    //Creating the RadioButton Option
                    radioButton = createRadioButtonOption(optionTextStr);

                    //Setting RadioButton LayoutParams
                    TableRow.LayoutParams radBtnLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);

                    //Setting the RadioButton id and Margins: START
                    switch (i) {
                        case 0:
                            radioButton.setId(R.id.first_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            radBtnLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_left_margin);
                            radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                            break;
                        case 1:
                            radioButton.setId(R.id.second_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                            break;
                        case 2:
                            radioButton.setId(R.id.third_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            radBtnLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_left_margin);
                            radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                            break;
                        case 3:
                            radioButton.setId(R.id.fourth_option);
                            radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                            radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                            break;
                    }
                    //Setting the RadioButton id and Margins: END

                    if (tableRow != null) {
                        //Setting the bottom margin for the last TableRow
                        if ((i + 1) == noOfOptions) {
                            TableLayout.LayoutParams tableRowLayoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
                            tableRowLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_bottom_margin);
                        }

                        //Adding RadioButton to TableRow
                        tableRow.addView(radioButton, radBtnLayoutParams);
                    }

                }

                Log.i(TAG, "reuseAddRadioButtonOptions: radioButton: " + (radioButton != null ? radioButton.getText() : null));
            }

            //Removing any extra child views
            removeExtraChildViewsOnTable(existingChildCount, noOfOptions, tableLayout);

        }
        //Modifying existing RadioButtons and adding new ones when required: END

    }

    /**
     * Method that removes any extra options (than required) existing in the Table layout
     * under "R.id.option_container_area_id"
     *
     * @param existingChildCount is the Existing Count of Child Option Views before modifying the layout
     * @param noOfOptions        is the Number of Option Views that needs to be present
     * @param tableLayout        is the Table Layout containing the components
     */
    private void removeExtraChildViewsOnTable(int existingChildCount, int noOfOptions, TableLayout tableLayout) {

        if (noOfOptions < existingChildCount) {
            //Retrieving the current number of table rows
            int noOfTableRows = tableLayout.getChildCount();
            //Keeping track of the number of CheckBoxes/RadioButtons found
            int currentChildCount = 0;

            for (int i = 0; i < noOfTableRows; i++) {
                //Retrieving the Table Row
                TableRow childTableRow = (TableRow) tableLayout.getChildAt(i);
                //Accounting the number of CheckBoxes/RadioButtons in the current row
                currentChildCount += childTableRow.getChildCount();

                //When the current count of CheckBoxes/RadioButtons exceeds the required count
                while (currentChildCount > noOfOptions) {
                    //Removing the last child in the TableRow in each iteration
                    childTableRow.removeView(childTableRow.getChildAt((currentChildCount - 1) % 2));
                    currentChildCount--;
                }

                if (childTableRow.getChildCount() == 0) {
                    //If all the CheckBoxes/RadioButtons on the current Table Row were removed,
                    //then delete the Table Row and adjust the counter accordingly
                    tableLayout.removeView(childTableRow);
                    i--;
                    noOfTableRows--;
                }

            }

        }

    }

    /**
     * Method that adds New RadioButton Options to the layout "R.id.option_container_area_id"
     * based on the current screen orientation
     *
     * @param optionIndexOrderArray is the randomized order of option index generated
     * @param screenOrientation     is the Integer value of the current screen orientation
     */
    private void addNewRadioButtonOptions(String[] optionIndexOrderArray, int screenOrientation) {

        //Retrieving the count of child views present
        int childCount = mOptionContainerView.getChildCount();

        if (childCount > 0) {
            //Deleting any existing child views
            mOptionContainerView.removeAllViews();
        }

        int noOfOptions = optionIndexOrderArray.length;

        //adding RadioButton views dynamically to "R.id.option_container_area_id"
        //based on screen orientation
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //When the device is in portrait mode

            //Initializing a RadioGroup
            RadioGroup radioGroupView = new RadioGroup(this);
            radioGroupView.setId(R.id.rad_group_option);
            radioGroupView.setOrientation(RadioGroup.VERTICAL);
            radioGroupView.setGravity(Gravity.CENTER_HORIZONTAL);

            //Initializing Layout Params for RadioGroup
            RadioGroup.LayoutParams radGrpLayoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
            radGrpLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.rad_grp_left_margin);
            radGrpLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_grp_right_margin);
            radGrpLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_grp_top_margin);
            radGrpLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.rad_grp_bottom_margin);

            //adding RadioButton views dynamically
            for (int i = 0; i < noOfOptions; i++) {
                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                //Creating the RadioButton Option
                RadioButton radioButton = createRadioButtonOption(optionTextStr);

                //Setting RadioButton LayoutParams
                RadioGroup.LayoutParams radBtnLayoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);

                //Setting the RadioButton id and Margins: START
                switch (i) {
                    case 0:
                        radioButton.setId(R.id.first_option);
                        break;
                    case 1:
                        radioButton.setId(R.id.second_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        break;
                    case 2:
                        radioButton.setId(R.id.third_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        break;
                    case 3:
                        radioButton.setId(R.id.fourth_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        break;
                }
                //Setting the RadioButton id and Margins: END

                //adding to the RadioGroup
                radioGroupView.addView(radioButton, radBtnLayoutParams);

                Log.i(TAG, "addNewRadioButtonOptions: radioButton: " + radioButton.getText());
            }

            //adding RadioGroup to the layout
            mOptionContainerView.addView(radioGroupView, radGrpLayoutParams);

        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //When the device is in landscape mode

            //Creating a TableLayout
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setId(R.id.option_table_layout); //Setting component id

            //Marking all the columns stretchable
            tableLayout.setStretchAllColumns(true);

            //Setting Table Layout Params
            RelativeLayout.LayoutParams tableLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            //Declaring a TableRow to be used
            TableRow tableRow = null;

            //adding RadioButton views dynamically
            for (int i = 0; i < noOfOptions; i++) {
                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if ((i % 2) == 0) {
                    //Adding new TableRow for every two RadioButtons added
                    tableRow = new TableRow(this);
                    //Setting TableRow LayoutParams
                    TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    tableLayout.addView(tableRow, tableRowLayoutParams);
                }

                //Creating the RadioButton Option
                RadioButton radioButton = createRadioButtonOption(optionTextStr);

                //Setting RadioButton LayoutParams
                TableRow.LayoutParams radBtnLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);

                //Setting the RadioButton id and Margins: START
                switch (i) {
                    case 0:
                        radioButton.setId(R.id.first_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        radBtnLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_left_margin);
                        radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                        break;
                    case 1:
                        radioButton.setId(R.id.second_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                        break;
                    case 2:
                        radioButton.setId(R.id.third_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        radBtnLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_left_margin);
                        radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                        break;
                    case 3:
                        radioButton.setId(R.id.fourth_option);
                        radBtnLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_top_margin);
                        radBtnLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_right_margin);
                        break;
                }
                //Setting the RadioButton id and Margins: END

                //Setting the bottom margin for the last TableRow
                if ((i + 1) == noOfOptions) {
                    TableLayout.LayoutParams tableRowLayoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
                    tableRowLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.rad_btn_bottom_margin);
                }

                //Adding RadioButton to TableRow
                tableRow.addView(radioButton, radBtnLayoutParams);

                Log.i(TAG, "addNewRadioButtonOptions: radioButton: " + radioButton.getText());
            }

            //Adding TableLayout to "R.id.option_container_area_id"
            mOptionContainerView.addView(tableLayout, tableLayoutParams);

        }

    }

    /**
     * Method that Creates a RadioButton option, adds properties
     * and attaches a Listener
     *
     * @param optionTextStr is Text Content on the RadioButton to be shown
     * @return RadioButton prepared
     */
    private RadioButton createRadioButtonOption(String optionTextStr) {
        //Initializing a RadioButton view
        RadioButton radioButton = new RadioButton(this);

        //Setting Option Text and other Button properties
        setOptionButtonProperties(radioButton, optionTextStr);

        //Adding {@link android.widget.CompoundButton.OnCheckedChangeListener} to the button
        radioButton.setOnCheckedChangeListener(this);

        //Returning the RadioButton prepared
        return radioButton;
    }

    /**
     * Method that re-uses the existing CheckBox Option Views and adds extra CheckBoxes if required,
     * to the layout "R.id.option_container_area_id" based on the current screen orientation
     *
     * @param optionIndexOrderArray is the randomized order of option index generated
     * @param screenOrientation     is the Integer value of the current screen orientation
     * @param existingChildCount    is the Existing Count of Child Option Views before modifying the layout
     */
    private void reuseAddCheckBoxOptions(String[] optionIndexOrderArray, int screenOrientation, int existingChildCount) {

        int noOfOptions = optionIndexOrderArray.length;

        //Modifying existing CheckBox views and adding new ones when required: START
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //When the device was in portrait mode

            for (int i = 0; i < noOfOptions; i++) {
                //Declaring a CheckBox view
                CheckBox checkBox = null;

                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if (i < existingChildCount) {
                    //Modifying any existing ones

                    //Retrieving the existing CheckBox view
                    switch (i) {
                        case 0:
                            checkBox = mOptionContainerView.findViewById(R.id.first_option);
                            break;
                        case 1:
                            checkBox = mOptionContainerView.findViewById(R.id.second_option);
                            break;
                        case 2:
                            checkBox = mOptionContainerView.findViewById(R.id.third_option);
                            break;
                        case 3:
                            checkBox = mOptionContainerView.findViewById(R.id.fourth_option);
                            break;
                    }

                    if (checkBox != null) {
                        //Clearing previous selection if any
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }

                        //Ensuring the CheckBox is enabled
                        checkBox.setEnabled(true);

                        //Setting Option Text and other Button properties
                        setOptionButtonProperties(checkBox, optionTextStr);
                    }

                } else {
                    //Adding new CheckBox views when required

                    //Creating the CheckBox Option
                    checkBox = createCheckBoxOption(optionTextStr);

                    //Setting CheckBox LayoutParams
                    RelativeLayout.LayoutParams chkBoxLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                    chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);

                    //Setting the CheckBox id, relative positions and Margins: START
                    switch (i) {
                        case 0:
                            checkBox.setId(R.id.first_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                            break;
                        case 1:
                            checkBox.setId(R.id.second_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                            chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.first_option);
                            break;
                        case 2:
                            checkBox.setId(R.id.third_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                            chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.second_option);
                            break;
                        case 3:
                            checkBox.setId(R.id.fourth_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                            chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.third_option);
                            break;
                    }
                    //Setting the CheckBox id, relative positions and Margins: END

                    //Setting the bottom margin for the last CheckBox
                    if ((i + 1) == noOfOptions) {
                        chkBoxLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_bottom_margin);
                    }

                    //adding to the layout
                    mOptionContainerView.addView(checkBox, chkBoxLayoutParams);

                }

                Log.i(TAG, "reuseAddCheckBoxOptions: checkBox: " + (checkBox != null ? checkBox.getText() : null));
            }

            //Removing any extra child views
            if (noOfOptions < existingChildCount) {
                while (existingChildCount > noOfOptions) {
                    mOptionContainerView.removeViewAt(noOfOptions);
                    existingChildCount--;
                }
            }

        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //When the device was in landscape mode

            TableLayout tableLayout = mOptionContainerView.findViewById(R.id.option_table_layout);

            //Declaring a TableRow to be used
            TableRow tableRow = null;

            for (int i = 0; i < noOfOptions; i++) {
                //Declaring a CheckBox view
                CheckBox checkBox = null;

                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if (i < existingChildCount) {
                    //Modifying existing ones

                    //Retrieving the existing CheckBox view
                    switch (i) {
                        case 0:
                            checkBox = tableLayout.findViewById(R.id.first_option);
                            break;
                        case 1:
                            checkBox = tableLayout.findViewById(R.id.second_option);
                            break;
                        case 2:
                            checkBox = tableLayout.findViewById(R.id.third_option);
                            break;
                        case 3:
                            checkBox = tableLayout.findViewById(R.id.fourth_option);
                            break;
                    }

                    if (checkBox != null) {
                        //Clearing previous selection if any
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        }

                        //Ensuring the CheckBox is enabled
                        checkBox.setEnabled(true);

                        //Setting Option Text and other Button properties
                        setOptionButtonProperties(checkBox, optionTextStr);

                        if (checkBox.getParent() instanceof TableRow) {
                            //Retrieving the TableRow
                            tableRow = (TableRow) checkBox.getParent();
                        }
                    }

                } else {
                    //Adding new CheckBox views when required

                    if ((tableRow != null ? tableRow.getChildCount() : 0) == 2) {
                        //Adding new TableRow for every two CheckBoxes added
                        tableRow = new TableRow(this);
                        //Setting TableRow LayoutParams
                        TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        tableLayout.addView(tableRow, tableRowLayoutParams);
                    }

                    //Creating the CheckBox Option
                    checkBox = createCheckBoxOption(optionTextStr);

                    //Setting CheckBox LayoutParams
                    TableRow.LayoutParams chkBoxLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);

                    //Setting the CheckBox id, relative positions and Margins: START
                    switch (i) {
                        case 0:
                            checkBox.setId(R.id.first_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                            chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                            chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                            break;
                        case 1:
                            checkBox.setId(R.id.second_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                            chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                            break;
                        case 2:
                            checkBox.setId(R.id.third_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                            chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                            chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                            break;
                        case 3:
                            checkBox.setId(R.id.fourth_option);
                            chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                            chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                            break;
                    }
                    //Setting the CheckBox id, relative positions and Margins: END

                    if (tableRow != null) {
                        //Setting the bottom margin for the last TableRow
                        if ((i + 1) == noOfOptions) {
                            TableLayout.LayoutParams tableRowLayoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
                            tableRowLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_bottom_margin);
                        }

                        //adding to the TableRow
                        tableRow.addView(checkBox, chkBoxLayoutParams);
                    }

                }

                Log.i(TAG, "reuseAddCheckBoxOptions: checkBox: " + (checkBox != null ? checkBox.getText() : null));
            }

            //Removing any extra child views
            removeExtraChildViewsOnTable(existingChildCount, noOfOptions, tableLayout);

        }
        //Modifying existing CheckBox views and adding new ones when required: END

    }

    /**
     * Method that adds New CheckBox Options to the layout "R.id.option_container_area_id"
     * based on the current screen orientation
     *
     * @param optionIndexOrderArray is the randomized order of option index generated
     * @param screenOrientation     is the Integer value of the current screen orientation
     */
    private void addNewCheckBoxOptions(String[] optionIndexOrderArray, int screenOrientation) {

        //Retrieving the count of child views present
        int childCount = mOptionContainerView.getChildCount();

        if (childCount > 0) {
            //Deleting any existing child views
            mOptionContainerView.removeAllViews();
        }

        int noOfOptions = optionIndexOrderArray.length;

        //adding CheckBox views dynamically to "R.id.option_container_area_id"
        //based on screen orientation
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //When the device is in portrait mode

            for (int i = 0; i < noOfOptions; i++) {
                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                //Creating the CheckBox Option
                CheckBox checkBox = createCheckBoxOption(optionTextStr);

                //Setting CheckBox LayoutParams
                RelativeLayout.LayoutParams chkBoxLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);

                //Setting the CheckBox id, relative positions and Margins: START
                switch (i) {
                    case 0:
                        checkBox.setId(R.id.first_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                        break;
                    case 1:
                        checkBox.setId(R.id.second_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                        chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.first_option);
                        break;
                    case 2:
                        checkBox.setId(R.id.third_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                        chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.second_option);
                        break;
                    case 3:
                        checkBox.setId(R.id.fourth_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                        chkBoxLayoutParams.addRule(RelativeLayout.BELOW, R.id.third_option);
                        break;
                }
                //Setting the CheckBox id, relative positions and Margins: END

                //Setting the bottom margin for the last CheckBox
                if ((i + 1) == noOfOptions) {
                    chkBoxLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_bottom_margin);
                }

                //adding to the layout "R.id.option_container_area_id"
                mOptionContainerView.addView(checkBox, chkBoxLayoutParams);

                Log.i(TAG, "addNewCheckBoxOptions: checkBox: " + checkBox.getText());
            }

        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //When the device is in landscape mode

            //Creating a TableLayout
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setId(R.id.option_table_layout); //Setting component id

            //Marking all the columns stretchable
            tableLayout.setStretchAllColumns(true);

            //Setting Table Layout Params
            RelativeLayout.LayoutParams tableLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            //Declaring a TableRow to be used
            TableRow tableRow = null;

            for (int i = 0; i < noOfOptions; i++) {
                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionIndexOrderArray[i]);
                String optionTextStr = mOptionArray[optionIndex];

                if ((i % 2) == 0) {
                    //Adding new TableRow for every two CheckBoxes added
                    tableRow = new TableRow(this);
                    //Setting TableRow LayoutParams
                    TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    tableLayout.addView(tableRow, tableRowLayoutParams);
                }

                //Creating the CheckBox Option
                CheckBox checkBox = createCheckBoxOption(optionTextStr);

                //Setting CheckBox LayoutParams
                TableRow.LayoutParams chkBoxLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);

                //Setting the CheckBox id, relative positions and Margins: START
                switch (i) {
                    case 0:
                        checkBox.setId(R.id.first_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                        chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                        chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                        break;
                    case 1:
                        checkBox.setId(R.id.second_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_1);
                        chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                        break;
                    case 2:
                        checkBox.setId(R.id.third_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                        chkBoxLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_left_margin);
                        chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                        break;
                    case 3:
                        checkBox.setId(R.id.fourth_option);
                        chkBoxLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_top_margin_2);
                        chkBoxLayoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_right_margin);
                        break;
                }
                //Setting the CheckBox id, relative positions and Margins: END

                //Setting the bottom margin for the last TableRow
                if ((i + 1) == noOfOptions) {
                    TableLayout.LayoutParams tableRowLayoutParams = (TableLayout.LayoutParams) tableRow.getLayoutParams();
                    tableRowLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.chk_box_bottom_margin);
                }

                //adding to the TableRow
                tableRow.addView(checkBox, chkBoxLayoutParams);

                Log.i(TAG, "addNewCheckBoxOptions: checkBox: " + checkBox.getText());
            }

            //Adding TableLayout to "R.id.option_container_area_id"
            mOptionContainerView.addView(tableLayout, tableLayoutParams);

        }

    }

    /**
     * Method that Creates a CheckBox option, adds properties
     * and attaches a Listener
     *
     * @param optionTextStr is Text Content on the CheckBox to be shown
     * @return CheckBox prepared
     */
    private CheckBox createCheckBoxOption(String optionTextStr) {
        //Initializing a CheckBox view
        CheckBox checkBox = new CheckBox(this);

        //Setting Option Text and other Button properties
        setOptionButtonProperties(checkBox, optionTextStr);

        //Adding {@link android.widget.CompoundButton.OnCheckedChangeListener} to the button
        checkBox.setOnCheckedChangeListener(this);

        //Returning the CheckBox prepared
        return checkBox;
    }

    /**
     * Method that returns a Bundle containing data related to Options Container layout
     * "R.id.option_container_area_id"
     *
     * @return Bundle containing the properties describing the Options Container layout
     */
    private Bundle getOptionContainerProperties() {

        //Stores the count of components found (CheckBox/RadioButton/EditText)
        int childCount = 0;

        //Stores the type of components found
        String typeStr = "";

        //Looking for First CompoundButton option if present
        if (mOptionContainerView.findViewById(R.id.first_option) != null) {
            View view = mOptionContainerView.findViewById(R.id.first_option);

            //Retrieving the type
            //(Only for the first since it will be the same on other options
            if (view instanceof CheckBox) {
                typeStr = CheckBox.class.getSimpleName();
            } else if (view instanceof RadioButton) {
                typeStr = RadioButton.class.getSimpleName();
            }

            //Advancing count
            childCount++;
        }

        //Looking for Second CompoundButton option if present
        if (mOptionContainerView.findViewById(R.id.second_option) != null) {
            childCount++; //Advancing count
        }

        //Looking for Third CompoundButton option if present
        if (mOptionContainerView.findViewById(R.id.third_option) != null) {
            childCount++; //Advancing count
        }

        //Looking for Fourth CompoundButton option if present
        if (mOptionContainerView.findViewById(R.id.fourth_option) != null) {
            childCount++; //Advancing count
        }

        //Looking for EditText Component option if present
        if (childCount == 0 && mOptionContainerView.findViewById(R.id.text_option) != null) {
            typeStr = EditText.class.getSimpleName(); //Retrieving the type

            childCount++; //Advancing count
        }

        //Adding to the Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(EXISTING_CHILD_COUNT_INT_KEY, childCount);
        bundle.putString(EXISTING_CHILD_TYPE_STR_KEY, typeStr);

        //Returning the Bundle
        return bundle;
    }

    /**
     * Method that sets the Option Text and other CompoundButton Button related properties
     *
     * @param buttonView is either a RadioButton (or) CheckBox
     * @param optionStr  is the Text to be shown on the button
     */
    private void setOptionButtonProperties(CompoundButton buttonView, String optionStr) {
        //Setting the option value
        buttonView.setText(optionStr);

        //Setting the Text Color
        buttonView.setTextColor(Color.BLACK);

        //Setting Text Gravity
        buttonView.setGravity(Gravity.CENTER);

        //Initializing the Level List Drawable used for the Option background with Level as 0
        LevelListDrawable optionLevelListDrawable = (LevelListDrawable) ContextCompat.getDrawable(this, R.drawable.option_level_list);
        if (optionLevelListDrawable != null) {
            optionLevelListDrawable.setLevel(0);
            buttonView.setBackground(optionLevelListDrawable);
        }

        //Setting the Text Typeface
        buttonView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        //Setting the TextSize
        buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.question_option_text_size));
    }

    /**
     * Method that initializes the Answer Hints for the current Question
     */
    private void initializeHints() {
        Log.i(TAG, "initializeHints: QuestionIndex: " + mCurrentQuestionIndex);

        //Retrieving the Answer Hints Array
        mHintArray = getStringArrayResourceByName("hint", mCurrentQuestionIndex);

        Log.i(TAG, "initializeHints: CurrentHintImage: " + mHintArray[0]);

        //Resetting the Bitmap to null
        mDownloadedBitmap = null;

        //Loading the Hint image: START
        if (mPrefetchedBitmap != null) {
            //When the image is already prefetched during the last question
            mDownloadedBitmap = mPrefetchedBitmap;
            //Copying the Future Task Fragment to the Current Task Fragment
            mCurrentBitmapTaskFragment.copyToCurrent(mFutureBitmapTaskFragment);

        } else {
            //When the image is not prefetched, the image will be downloaded for the current question

            if (mHintArray[0].startsWith("res")) {
                //When the path starts as "res", then the image is located under res directory

                int startIndex = mHintArray[0].lastIndexOf("/");
                int endIndex = mHintArray[0].lastIndexOf(".");
                int imageResourceId = getResources().getIdentifier(mHintArray[0].substring(startIndex + 1, endIndex), "drawable", getPackageName());
                mDownloadedBitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);

                Log.i(TAG, "initializeHints: CurrentHintImage downloaded from local");

                //Dismiss Progress dialog if active
                dismissProgressDialog();

            } else if (mHintArray[0].startsWith("http")) {
                //When the path starts as "http", then the image is located in URL
                if (mHintArray[0].contains("%%")) {
                    //Correcting the URL retrieved from resource if it contains "%%"
                    mCurrentBitmapTaskFragment.executeCurrentTask(mCurrentQuestionIndex, mHintArray[0].replace("%%", "%"));
                } else {
                    mCurrentBitmapTaskFragment.executeCurrentTask(mCurrentQuestionIndex, mHintArray[0]);
                }
            }
        }
        //Loading the Hint image: END

    }

    /**
     * Method that loads the hint image for the next question in advance for faster loading
     */
    private void prefetchNextHintImage() {

        //Getting the next question index
        mFutureQuestionIndex = Integer.parseInt(mQuestionIndexOrderArray[mCurrentQuestionNo]);

        Log.i(TAG, "prefetchNextHintImage: NextQuestionIndex: " + mFutureQuestionIndex);

        //Retrieving the Answer Hints Array
        String[] hintArray = getStringArrayResourceByName("hint", mFutureQuestionIndex);

        Log.i(TAG, "prefetchNextHintImage: NextHintImage: " + hintArray[0]);

        //Resetting the Prefetched Bitmap to null
        mPrefetchedBitmap = null;

        //Loading the Hint image of the next question: START
        if (hintArray[0].startsWith("http")) {
            //When the path starts as "http", then the image is located in URL
            if (hintArray[0].contains("%%")) {
                //Correcting the URL retrieved from resource if it contains "%%"
                mFutureBitmapTaskFragment.executeFutureTask(mFutureQuestionIndex, hintArray[0].replace("%%", "%"));
            } else {
                mFutureBitmapTaskFragment.executeFutureTask(mFutureQuestionIndex, hintArray[0]);
            }
        }
        //Loading the Hint image of the next question: END

    }

    /**
     * Method that returns the String array retrieved from the array resource
     * through the identifier name generated using the lookup string
     * and the question index passed
     *
     * @param lookupStr     is the String that identifies the kind of the array.
     *                      Possible values are "option", "key", "hint"
     * @param questionIndex is the question index to be looked up in the list of questions loaded
     * @return String array of the identified resource
     */
    private String[] getStringArrayResourceByName(String lookupStr, int questionIndex) {

        //Preparing the identifier string to lookup
        String identifierStr = lookupStr + "_" + (questionIndex + 1) + "_array";
        //Retrieving the resource id
        int identifier = getResources().getIdentifier(identifierStr, "array", getPackageName());

        return getResources().getStringArray(identifier); //Returning the array retrieved
    }

    /**
     * Called when the checked state of a compound button (for option buttons) has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //Stores the index of the option being selected/unselected
        int optionIndex = 0;

        switch (buttonView.getId()) {
            case R.id.first_option:
                optionIndex = 1;
                break;
            case R.id.second_option:
                optionIndex = 2;
                break;
            case R.id.third_option:
                optionIndex = 3;
                break;
            case R.id.fourth_option:
                optionIndex = 4;
                break;
        }

        if (optionIndex > 0) {
            //When option index is present
            if (isChecked) {
                //Making necessary changes when selected
                addSelectedAnswer(buttonView, optionIndex);
            } else {
                //Making necessary changes when deselected
                removeSelectedAnswer(buttonView, optionIndex);
            }

        }

    }

    /**
     * Method that adds the selected answer to the ArrayList {@link .mSelectedAnswerList}
     * and the corresponding option index to the String {@link .mSelectedKeysOrderStr}
     *
     * @param buttonView  The compound button view whose state has changed.
     * @param optionIndex is the index of the option button being selected
     */
    private void addSelectedAnswer(CompoundButton buttonView, int optionIndex) {

        int noOfKeys = mCorrectAnswerList.size(); //Number of Answer Keys

        //Retrieving the selected answer
        String selectedAnswerText = buttonView.getText().toString();

        //Adding the selected answer to the list: START
        if (noOfKeys == 1) {
            //Clearing the list every time when the question is RadioButton based
            mSelectedAnswerList.clear();
            //Managing the RadioButton selections manually
            manageVirtualRadioGroup((RadioButton) buttonView);
        }

        if (!mSelectedAnswerList.contains(selectedAnswerText)) {
            //Avoiding duplicates
            mSelectedAnswerList.add(selectedAnswerText);
        }
        //Adding the selected answer to the list: END

        //Rebuilding the selected options order: START
        if (noOfKeys == 1) {
            //When the question is RadioButton based
            mSelectedIndexOrderStr = String.valueOf(optionIndex);
        } else if (noOfKeys > 1) {
            //When the question is CheckBox based
            if (mSelectedIndexOrderStr.trim().length() > 0) {
                //When multiple options were previously selected
                mSelectedIndexOrderStr = mSelectedIndexOrderStr + ";" + optionIndex;
            } else {
                //When NO option was previously selected
                mSelectedIndexOrderStr = String.valueOf(optionIndex);
            }

        }
        //Rebuilding the selected options order: END

        //Changing the drawable for the selection done
        LevelListDrawable optionLevelListDrawable = (LevelListDrawable) buttonView.getBackground();
        optionLevelListDrawable.setLevel(1);
    }

    /**
     * Method that removes the selected answer from the ArrayList {@link .mSelectedAnswerList}
     * and the corresponding option index from the String {@link .mSelectedKeysOrderStr}
     *
     * @param buttonView  The compound button view whose state has changed.
     * @param optionIndex is the index of the option button being deselected
     */
    private void removeSelectedAnswer(CompoundButton buttonView, int optionIndex) {

        int noOfKeys = mCorrectAnswerList.size(); //Number of Answer Keys

        //Retrieving the un-selected answer
        String unSelectedAnswerText = buttonView.getText().toString();

        //Removing the unselected answer from the list
        mSelectedAnswerList.remove(unSelectedAnswerText);

        //Rebuilding the selected options order: START
        if (noOfKeys > 1) {
            //Updating only when the question is CheckBox based
            String optionIndexStr = String.valueOf(optionIndex);

            if (mSelectedIndexOrderStr.contains(";")) {
                //When multiple options were previously selected

                if (mSelectedIndexOrderStr.endsWith(optionIndexStr)) {
                    mSelectedIndexOrderStr = mSelectedIndexOrderStr.replace(";" + optionIndexStr, "");
                } else {
                    mSelectedIndexOrderStr = mSelectedIndexOrderStr.replace(optionIndexStr + ";", "");
                }

            } else {
                //When only one option was previously selected
                mSelectedIndexOrderStr = "";
            }

        }
        //Rebuilding the selected options order: END

        //Resetting the drawable for the deselection done
        LevelListDrawable optionLevelListDrawable = (LevelListDrawable) buttonView.getBackground();
        optionLevelListDrawable.setLevel(0);
    }

    /**
     * Method called whenever the user selects a RadioButton in the options layout
     * to manage/monitor and clear the previous Radio Selections if any
     *
     * @param radioButton is the RadioButton that was selected now by the user
     */
    public void manageVirtualRadioGroup(RadioButton radioButton) {

        if (mVirtualRadioGrpList.size() > 0) {
            //If there are any RadioButtons that were previously selected

            //Creating a working copy of the current list of RadioButtons
            ArrayList<RadioButton> radioButtonList = new ArrayList<>(mVirtualRadioGrpList);

            //Iterating through the Main list and deselecting the RadioButton
            for (RadioButton prevRadioButton : mVirtualRadioGrpList) {
                if (prevRadioButton.getId() != radioButton.getId()) {
                    prevRadioButton.setChecked(false);
                    //Removing the RadioButton from the working copy
                    radioButtonList.remove(prevRadioButton);
                    //Resetting the drawable for the deselection done
                    LevelListDrawable optionLevelListDrawable = (LevelListDrawable) prevRadioButton.getBackground();
                    if (optionLevelListDrawable.getLevel() > 0) {
                        optionLevelListDrawable.setLevel(0);
                    }
                }
            }

            if (radioButtonList.size() < mVirtualRadioGrpList.size()) {
                //If the working copy contains less data, then overwrite the Main List

                //Clearing the Main List
                mVirtualRadioGrpList.clear();

                if (radioButtonList.size() > 0) {
                    //Adding the working copy to the Main List if any data in working copy
                    mVirtualRadioGrpList.addAll(radioButtonList);
                }

            }

        }

        //Checking to avoid any duplicate RadioButton from being added
        if (!mVirtualRadioGrpList.contains(radioButton)) {
            //Adding the current RadioButton that was selected to the Main List
            mVirtualRadioGrpList.add(radioButton);
        }

    }

    /**
     * Method that disables the view components related to Hint button
     */
    private void disableHintButtonComponents() {

        //Removing the layout for Hint when the Hint is disabled
        mHintContentView.setVisibility(View.GONE);

        //disabling the button for "Show Hint"
        mShowHintButtonView.setEnabled(false);

        //updating the state of hint button to False
        mHintButtonState = false;

    }

    /**
     * Method that enables the view components related to Hint button
     */
    private void enableHintButtonComponents() {

        //Adding the layout for Hint when the Hint is enabled
        mHintContentView.setVisibility(View.VISIBLE);

        //enabling the button for "Show Hint"
        mShowHintButtonView.setEnabled(true);

        //updating the state of hint button to True
        mHintButtonState = true;

        //Resetting the text shown in the Hint Text view
        mHintTextView.setText("");
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.submit_button_id:
                onSubmitButtonClicked();
                break;
            case R.id.show_hint_button_id:
                onShowHintButtonClicked();
                break;
            case R.id.image_redirect_button_id:
                onImageRedirectButtonClicked();
                break;
        }

    }

    /**
     * Method invoked when Submit/Next button (R.id.submit_button_id) is pressed
     */
    private void onSubmitButtonClicked() {

        //Retrieving the text on Submit Button
        String buttonText = mSubmitButtonView.getText().toString();

        int noOfSelectedAnswers = mSelectedAnswerList.size();

        if (noOfSelectedAnswers == 0 && !mIsTextualQuestion) {
            //Show a Toast message when no options are selected
            Toast.makeText(this, R.string.no_option_selected, Toast.LENGTH_SHORT).show();
        } else {
            //When one/many of the options are selected

            if (buttonText.equals(getString(R.string.submit_button_text))) {
                //When the button is in Submit mode

                int noOfKeys = mCorrectAnswerList.size();

                //used to see if the selected answers are incorrect(0)/correct(1)/partially correct(0<x<1)
                double grade = 0;

                //Evaluating the Answers selected: START
                if (noOfKeys == 1) {
                    //When the options are RadioButton based or a textual question

                    if (!mIsTextualQuestion && mCorrectAnswerList.equals(mSelectedAnswerList)) {
                        //For RadioButton based
                        grade++;
                    } else if (mIsTextualQuestion) {
                        //For textual question
                        String correctAnswerStr = mCorrectAnswerList.get(0);
                        mTextualUserInputStr = mTextOptionView.getText().toString().trim();
                        if (mTextualUserInputStr.equalsIgnoreCase(correctAnswerStr)) {
                            grade++;
                        }
                    }

                } else if (noOfKeys > 1) {
                    //When the options are CheckBox based

                    for (String selectedAnswerStr : mSelectedAnswerList) {

                        if (mCorrectAnswerList.contains(selectedAnswerStr)) {
                            grade++;
                        }

                    }

                    //Normalizing with the number of keys
                    grade /= (noOfKeys > noOfSelectedAnswers ? noOfKeys : noOfSelectedAnswers);

                }
                //Evaluating the Answers selected: END

                if (!mHintButtonState) {
                    //When the hint button is not yet enabled

                    if (grade == 1) {
                        //When the selected answers are correct

                        //Highlighting the correct answers for non textual based question
                        if (!mIsTextualQuestion) {
                            revealAnswers();
                        }

                        //Updating the Score
                        advanceUserScore();

                        //Displaying a Toast message to congratulate
                        Toast.makeText(this, R.string.correct_answer, Toast.LENGTH_SHORT).show();

                        //Revealing the Hint image
                        revealHintImage();

                        //Scrolling to Image to reveal the bird(s)
                        scrollToView(mHintImageView);

                        if (mCurrentQuestionNo < mNoOfQuestionsToLoad) {
                            //Changing the Submit button Text to "Next" when there are more questions
                            mSubmitButtonView.setText(R.string.next_button_text);
                        } else {
                            //Changing the Submit button Text to "Finish" when all questions are done
                            mSubmitButtonView.setText(R.string.finish_button_text);
                        }

                        //Disabling the Option Buttons/EditText
                        if (mIsTextualQuestion) {
                            mTextOptionView.setEnabled(false);
                        } else {
                            disableOptions();
                        }

                    } else if (grade >= 0 && grade < 1) {
                        //When the selected answers are incorrect/or partially correct

                        //Displaying Toast message for incorrect answer
                        //and prompting for Hint that can be used
                        if (grade == 0) {
                            //When none of the selected answers are correct
                            Toast.makeText(this, R.string.wrong_answer_hint, Toast.LENGTH_SHORT).show();
                        } else if (grade > 0 && grade < 1) {
                            //When the selected answers are partially correct
                            Toast.makeText(this, R.string.partially_correct_answer_hint, Toast.LENGTH_SHORT).show();
                        }

                        //Enabling the "Show Hint" button and other related components
                        enableHintButtonComponents();

                    }

                } else {
                    //When the hint button is enabled

                    if (grade == 1) {
                        //When the selected answers are correct

                        //Updating the Score
                        advanceUserScore();

                        //Displaying a Toast message to congratulate
                        Toast.makeText(this, R.string.correct_answer, Toast.LENGTH_SHORT).show();

                    } else if (grade >= 0 && grade < 1) {
                        //When the selected answers are incorrect/or partially correct

                        //Displaying Toast message for incorrect answer
                        if (grade == 0) {
                            //When none of the selected answers are correct
                            Toast.makeText(this, R.string.wrong_answer_again, Toast.LENGTH_SHORT).show();
                        } else if (grade > 0 && grade < 1) {
                            //When the selected answers are partially correct
                            Toast.makeText(this, R.string.partially_correct_answer_again, Toast.LENGTH_SHORT).show();
                        }

                    }

                    //Highlighting the correct answers
                    if (!mIsTextualQuestion || grade < 1) {
                        //Revealing for textual based question only when the answer is incorrect
                        revealAnswers();
                    }

                    //Revealing the Hint image
                    revealHintImage();

                    if (!mHintButtonPressed && !mIsTextualQuestion) {
                        //Scrolling to Image to reveal the bird(s) when the hint button was not used
                        scrollToView(mHintImageView);
                    }

                    if (mCurrentQuestionNo < mNoOfQuestionsToLoad) {
                        //Changing the Submit button Text to "Next" when there are more questions
                        mSubmitButtonView.setText(R.string.next_button_text);
                    } else {
                        //Changing the Submit button Text to "Finish" when all questions are done
                        mSubmitButtonView.setText(R.string.finish_button_text);
                    }

                    //Disabling the Option Buttons/EditText
                    if (mIsTextualQuestion) {
                        mTextOptionView.setEnabled(false);
                    } else {
                        disableOptions();
                    }

                }

            } else if (buttonText.equals(getString(R.string.next_button_text))) {
                //When the button is in Next Question mode

                //Reverting the Button text to "Submit"
                mSubmitButtonView.setText(R.string.submit_button_text);

                //Canceling the download task of the current question if not completed
                mCurrentBitmapTaskFragment.cancelTaskInProgress(mCurrentQuestionIndex);

                //Canceling the download task of the next question if not completed
                mFutureBitmapTaskFragment.cancelTaskInProgress(mFutureQuestionIndex);

                //Scrolling over to the Question View
                scrollToView(mQuestionTextView);

                //Loading the Next Question and reinitializing the screen components
                Log.i(TAG, "onto Next Question: " + mCurrentQuestionNo);
                loadNextQuestion();

            } else if (buttonText.equals(getString(R.string.finish_button_text))) {
                //When the button is in Finish mode

                //Canceling the CountDownTimer in the end
                mCountDownLatchFragment.cancelTimer();

                //Ending the quiz as all the questions are completed
                showScoreSummary(false);
            }

        }

    }

    /**
     * Method invoked when "Show Hint" button (R.id.show_hint_button_id) is pressed
     */
    private void onShowHintButtonClicked() {

        if (!mHintButtonPressed) {
            //Restricting call to only for the first time for each question, when button gets enabled

            //Setting the Hint button pressed flag to true
            mHintButtonPressed = true;

            //Setting the Hint text in the view "R.id.hint_text_box_id"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mHintTextView.setText(Html.fromHtml(mHintArray[1], Html.FROM_HTML_MODE_LEGACY));
            } else {
                mHintTextView.setText(Html.fromHtml(mHintArray[1]));
            }

            //Revealing the Hint image
            revealHintImage();

        }

        Toast.makeText(this, R.string.hint_button_toast_text, Toast.LENGTH_SHORT).show();

        //Scrolling over to the Hint Text in the view "R.id.hint_text_box_id"
        scrollToView(mHintTextView);

    }

    /**
     * Method that reveals the Hint Image if available
     */
    private void revealHintImage() {
        //Setting the Hint Image if present
        if (mDownloadedBitmap != null) {
            //When the image is downloaded successfully
            mHintImageView.setImageBitmap(mDownloadedBitmap);

        } else if (!mHintButtonPressed) {
            //When Hint button is not yet pressed,
            //this will be called when user selects the correct answer without any hint

            //Clearing the future download task if any to download the current image faster
            mFutureBitmapTaskFragment.cancelTaskInProgress(mFutureQuestionIndex);

            if (mCurrentBitmapTaskFragment.getDownloadTaskState(mCurrentQuestionIndex)
                    .equals(ImageDownloaderTaskFragment.TaskState.TASK_STATE_STARTED.toString())) {
                //Attempting for download only if the Current Task is still in STARTED state

                //Attempting to load the current image being downloaded within a timeout of 15 millis
                Bitmap bitmap = mCurrentBitmapTaskFragment.getImageOnDemand(mCurrentQuestionIndex, 15);

                if (bitmap != null) {
                    //Showing the image if downloaded successfully
                    mHintImageView.setImageBitmap(bitmap);
                } else {
                    //Showing a Toast message on failure to download the image
                    Toast.makeText(this, getString(R.string.on_demand_download_fail_toast_text), Toast.LENGTH_SHORT).show();
                }

            } else {
                //Showing a Toast message if the Current Task is STOPPED/FAILED already
                Toast.makeText(this, getString(R.string.on_demand_download_fail_toast_text), Toast.LENGTH_SHORT).show();
            }

        } else {

            //Clearing the future download task if any to download the current image faster
            mFutureBitmapTaskFragment.cancelTaskInProgress(mFutureQuestionIndex);

            if (mCurrentBitmapTaskFragment.getDownloadTaskState(mCurrentQuestionIndex)
                    .equals(ImageDownloaderTaskFragment.TaskState.TASK_STATE_STARTED.toString())) {

                //Show Progress Dialog when Hint button is pressed and image is not yet downloaded
                showProgressDialog(R.string.progress_dialog_downloading_text);

            }

        }

    }

    /**
     * Method invoked when Image redirect button (R.id.image_redirect_button_id) is pressed
     */
    private void onImageRedirectButtonClicked() {
        //Scrolling over to the hint image when the button is pressed
        scrollToView(mHintImageView);
    }

    /**
     * Method that scrolls over to the View component passed
     *
     * @param scrollToView is the View under scrollable layout to which the screen needs to be auto scrolled
     */
    private void scrollToView(View scrollToView) {
        //Initializing {@link android.graphics.Rect} to store the view coordinates
        Rect viewCoordRect = new Rect();

        //Fills the viewCoordRect with the relative coordinates of the View in the parent layout
        scrollToView.getHitRect(viewCoordRect);

        //Requesting ScrollView to scroll to the rectangle "viewCoordRect" on the screen
        mScrollableContentView.requestChildRectangleOnScreen((ViewGroup) scrollToView.getParent(), viewCoordRect, false);
    }

    /**
     * Method that adds Click Listeners on Buttons
     */
    private void setClickListenersOnButtons() {

        mSubmitButtonView.setOnClickListener(this);
        mShowHintButtonView.setOnClickListener(this);
        mImageRedirectButtonView.setOnClickListener(this);

    }

    /**
     * Callback Method of {@link ImageDownloaderTaskFragment}
     * invoked when the {@code ImageDownloaderTask} of {@link ImageDownloaderTaskFragment} is starting to download the image.
     * Method evaluates the Network Connectivity prior to downloading the image
     *
     * @return True when the Network Connectivity is established; false otherwise
     */
    @Override
    public boolean isNetworkConnected() {
        //Get the instance of Connectivity Service
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //Flag to save the connectivity status
        boolean isNetworkConnected = false;

        if (connectivityManager != null) {
            //Retrieving current active default data network
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Retrieving the connectivity status
            isNetworkConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }

        //Displaying a Toast when there is a Network Connectivity issue
        if (!isNetworkConnected) {
            Toast.makeText(this, getString(R.string.bad_network_toast_text), Toast.LENGTH_LONG).show();
        }

        //Returning the state of Internet Connectivity
        return isNetworkConnected;
    }

    /**
     * Callback Method of {@link ImageDownloaderTaskFragment}
     * invoked when the {@code ImageDownloaderTask} of {@link ImageDownloaderTaskFragment} has successfully downloaded the image.
     * This method is used to save the Image downloaded, to a Bitmap variable.
     *
     * @param bitmap        is the Bitmap image that was downloaded
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      image was downloaded
     */
    @Override
    public void onDownloadFinish(Bitmap bitmap, int questionIndex) {

        if (questionIndex == mCurrentQuestionIndex) {
            //When the call was made for the current question

            mDownloadedBitmap = bitmap;

            if (mHintButtonPressed) {
                //If the hint button is already pressed, then reveal the image
                revealHintImage();
            }

            //Dismiss Progress dialog if active
            dismissProgressDialog();

        } else if (questionIndex == mFutureQuestionIndex) {
            //When the call was made for the next question

            mPrefetchedBitmap = bitmap;
        }

    }

    /**
     * Callback Method of {@link ImageDownloaderTaskFragment}
     * invoked when the {@code ImageDownloaderTask} of {@link ImageDownloaderTaskFragment} has failed to download the image due
     * to some intermittent issues. This method is used to log the failure
     * and to set the local bitmap variable to null.
     *
     * @param imageURLStr   is the URL of the image that failed to download
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      image failed to download
     */
    @Override
    public void onDownloadError(String imageURLStr, int questionIndex) {
        Log.e(TAG, "Failed to download the image - \n" + imageURLStr);

        if (questionIndex == mCurrentQuestionIndex) {
            //When the call was made for the current question
            mDownloadedBitmap = null;

            //Dismiss Progress dialog if active
            dismissProgressDialog();

        } else if (questionIndex == mFutureQuestionIndex) {
            //When the call was made for the next question
            mPrefetchedBitmap = null;
        }

    }

    /**
     * Callback Method of {@link ImageDownloaderTaskFragment}
     * invoked when the {@code ImageDownloaderTask} of {@link ImageDownloaderTaskFragment} is publishing
     * the Current download task progress to the main thread
     *
     * @param primaryProgress   is the Integer value of the Primary Progress
     * @param secondaryProgress is the Integer value of the Secondary buffer Progress
     */
    @Override
    public void syncProgress(int primaryProgress, int secondaryProgress) {
        ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG_PROGRESS_DIALOG);
        if (progressDialogFragment != null) {
            //Setting the Progress Values when the ProgressBar Dialog is active
            progressDialogFragment.setProgress(primaryProgress, secondaryProgress);
        }
    }

    /**
     * Method that increments the User score
     */
    private void advanceUserScore() {
        //Updating the score
        mTotalScoreTextView.setText(getString(R.string.current_score_format, (getUserScore() + 1), mNoOfQuestionsToLoad));
    }

    /**
     * Method that reveals/highlights the correct answer(s)
     */
    private void revealAnswers() {

        int noOfKeys = mCorrectAnswerList.size();
        int noOfOptions = mOptionArray.length;

        if (noOfOptions > 1) {
            //When the Options are present (else it is a text based question)

            String[] optionOrderArray = mOptionIndexOrderStr.split(";");

            //To keep track on the count of options being highlighted
            int countOfAnswersRevealed = 0;

            for (int i = 0; i < noOfOptions; i++) {
                //Iterating over the options

                //Declaring a CompoundButton view
                CompoundButton compoundButtonView = null;

                //Retrieving the current index option value
                int optionIndex = Integer.parseInt(optionOrderArray[i]);
                //Retrieving the corresponding text
                String optionStr = mOptionArray[optionIndex];

                if (mCorrectAnswerList.contains(optionStr)) {
                    //When the optionStr is the correct answer in the list

                    //Retrieving the corresponding button view
                    switch (i) {
                        case 0:
                            compoundButtonView = findViewById(R.id.first_option);
                            break;
                        case 1:
                            compoundButtonView = findViewById(R.id.second_option);
                            break;
                        case 2:
                            compoundButtonView = findViewById(R.id.third_option);
                            break;
                        case 3:
                            compoundButtonView = findViewById(R.id.fourth_option);
                            break;
                    }

                    //Highlighting the drawable of the button by setting the level to 2
                    LevelListDrawable optionLevelListDrawable = (LevelListDrawable) (compoundButtonView != null ? compoundButtonView.getBackground() : null);
                    if (optionLevelListDrawable != null) {
                        optionLevelListDrawable.setLevel(2);
                    }

                    countOfAnswersRevealed++; //Updating the count of answers revealed

                }

                if (noOfKeys == countOfAnswersRevealed) {
                    //Break out when all the answers are revealed
                    break;
                }

            }

        } else {
            //When the Number of Options are equal to 1, it is a text based question

            //Retrieving the correct Answer
            String correctAnswerStr = mCorrectAnswerList.get(0);

            //Updating the Hint text view
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mHintTextView.setText(Html.fromHtml(getString(R.string.default_textual_question_answer_text, correctAnswerStr), Html.FROM_HTML_MODE_LEGACY));
            } else {
                mHintTextView.setText(Html.fromHtml(getString(R.string.default_textual_question_answer_text, correctAnswerStr)));
            }

            //Scrolling over to the Hint Text in the view "R.id.hint_text_box_id"
            scrollToView(mHintTextView);
        }

    }

    /**
     * Method to display the ProgressBar Dialog
     *
     * @param messageId is the String resource Id containing the message to be shown
     */
    private void showProgressDialog(@StringRes int messageId) {
        //Initializing the Progress Bar Dialog Fragment
        ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(getString(messageId));
        //Displaying the Dialog
        progressDialogFragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_PROGRESS_DIALOG);

        //Pausing the CountDownTimer
        if (mCountDownLatchFragment != null) {
            mCountDownLatchFragment.pauseTimer();
        }

    }

    /**
     * Method to dismiss the Progress Dialog if active
     */
    private void dismissProgressDialog() {
        //Retrieving the Progress Bar Dialog Fragment
        ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG_PROGRESS_DIALOG);
        if (progressDialogFragment != null) {
            //Dismissing the Progress Bar Dialog when active
            progressDialogFragment.dismiss();
            //Resuming the CountDownTimer
            if (mCountDownLatchFragment != null) {
                mCountDownLatchFragment.resumeTimer();
            }
        }
    }

    /**
     * Method that displays a dialog for the Final Score at the end of the quiz
     *
     * @param timeElapsed is a boolean of whether the time elapsed before completing the quiz (True); False otherwise
     */
    private void showScoreSummary(boolean timeElapsed) {

        if (mActivityStateStr.equals(QuizActivityState.ACTIVE.toString())) {
            //When the current Activity is active

            //Initializing the Final Score Dialog
            FinalScoreDialogFragment finalScoreDialogFragment = FinalScoreDialogFragment.newInstance(getUserScore(), mNoOfQuestionsToLoad, timeElapsed);
            //Displaying the dialog
            finalScoreDialogFragment.show(getSupportFragmentManager(), FinalScoreDialogFragment.DIALOG_FRAGMENT_TAG);
        }

    }

    /**
     * Method that returns the current score of the User
     *
     * @return Integer containing the score of the User
     */
    private int getUserScore() {
        //Retrieving the current score text string
        String scoreTextStr = mTotalScoreTextView.getText().toString();

        //Parsing and Returning the score
        int slashIndex = scoreTextStr.indexOf("/");
        return Integer.parseInt(scoreTextStr.substring(0, slashIndex));
    }

    private void disableOptions() {

        int noOfOptions = mOptionArray.length; //Number of Options

        if (noOfOptions > 1) {
            //When the Number of Options are more than 1 (else it is a text based question)

            //Iterating over the options to retrieve and disable the button: START
            for (int optionIndex = 0; optionIndex < noOfOptions; optionIndex++) {

                //Declaring the CompoundButton
                CompoundButton optionButton = null;

                //Retrieving the corresponding option button
                switch (optionIndex) {
                    case 0:
                        optionButton = mOptionContainerView.findViewById(R.id.first_option);
                        break;
                    case 1:
                        optionButton = mOptionContainerView.findViewById(R.id.second_option);
                        break;
                    case 2:
                        optionButton = mOptionContainerView.findViewById(R.id.third_option);
                        break;
                    case 3:
                        optionButton = mOptionContainerView.findViewById(R.id.fourth_option);
                        break;
                }

                //Disabling the option button
                if (optionButton != null) {
                    optionButton.setEnabled(false);
                }
            }
            //Iterating over the options to retrieve and disable the button: END

        } else {
            //When the Number of Options are equal to 1, it is a text based question

            //Disabling the EditText option
            mTextOptionView.setEnabled(false);
        }

    }

    /**
     * Callback Method of {@link FinalScoreDialogFragment.FinalScoreDialogListener}
     * invoked when the user clicks on the Quit Button in the Final Score Dialog
     */
    @Override
    public void onQuitButtonClicked() {
        //Invoking finish on the activity to exit the activity
        finish();
    }

    /**
     * Callback Method of {@link FinalScoreDialogFragment.FinalScoreDialogListener}
     * invoked when the user clicks on the "Retake Quiz" Button in the Final Score Dialog
     */
    @Override
    public void onRetakeQuizButtonClicked() {
        //Initializing an Intent to {@link com.example.kaushiknsanji.birdquiz.WelcomeActivity}
        //to relaunch the quiz
        Intent welcomeIntent = new Intent(this, WelcomeActivity.class);

        //Starting the {@link com.example.kaushiknsanji.birdquiz.WelcomeActivity}
        startActivity(welcomeIntent);

        //Exiting the current activity once done
        finish();
    }

    /**
     * Callback Method of {@link CountDownLatchFragment.CountDownLatchListener}
     * invoked when every second of the timer elapses
     *
     * @param millisUntilFinished is the remaining Millis of the timer
     */
    @Override
    public void updateMillisRemaining(long millisUntilFinished) {
        updateTimerText(millisUntilFinished);
    }

    /**
     * Method that updates the Timer text field "R.id.count_down_text_id"
     *
     * @param millisUntilFinished is the remaining Millis of the timer
     */
    private void updateTimerText(long millisUntilFinished) {
        //Retrieving the Minutes and seconds
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        //Updating the timer with the value
        mCountDownTextView.setText(getString(R.string.timer_text, minutes, seconds));
    }

    /**
     * Callback Method of {@link CountDownLatchFragment.CountDownLatchListener}
     * invoked when the {@link android.os.CountDownTimer} finishes
     */
    @Override
    public void onTimerFinish() {
        //Ending the quiz when the timer has elapsed
        showScoreSummary(true);
        //Updating the timer image to timer_off
        mCountDownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer_off, 0, 0, 0);
    }

    /**
     * Callback Method of {@link com.example.kaushiknsanji.birdquiz.CountDownLatchFragment.CountDownLatchListener}
     * invoked when the {@link android.os.CountDownTimer} starts
     */
    @Override
    public void onTimerStart() {
        //Updating the timer image to timer_start
        mCountDownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer, 0, 0, 0);
    }

    /**
     * Callback Method of {@link com.example.kaushiknsanji.birdquiz.CountDownLatchFragment.CountDownLatchListener}
     * invoked when the {@link android.os.CountDownTimer} is paused/cancelled
     */
    @Override
    public void onTimerCancel() {
        //Updating the timer image to timer_off
        mCountDownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer_off, 0, 0, 0);
    }

    //Enum for managing different states of this Activity
    private enum QuizActivityState {
        ACTIVE, INACTIVE
    }

}
