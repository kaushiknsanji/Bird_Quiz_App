package com.example.kaushiknsanji.birdquiz;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Fragment that manages Custom AsyncTask implemented for downloading an Image
 * from the specified URL
 *
 * @author <a href="mailto:kaushiknsanji@gmail.com">Kaushik N Sanji</a>
 */
public class ImageDownloaderTaskFragment extends Fragment {

    //Declaring TAGS for two download tasks
    public static final String TAG_CURRENT_TASK = ImageDownloaderTaskFragment.class.getSimpleName() + "_CURRENT";
    public static final String TAG_FUTURE_TASK = ImageDownloaderTaskFragment.class.getSimpleName() + "_FUTURE";
    private static final String TAG = ImageDownloaderTaskFragment.class.getSimpleName();
    //Bundle Key Constants for saving/restoring
    private static final String TASK_STATE_STR_KEY = "TaskState";
    private static final String QUESTION_INDEX_INT_KEY = "QuestionIndex";
    private static final String IMAGE_URL_STR_KEY = "ImageURL";
    //Stores the states for the download task (Defaulted to STOPPED)
    private String mTaskStateStr = TaskState.TASK_STATE_STOPPED.toString();
    //Stores the Question Index of the download task
    private int mQuestionIndex;
    //Stores the Image URL of the download task
    private String mImageURLStr;
    //Stores the Image downloaded by the task
    private Bitmap mDownloadedBitmap;
    //Instance of the interface to deliver action events
    private ImageDownloaderListener mDownloaderListener;
    //Instances of the ImageDownloaderTasks for both Current and Future download tasks
    private ImageDownloaderTask mCurrentBitmapDownloadTask; //For Current Question
    private ImageDownloaderTask mFutureBitmapDownloadTask; //For Future Question

