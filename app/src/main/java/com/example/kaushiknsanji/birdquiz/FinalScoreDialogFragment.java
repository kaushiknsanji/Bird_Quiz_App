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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * DialogFragment class for displaying the Final score at the end of the quiz
 * or when the quiz timer elapses
 *
 * @author Kaushik N Sanji
 */
public class FinalScoreDialogFragment extends DialogFragment {

    private static final String TAG = FinalScoreDialogFragment.class.getSimpleName();
    //Bundle Key constants
    private static final String SCORE_INT_KEY = "Score";
    private static final String NUMBER_OF_QUESTIONS_INT_KEY = "NoOfQuestions";
    private static final String TIME_ELAPSED_BOOL_KEY = "TimeElapsed";
    //Instance of the interface to deliver action events
    private FinalScoreDialogListener mListener;

    //Creating a static instance of the DialogFragment
    static FinalScoreDialogFragment newInstance(int score, int noOfQuestions, boolean timeElapsed) {
        FinalScoreDialogFragment finalScoreDialogFragment = new FinalScoreDialogFragment();
        //Storing Argument values: START
        Bundle args = new Bundle();
        args.putInt(SCORE_INT_KEY, score);
        args.putInt(NUMBER_OF_QUESTIONS_INT_KEY, noOfQuestions);
        args.putBoolean(TIME_ELAPSED_BOOL_KEY, timeElapsed);
        finalScoreDialogFragment.setArguments(args);
        //Storing Argument values: END
        return finalScoreDialogFragment; //Returning the instance
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FinalScoreDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FinalScoreDialogListener");
        }
    }

    //Attaching the Activity to the fragment
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FinalScoreDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FinalScoreDialogListener");
        }
    }

    //Creating the Dialog to be shown
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Building the Alert Dialog for the Final Score
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        //Retrieving the Layout Inflater
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        //Retrieving the layout of Final Score
        //(Passing null since the view will be attached to the dialog)
        View finalScoreLayoutView = layoutInflater.inflate(R.layout.final_score_layout, null);

        //Retrieving the arguments passed
        Bundle args = getArguments();
        int totalScore = args.getInt(SCORE_INT_KEY);
        int noOfQuestions = args.getInt(NUMBER_OF_QUESTIONS_INT_KEY);
        boolean timeElapsed = args.getBoolean(TIME_ELAPSED_BOOL_KEY);

        //Retrieving the Title View and setting the Typeface: START
        TextView titleTextView = finalScoreLayoutView.findViewById(R.id.final_score_title_text_id);
        Typeface titleTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coprgtb.ttf");
        titleTextView.setTypeface(titleTypeface);
        //Retrieving the Title View and setting the Typeface: END

        //Retrieving the Final Score Text View to set the Text and Typeface: START
        TextView finalScoreTextView = finalScoreLayoutView.findViewById(R.id.final_score_text_id);
        finalScoreTextView.setText(getString(R.string.final_score_text, totalScore, noOfQuestions));
        Typeface finalScoreTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/rothenbg.ttf");
        finalScoreTextView.setTypeface(finalScoreTypeface);
        //Retrieving the Final Score Text View to set the Text and Typeface: END

        //Retrieving the Grade Summary Text View to set the Typeface and the Score Summary: START
        TextView gradeSummaryTextView = finalScoreLayoutView.findViewById(R.id.grade_summary_text_id);
        setGradeSummaryText(gradeSummaryTextView, totalScore, noOfQuestions, timeElapsed);
        //Retrieving the Grade Summary Text View to set the Typeface and the Score Summary: END

        //Setting Positive Button and its Listener
        Button retakeButton = finalScoreLayoutView.findViewById(R.id.retake_button_id);
        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRetakeQuizButtonClicked();
                dismiss();
            }
        });

        //Setting Negative Button and its Listener
        Button quitButton = finalScoreLayoutView.findViewById(R.id.quit_button_id);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuitButtonClicked();
                dismiss();
            }
        });

        //Setting the view on Dialog
        dialogBuilder.setView(finalScoreLayoutView);

        return dialogBuilder.create(); //Returning the dialog created
    }

    /**
     * Method that sets the Score Summary and Typeface for the TextView (R.id.grade_summary_text_id)
     *
     * @param gradeSummaryTextView is the TextView (R.id.grade_summary_text_id) in the final score layout
     * @param totalScore           is the integer value of the Final score
     * @param noOfQuestions        is the integer value of the total questions loaded in the quiz
     * @param timeElapsed          is a boolean of whether the time elapsed before completing the quiz (True); False otherwise
     */
    private void setGradeSummaryText(TextView gradeSummaryTextView, int totalScore, int noOfQuestions, boolean timeElapsed) {

        //Stores the string to be displayed as a summary to the score
        String summaryTextStr = "";

        if (!timeElapsed) {
            //When the quiz was completed within time

            //Calculating the Percent score
            double gradePercent = ((double) totalScore / (double) noOfQuestions) * 100;

            if (gradePercent == 100) {
                //When all answers are correct
                summaryTextStr = getString(R.string.perfect_score_summary_text);
            } else if (gradePercent >= 75 && gradePercent < 100) {
                //Above average score for 75 percent and above
                summaryTextStr = getString(R.string.above_avg_score_summary_text);
            } else if (gradePercent > 25 && gradePercent < 75) {
                //Average score for 25 to 75 percent
                summaryTextStr = getString(R.string.avg_score_summary_text);
            } else if (gradePercent <= 25 && gradePercent > 0) {
                //Below average score for 25 percent and below
                summaryTextStr = getString(R.string.below_avg_score_summary_text);
            } else if (gradePercent == 0) {
                //When none of the answers are correct
                summaryTextStr = getString(R.string.zero_score_summary_text);
            }

        } else {
            //When the quiz was not completed within time
            summaryTextStr = getString(R.string.elapsed_time_summary_text);
        }


        //Setting the summary on the TextView
        gradeSummaryTextView.setText(summaryTextStr);
        //Setting the Typeface
        Typeface gradeSummaryTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/quintessential_regular.ttf");
        gradeSummaryTextView.setTypeface(gradeSummaryTypeface);
    }

    //Called after the Dialog has been created/displayed
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Preventing cancellation through back button or on touch of the screen
        getDialog().setCancelable(false);
    }

    /**
     * Activity that creates instance of this {@link DialogFragment}
     * needs to implement the interface to receive event callbacks
     */
    interface FinalScoreDialogListener {
        /**
         * Callback Method of {@link FinalScoreDialogListener}
         * invoked when the user clicks on the Quit Button in the Final Score Dialog
         */
        void onQuitButtonClicked();

        /**
         * Callback Method of {@link FinalScoreDialogListener}
         * invoked when the user clicks on the "Retake Quiz" Button in the Final Score Dialog
         */
        void onRetakeQuizButtonClicked();
    }

}
