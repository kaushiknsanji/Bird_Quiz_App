package com.example.kaushiknsanji.birdquiz;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;

/**
 * Fragment class for managing the {@link CountDownTimer} for the quiz
 *
 * @author <a href="mailto:kaushiknsanji@gmail.com">Kaushik N Sanji</a>
 */
public class CountDownLatchFragment extends Fragment {

    public static final String TAG = CountDownLatchFragment.class.getSimpleName();
    //Bundle Key Constants for saving/restoring
    private static final String MILLIS_IN_FUTURE_REMAINING_LONG_KEY = "MillisInFutureRemaining";
    private static final String COUNT_DOWN_INTERVAL_LONG_KEY = "CountDownInterval";
    private static final String TIMER_STATE_STR_KEY = "TimerState";
    //Stores the interval along the way to receive callbacks. Defaulted to every second
    private long mCountDownInterval = 1000;
    //Stores the number of millis in the future remaining from the start until the countdown is done
    private long mMillisInFutureRemaining;
    //Stores the Instance of CountDownTimer
    private CountDownTimer mCountDownTimer;
    //Stores the CountDownTimer State
    private String mTimerStateStr;
    //Instance of the interface to deliver action events
    private CountDownLatchListener mCountDownLatchListener;

    //Attaching the context to the fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCountDownLatchListener = (CountDownLatchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the interface CountDownLatchListener");
        }
    }

    //Attaching the activity to the fragment
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCountDownLatchListener = (CountDownLatchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement the interface CountDownLatchListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting this fragment to be retained across config changes
        setRetainInstance(true);

    }

    /**
     * Method that loads/creates and starts the {@link android.os.CountDownTimer}
     *
     * @param millisInFuture is the Millis since epoch when alarm should stop.
     */
    public void loadTimer(long millisInFuture) {
        //Updating the current remaining millis
        mMillisInFutureRemaining = millisInFuture;

        //Initializing the timer
        mCountDownTimer = new MyCountDownTimer(mMillisInFutureRemaining, mCountDownInterval);

        //Starting the timer
        mCountDownTimer.start();

        //Sending the start event to the listener
        mCountDownLatchListener.onTimerStart();

        //Setting the Timer state to ACTIVE
        mTimerStateStr = TimerState.ACTIVE.toString();
    }

    /**
     * Method to retrieve the time remaining in Millis
     *
     * @return the value of Millis remaining to complete
     */
    public long getRemainingTimeInMillis() {
        return mMillisInFutureRemaining;
    }

    /**
     * Methods that sets the start time in Millis since epoch when alarm should stop
     *
     * @param millisInFuture is the Millis since epoch when alarm should stop
     */
    public void setStartTimeInMillis(long millisInFuture) {
        //Updating the current remaining millis
        mMillisInFutureRemaining = millisInFuture;
    }

    /**
     * Method that pauses the Count Down Timer
     */
    public void pauseTimer() {
        cancelTimer();
    }

    /**
     * Method that resumes the Count Down Timer
     */
    public void resumeTimer() {
        loadTimer(mMillisInFutureRemaining);
    }

    /**
     * Method that cancels the {@link CountDownTimer}
     */
    public void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
            //Setting the Timer state to INACTIVE
            mTimerStateStr = TimerState.INACTIVE.toString();
            //Sending the cancel/pause event to the Listener
            mCountDownLatchListener.onTimerCancel();
        }
    }

    /**
     * Method that returns the State of CountDownTimer
     *
     * @return The string that says "ACTIVE"/"INACTIVE" which are the states of the CountDownTimer
     */
    public String getTimerState() {
        return mTimerStateStr;
    }

    //Saving the state of CountDownLatchFragment to Bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putLong(MILLIS_IN_FUTURE_REMAINING_LONG_KEY, mMillisInFutureRemaining);
        outState.putLong(COUNT_DOWN_INTERVAL_LONG_KEY, mCountDownInterval);
        outState.putString(TIMER_STATE_STR_KEY, mTimerStateStr);

        super.onSaveInstanceState(outState);
    }

    //Restoring the state from the Bundle after the activity is created
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.size() > 0) {
            mMillisInFutureRemaining = savedInstanceState.getLong(MILLIS_IN_FUTURE_REMAINING_LONG_KEY);
            mCountDownInterval = savedInstanceState.getLong(COUNT_DOWN_INTERVAL_LONG_KEY);
            mTimerStateStr = savedInstanceState.getString(TIMER_STATE_STR_KEY);
        }

    }

    //Called when Activity is destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        //Clearing the reference to the Activity to avoid leaking
        mCountDownLatchListener = null;
    }

    //Enum for managing different states of the CountDownTimer
    public enum TimerState {
        ACTIVE, INACTIVE
    }

    /**
     * Activity that creates an instance of this {@link CountDownLatchFragment}
     * needs to implement the interface to receive event callbacks
     */
    interface CountDownLatchListener {
        /**
         * Callback Method of {@link CountDownLatchListener}
         * invoked when every second of the timer elapses
         *
         * @param millisUntilFinished is the remaining Millis of the timer
         */
        void updateMillisRemaining(long millisUntilFinished);

        /**
         * Callback Method of {@link CountDownLatchListener}
         * invoked when the {@link CountDownTimer} finishes
         */
        void onTimerFinish();

        /**
         * Callback Method of {@link CountDownLatchListener}
         * invoked when the {@link CountDownTimer} starts
         */
        void onTimerStart();

        /**
         * Callback Method of {@link CountDownLatchListener}
         * invoked when the {@link CountDownTimer} is paused/cancelled
         */
        void onTimerCancel();
    }

    /**
     * Class that extends the {@link CountDownTimer}
     * and adds implementation to the abstract methods
     */
    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        @Override
        public void onTick(long millisUntilFinished) {
            //Storing the current remaining millis
            mMillisInFutureRemaining = millisUntilFinished;
            //Waiting on Listener to attach
            while (mCountDownLatchListener == null) ;
            //Sending the event to the listener
            mCountDownLatchListener.updateMillisRemaining(mMillisInFutureRemaining);
        }

        /**
         * Callback fired when the time is up.
         */
        @Override
        public void onFinish() {
            //Waiting on Listener to attach
            while (mCountDownLatchListener == null) ;
            //Updating the timer to 0
            mMillisInFutureRemaining = 0;
            //Setting the Timer state to INACTIVE
            mTimerStateStr = TimerState.INACTIVE.toString();
            //Sending the final tick to the listener
            mCountDownLatchListener.updateMillisRemaining(mMillisInFutureRemaining);
            //Sending the Finish event to the listener
            mCountDownLatchListener.onTimerFinish();
        }

    }

}