    //Attaching the context to the fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDownloaderListener = (ImageDownloaderListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ImageDownloaderListener");
        }
    }

    //Attaching the activity to the fragment
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDownloaderListener = (ImageDownloaderListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ImageDownloaderListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting this fragment to be retained across config changes
        setRetainInstance(true);

    }

    /**
     * Method that returns the Task State value for the provided Question Index
     *
     * @param questionIndex is the Integer identifier of the Question for which the task state is required
     * @return String containing the state(STARTED/COMPLETED/FAILED/STOPPED) of the Download Task
     */
    public String getDownloadTaskState(int questionIndex) {
        if (questionIndex == mQuestionIndex) {
            //Returning the value when the task is of the index passed
            return mTaskStateStr;
        }

        //Returning the value of stopped when the question index
        //does not match with the task
        return TaskState.TASK_STATE_STOPPED.toString();
    }

    /**
     * Method that returns the Bitmap Image of the provided Question Index on the task
     *
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      Image downloaded by the task is required
     * @return Bitmap containing the Image downloaded by the task
     */
    public Bitmap getDownloadedBitmap(int questionIndex) {
        if (questionIndex == mQuestionIndex) {
            //Returning the Image when the Task is of the Index passed
            return mDownloadedBitmap;
        }
        //Returning null when the question index does not match with the task
        return null;
    }

    /**
     * Method that Cancels the download task if still in progress
     *
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      Image is being downloaded and needs to be cancelled if not completed
     */
    public void cancelTaskInProgress(int questionIndex) {
        if (questionIndex == mQuestionIndex
                && mTaskStateStr.equals(TaskState.TASK_STATE_STARTED.toString())) {
            //Cancelling the Task only when it is for the same Index passed
            //and the task has not yet completed/failed/stopped

            if (mCurrentBitmapDownloadTask != null) {
                //Canceling if the Task Fragment is for Current Task
                mCurrentBitmapDownloadTask.cancel(true);

            } else if (mFutureBitmapDownloadTask != null) {
                //Canceling if the Task Fragment is for Future Task
                mFutureBitmapDownloadTask.cancel(true);

            }

            //Updating the Task state to STOPPED
            mTaskStateStr = TaskState.TASK_STATE_STOPPED.toString();
        }
    }

    /**
     * Method that executes/starts the Image download task for the Current Fragment Task
     *
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      Image is going to be downloaded
     * @param imageURLStr   is the source URL of the Image to be downloaded
     */
    public void executeCurrentTask(int questionIndex, String imageURLStr) {
        //Setting the Future Task to null
        mFutureBitmapDownloadTask = null;

        //Initializing the Current Task
        mCurrentBitmapDownloadTask = new ImageDownloaderTask();

        //loading the values
        mQuestionIndex = questionIndex;
        mImageURLStr = imageURLStr;

        //Setting the Image to null initially
        mDownloadedBitmap = null;

        //Setting the Task state to STARTED
        mTaskStateStr = TaskState.TASK_STATE_STARTED.toString();

        //Waiting for the Listener to attach if restarted
        while (mDownloaderListener == null) ;

        //Evaluating the Internet Connectivity
        if (mDownloaderListener.isNetworkConnected()) {
            //Starting the download task when the Network is active
            mCurrentBitmapDownloadTask.execute(mImageURLStr);
        } else {
            //Exiting with Error when the Network is inactive
            mDownloaderListener.onDownloadError(mImageURLStr, mQuestionIndex);

            //Updating the Task state to FAILED
            mTaskStateStr = TaskState.TASK_STATE_FAILED.toString();

            Log.e(TAG, "executeCurrentTask: Failed due to Bad Network Connection");
        }

    }

    /**
     * Method that executes/starts the Image download task for the Future Fragment Task
     *
     * @param questionIndex is the Integer identifier of the Question for which the
     *                      Image is going to be downloaded
     * @param imageURLStr   is the source URL of the Image to be downloaded
     */
    public void executeFutureTask(int questionIndex, String imageURLStr) {
        //Setting the Current Task to null
        mCurrentBitmapDownloadTask = null;

        //Initializing the Future Task
        mFutureBitmapDownloadTask = new ImageDownloaderTask();

        //loading the values
        mQuestionIndex = questionIndex;
        mImageURLStr = imageURLStr;

        //Setting the Image to null initially
        mDownloadedBitmap = null;

        //Setting the Task state to STARTED
        mTaskStateStr = TaskState.TASK_STATE_STARTED.toString();

        //Waiting for the Listener to attach if restarted
        while (mDownloaderListener == null) ;

        //Evaluating the Internet Connectivity
        if (mDownloaderListener.isNetworkConnected()) {
            //Starting the download task when the Network is active
            mFutureBitmapDownloadTask.execute(mImageURLStr);
        } else {
            //Exiting with Error when the Network is inactive
            mDownloaderListener.onDownloadError(mImageURLStr, mQuestionIndex);

            //Updating the Task state to FAILED
            mTaskStateStr = TaskState.TASK_STATE_FAILED.toString();

            Log.e(TAG, "executeFutureTask: Failed due to Bad Network Connection");
        }

    }

    /**
     * Method that retrieves and returns the image if successfully downloaded within
     * the timeout specified; else the task gets cancelled
     *
     * @param questionIndex   is the Integer identifier of the Question for which the
     *                        Image is being downloaded
     * @param timeoutInMillis is the timeout in Millis within which the Image needs to be
     *                        downloaded; else the task gets cancelled
     * @return Bitmap containing the image if downloaded successfully within time; else will be null
     * It can be Null even when the Question Index is not matching with that of the present task
     */
    public Bitmap getImageOnDemand(int questionIndex, long timeoutInMillis) {

        if (mQuestionIndex == questionIndex) {
            //When the Task is of the same index passed

            try {
                //Trying to retrieve the Image within the timeout specified
                if (mCurrentBitmapDownloadTask != null) {
                    //When the download is for the Current Task Fragment
                    mDownloadedBitmap = mCurrentBitmapDownloadTask.get(timeoutInMillis, TimeUnit.MILLISECONDS);
                } else if (mFutureBitmapDownloadTask != null) {
                    //When the download is for the Future Task Fragment
                    mDownloadedBitmap = mFutureBitmapDownloadTask.get(timeoutInMillis, TimeUnit.MILLISECONDS);
                }

                //Adding the successfully downloaded image to Bitmap Cache
                BitmapImageCache.addBitmapToCache(mImageURLStr, mDownloadedBitmap);

                return mDownloadedBitmap; //Returning the Downloaded Bitmap

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                //Cancelling the long running task on error
                cancelTaskInProgress(mQuestionIndex);
                //Ensuring the Image is Null
                mDownloadedBitmap = null;
            }

        }

        //Returning Null when the Task is of different question index from the one passed
        return null;
    }

    //Called when Activity is destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        //Clearing the reference to the Activity to avoid leaking
        mDownloaderListener = null;
    }

    //Saving the state of ImageDownloaderTaskFragment to Bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(TASK_STATE_STR_KEY, mTaskStateStr);
        outState.putInt(QUESTION_INDEX_INT_KEY, mQuestionIndex);
        outState.putString(IMAGE_URL_STR_KEY, mImageURLStr);

        super.onSaveInstanceState(outState);
    }

    //Restoring the state from the Bundle after the activity is created
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.size() > 0) {
            mTaskStateStr = savedInstanceState.getString(TASK_STATE_STR_KEY);
            mQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX_INT_KEY);
            mImageURLStr = savedInstanceState.getString(IMAGE_URL_STR_KEY);

            if (mTaskStateStr.equals(TaskState.TASK_STATE_COMPLETED.toString())) {
                //Restoring Bitmap from the Bitmap Cache when the download task had completed
                mDownloadedBitmap = BitmapImageCache.getBitmapFromCache(mImageURLStr);
            } else {
                //Setting to null when the download task had not completed
                mDownloadedBitmap = null;
            }

        }

    }

    /**
     * Method that returns the Fragment data on Task Complete
     *
     * @return Bundle containing the Current Fragment's data
     */
    private Bundle getFragmentContent() {

        Bundle bundle = new Bundle();

        if (mTaskStateStr.equals(TaskState.TASK_STATE_COMPLETED.toString())) {
            //Updating Bundle on Task Complete state

            bundle.putString(TASK_STATE_STR_KEY, mTaskStateStr);
            bundle.putInt(QUESTION_INDEX_INT_KEY, mQuestionIndex);
            bundle.putString(IMAGE_URL_STR_KEY, mImageURLStr);

        }

        //Returning the Bundle prepared
        return bundle;
    }

    /**
     * Method that copies the content of a Fragment to another Fragment of the same type
     *
     * @param taskFragment is the Fragment of the same type which is the source for copy
     */
    public void copy(ImageDownloaderTaskFragment taskFragment) {

        Bundle bundle = taskFragment.getFragmentContent();

        if (bundle.size() > 0) {
            //Copying from the bundle if it contains data

            mTaskStateStr = bundle.getString(TASK_STATE_STR_KEY);
            mQuestionIndex = bundle.getInt(QUESTION_INDEX_INT_KEY);
            mImageURLStr = bundle.getString(IMAGE_URL_STR_KEY);
            //Retrieving the Image from the Bitmap Cache
            mDownloadedBitmap = BitmapImageCache.getBitmapFromCache(mImageURLStr);

        }

    }

    //Enum for managing/updating the different states of the download tasks
    public enum TaskState {
        TASK_STATE_STARTED, TASK_STATE_COMPLETED, TASK_STATE_FAILED, TASK_STATE_STOPPED
    }

    /**
     * Activity that creates instance of this {@link ImageDownloaderTaskFragment}
     * needs to implement the interface to receive event callbacks
     */
    interface ImageDownloaderListener {

        /**
         * Callback Method of {@link ImageDownloaderTaskFragment}
         * invoked when the {@link ImageDownloaderTask} is starting to download the image.
         * Method evaluates the Network Connectivity prior to downloading the image
         *
         * @return True when the Network Connectivity is established; false otherwise
         */
        boolean isNetworkConnected();

        /**
         * Callback Method of {@link ImageDownloaderTaskFragment}
         * invoked when the {@link ImageDownloaderTask} has successfully downloaded the image.
         * This method is used to save the Image downloaded, to a Bitmap variable.
         *
         * @param bitmap        is the Bitmap image that was downloaded
         * @param questionIndex is the Integer identifier of the Question for which the
         *                      image was downloaded
         */
        void onDownloadFinish(Bitmap bitmap, int questionIndex);

        /**
         * Callback Method of {@link ImageDownloaderTaskFragment}
         * invoked when the {@link ImageDownloaderTask} has failed to download the image due
         * to some intermittent issues. This method is used to log the failure
         * and to set the local bitmap variable to null.
         *
         * @param imageURLStr   is the URL of the image that failed to download
         * @param questionIndex is the Integer identifier of the Question for which the
         *                      image failed to download
         */
        void onDownloadError(String imageURLStr, int questionIndex);

        /**
         * Callback Method of {@link ImageDownloaderTaskFragment}
         * invoked when the {@link ImageDownloaderTask} is publishing
         * the Current download task progress to the main thread
         *
         * @param primaryProgress   is the Integer value of the Primary Progress
         * @param secondaryProgress is the Integer value of the Secondary buffer Progress
         */
        void syncProgress(int primaryProgress, int secondaryProgress);
    }

    /**
     * Custom AsyncTask implemented for downloading an Image from the specified URL
     */
    private class ImageDownloaderTask extends AsyncTask<String, Integer, Bitmap> {

        //Stores the Content Length of the image being downloaded
        private int mContentLength;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            InputStream bitmapInputStream = null;

            try {

                URL imageURL = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) imageURL.openConnection();
                urlConnection.setConnectTimeout(10000); //Setting timeout to 10 sec
                urlConnection.connect(); //Opening connection to the resource

                //Retrieving the content length
                mContentLength = urlConnection.getContentLength();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //Downloading the image on Response OK
                    bitmapInputStream = urlConnection.getInputStream();
                    bitmap = getBitmapSampledImage(new BufferedInputStream(bitmapInputStream));
                    //Adding the successfully downloaded image to Bitmap Cache
                    BitmapImageCache.addBitmapToCache(params[0], bitmap);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //Closing the stream in the end
                if (bitmapInputStream != null) {
                    try {
                        bitmapInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return bitmap; //Returning the bitmap retrieved
        }

        /**
         * Method that downloads the image and downsamples the image if necessary
         * and returns the processed image
         *
         * @param inputStream is the Buffered stream of the URL
         * @return Bitmap containing the image downloaded
         * @throws IOException
         */
        private Bitmap getBitmapSampledImage(BufferedInputStream inputStream) throws IOException {

            //Initializing ByteArrayOutputStream to write the downloaded bytes to a byte array
            //for downsampling
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            //Initializing the byte array
            byte[] buf = new byte[4096];
            int bytesRead = 0;

            //Reading and writing the bytes to the array
            while ((bytesRead = inputStream.read(buf)) > 0) {
                byteArrayOutputStream.write(buf, 0, bytesRead);
                //Publishing progress
                publishProgress(bytesRead);
            }

            //Converting the Image stream content to byte array
            byte[] imageData = byteArrayOutputStream.toByteArray();

            //Closing the ByteArrayOutputStream
            byteArrayOutputStream.close();

            //Retrieving only the bounds for the first time through BitmapFactory.Options
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            bitmapOptions.inPreferQualityOverSpeed = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bitmapOptions.inScaled = false;
                bitmapOptions.inPremultiplied = false;
            }

            //Decoding the Image byte array
            BitmapFactory.decodeByteArray(imageData, 0, imageData.length, bitmapOptions);

            //Setting the required dimensions
            int reqdWidth = 576;
            int reqdHeight = 432;

            //Deriving the scaling factor to downsize the image: START
            //Starting with the down scaling factor of 1
            int downScalingFactorSize = 1;

            //Retrieving the raw image dimensions decoded through Bitmap options
            int rawWidth = bitmapOptions.outWidth;
            int rawHeight = bitmapOptions.outHeight;

            if (rawWidth > reqdWidth || rawHeight > reqdHeight) {
                //Calculating Half raw dimensions
                int halfWidth = rawWidth / 2;
                int halfHeight = rawHeight / 2;

                //Calculating the down-scaling factor
                while ((halfWidth / downScalingFactorSize) >= reqdWidth
                        && (halfHeight / downScalingFactorSize) >= reqdHeight) {
                    downScalingFactorSize *= 2;
                }

            }
            //Deriving the scaling factor to downsize the image: END

            //Retrieving the actual downsized image using the options derived
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = downScalingFactorSize;

            //Publishing final progress
            publishProgress(100);

            //Returning the downsized version
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, bitmapOptions);
        }

        //Internally invoked on call to {@link #publishProgress}
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            //Retrieving the progress value
            int progressValue = values[0];

            if (mCurrentBitmapDownloadTask != null) {
                //Publishing only for the Current download task

                if (progressValue == 100) {
                    //When completed, show 100% done!
                    mDownloaderListener.syncProgress(progressValue, progressValue);
                } else {
                    //When partially done, show the proper value

                    //Calculating Secondary Progress Value
                    int secondaryProgressValue = (int) ((float) progressValue / (float) mContentLength);
                    //Sending the progress values to the listener to update the ProgressBar
                    mDownloaderListener.syncProgress((secondaryProgressValue - 10) * 100, secondaryProgressValue * 100);
                }

            }

        }

        //Internally invoked when {@link #doInBackground} completes the task
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            //Waiting for the Listener to attach if restarted
            while (mDownloaderListener == null) ;

            if (bitmap != null) {
                //Storing the Bitmap downloaded
                mDownloaderListener.onDownloadFinish(bitmap, mQuestionIndex);

                //Updating the Task state to COMPLETED
                mTaskStateStr = TaskState.TASK_STATE_COMPLETED.toString();

                //Updating the Image in Fragment
                mDownloadedBitmap = bitmap;

            } else {
                //When the image was not downloaded due to some error
                mDownloaderListener.onDownloadError(mImageURLStr, mQuestionIndex);

                //Updating the Task state to FAILED
                mTaskStateStr = TaskState.TASK_STATE_FAILED.toString();

                //Updating the Image in Fragment to null
                mDownloadedBitmap = null;
            }

        }

    }

}
