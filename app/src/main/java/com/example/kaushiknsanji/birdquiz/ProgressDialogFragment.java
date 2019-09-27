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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * DialogFragment class for displaying the Progress of Image Download
 *
 * @author Kaushik N Sanji
 */
public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG_PROGRESS_DIALOG = ProgressDialogFragment.class.getSimpleName();
    //Bundle Keys for saving/restoring data
    private static final String TEXT_MESSAGE_STR_KEY = "TextMessageStr";
    private static final String PRIMARY_PROGRESS_INT_KEY = "PrimaryProgressValue";
    private static final String SECONDARY_PROGRESS_INT_KEY = "SecondaryProgressValue";
    //Stores the ProgressBar
    private ProgressBar mProgressBar;
    //Stores the Message for the ProgressBar Dialog
    private TextView mTextMessageView;

    //Creating a static instance of the DialogFragment
    static ProgressDialogFragment newInstance(String messageStr) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        //Storing Argument values in a Bundle: START
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_MESSAGE_STR_KEY, messageStr);
        progressDialogFragment.setArguments(bundle);
        //Storing Argument values in a Bundle: END
        return progressDialogFragment; //Returning the instance
    }

    //Creating the Dialog to be shown
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Building the Alert Dialog for the ProgressBar
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        //Retrieving the Layout Inflater
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        //Retrieving the layout of Progress Dialog
        //Passing null since the view will be attached to the dialog
        View progressDialogView = layoutInflater.inflate(R.layout.progress_bar_layout, null);

        //Initializing the view components
        mProgressBar = progressDialogView.findViewById(R.id.horizontal_progress_bar_id);
        mTextMessageView = progressDialogView.findViewById(R.id.progress_text_id);

        //Setting the Text Message for the ProgressBar
        mTextMessageView.setText(getArguments().getString(TEXT_MESSAGE_STR_KEY));

        //Setting the view on the Dialog
        dialogBuilder.setView(progressDialogView);

        //Returning the dialog created
        return dialogBuilder.create();
    }

    /**
     * Method that sets the Primary and Secondary progress on the ProgressBar
     *
     * @param primaryProgressValue   is the Integer value for the Primary Progress to be shown
     * @param secondaryProgressValue is the Integer value for the Secondary Progress to be shown
     */
    public void setProgress(int primaryProgressValue, int secondaryProgressValue) {
        if (mProgressBar.getProgress() == 0) {
            //Set the Progress value AS-IS when the current value is 0
            mProgressBar.setProgress(primaryProgressValue);
        } else {
            //Increment the Progress value when the current value is more than 0
            mProgressBar.incrementProgressBy(primaryProgressValue);
        }

        if (mProgressBar.getSecondaryProgress() == 0) {
            //Set the Progress value AS-IS when the current value is 0
            mProgressBar.setSecondaryProgress(secondaryProgressValue);
        } else {
            //Increment the Progress value when the current value is more than 0
            mProgressBar.incrementSecondaryProgressBy(secondaryProgressValue);
        }

    }

    //Saving the state of the ProgressBar Dialog to Bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(TEXT_MESSAGE_STR_KEY, mTextMessageView.getText().toString());
        outState.putInt(PRIMARY_PROGRESS_INT_KEY, mProgressBar.getProgress());
        outState.putInt(SECONDARY_PROGRESS_INT_KEY, mProgressBar.getSecondaryProgress());

        super.onSaveInstanceState(outState);
    }

    //Called when the attached Activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Preventing cancellation through back button or on touch of the screen
        getDialog().setCancelable(false);

        //Restoring the state of the ProgressBar Dialog from the Bundle
        if (savedInstanceState != null && savedInstanceState.size() > 0) {
            mTextMessageView.setText(savedInstanceState.getString(TEXT_MESSAGE_STR_KEY));
            mProgressBar.setProgress(savedInstanceState.getInt(PRIMARY_PROGRESS_INT_KEY));
            mProgressBar.setSecondaryProgress(savedInstanceState.getInt(SECONDARY_PROGRESS_INT_KEY));
        }

    }

}
