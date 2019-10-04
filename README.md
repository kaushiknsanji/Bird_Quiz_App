# Quiz of Aves

![GitHub](https://img.shields.io/github/license/kaushiknsanji/Bird_Quiz_App)  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/Bird_Quiz_App)  ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/Bird_Quiz_App)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/kaushiknsanji/Bird_Quiz_App)  ![GitHub All Releases](https://img.shields.io/github/downloads/kaushiknsanji/Bird_Quiz_App/total) ![GitHub search hit counter](https://img.shields.io/github/search/kaushiknsanji/Bird_Quiz_App/Quiz%20App) ![Minimum API level](https://img.shields.io/badge/API-16+-yellow)

This App has been developed as part of the **Udacity Android Basics Nanodegree Course** for the Exercise Project **"Quiz App"**. **Quiz of Aves** is a _Quiz on Birds_. 

---

## App Compatibility
Android device running with Android OS 4.1 (API Level 16) or above. Designed for Phones and NOT for Tablets.

---

## Rubric followed for the Project

* Questions should be in a variety of formats such as free text response, checkboxes, and radio buttons.
* Checkboxes are only used for questions with multiple right answers. Radio buttons are only used for questions with a single right answer. Any question which uses radio buttons allows only one to be checked at once.
* App includes a button for the user to submit their answers and receive a score. 
* Code adheres to all of the best layout practices.
* App includes at least four of the following Views: TextView, ImageView, Button, Checkbox, EditText, LinearLayout, RelativeLayout, ScrollView, RadioButton, RadioGroup.
* App gracefully handles displaying all the content on screen when rotated. 
* Each question has a correct answer.
* App contains at least one if/else statement.
* Grading button displays a toast which accurately displays the results of the quiz. The grading logic checks each answer correctly.
* App contains 4 - 10 questions, including at least one check box, one radio button, and one text entry. 
 
---

### Things explored/developed in addition to the above defined Rubric

* `android.os.AsyncTask` for downloading the images for each of the questions. Headless [`Fragment`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/ImageDownloaderTaskFragment.java) has been used for managing this Custom `AsyncTask`.
* [`android.util.LruCache`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/BitmapImageCache.java) for caching the Bitmaps downloaded.
* `android.os.CountDownTimer` for the Quiz Timer. Headless [`Fragment`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/CountDownLatchFragment.java) has been used for managing the `CountDownTimer`, designed as a latch that adds functionality such as _Pause_ and _Resume_.
* [`DialogFragment`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/QuestionNumberPickerDialogFragment.java) to display the Number Picker Dialog for the user to select/enter the number of questions to attempt.
* [`DialogFragment`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/ProgressDialogFragment.java) for displaying the Progress of Image Download, with a custom progress bar layout.
* [`DialogFragment`](/app/src/main/java/com/example/kaushiknsanji/birdquiz/FinalScoreDialogFragment.java) for displaying the Final score at the end of the quiz or when the quiz timer elapses.
* Custom Fonts/Typefaces have been used in Dialogs.
* Vector images have been used in certain places.
* Mipmap images for the App icons.
* Intents for moving from one activity to the other.
* [Id resource](/app/src/main/res/values/ids.xml) for the components generated programmatically.
* Nine patch images used as a background image for the question and option fields.
* [Level List Drawable](/app/src/main/res/drawable/option_level_list.xml) for decorating the options.
* [State List Drawable](/app/src/main/res/drawable/button_state_selector.xml) of shape drawables with gradient for the Submit/Hint buttons.
* [String array](/app/src/main/res/values/quiz_strings.xml) resources for storing the questions, their options and keys.

---

## Design and Implementation of the App

The first screen displayed when the app is launched, is the welcome screen as shown below. This displays the important information regarding the quiz.

#### The Welcome screen layout

<img src="https://user-images.githubusercontent.com/26028981/27983052-4a1ff1fe-63d1-11e7-913b-d06c095d5001.png" width="40%" />

The [layout](/app/src/main/res/layout/activity_welcome.xml) behaviour is controlled by the [WelcomeActivity](/app/src/main/java/com/example/kaushiknsanji/birdquiz/WelcomeActivity.java). When the **"BEGIN QUIZ"** button is clicked, the question number picker dialog is launched where the user selects the number of questions to be attempted. The number of questions can also be manually entered. If the entered value is out-of-range, it simply ignores the value and stays at the selected value.

<img src="https://user-images.githubusercontent.com/26028981/27983062-83839f18-63d1-11e7-8a93-4dca3efcc554.png" width="40%" />

On Click of `Cancel` button, a toast message is shown prompting the user to select the number of questions to attempt. This will not dismiss the dialog and has been done _intentionally_ so that the user attempts some number of questions before quiting :stuck_out_tongue_winking_eye:

On Click of `Set` button, the user is taken to the quiz [layout](/app/src/main/res/layout/activity_quiz.xml) controlled by the [QuizActivity](/app/src/main/java/com/example/kaushiknsanji/birdquiz/QuizActivity.java).

#### The Quiz screen layout

<img src="https://user-images.githubusercontent.com/26028981/27983098-5776c2c8-63d2-11e7-93c0-94ec3127a312.png" width="40%" />     <img src="https://user-images.githubusercontent.com/26028981/27983093-31a29e6e-63d2-11e7-8950-278cfcc91cc4.png" width="40%" />

The Current Question Number is shown in the top left corner, with the current score in the top right corner. In the Footer section we have the Quiz timer implemented using the `android.os.CountDownTimer` managed by the Headless Fragment [CountDownLatchFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/CountDownLatchFragment.java) to enable additional functionality such as _Pause_ and _Resume_. The timer value is set accordingly to the number of questions selected by the user, by allocating 45 seconds for each question, that is, `timer value = No. Of Questions * 45`.

Below the Question component, are the MCQ options/textual `EditText` option that appear based on the question. Below this, are the buttons **SUBMIT** and **SHOW HINT**. The **SHOW HINT** button always appears disabled for every question, as for every question user has two chances to get the right answer. On the first incorrect attempt, **SHOW HINT** button and its related components are enabled.

Above the Question component is the Hidden Image that displays the Hint Image for the question. This will be the picture of the Bird, that the user needs to identify and answer accordingly. The Hint Image is shown when the **SHOW HINT** button is clicked.

When the Hint Image is not yet downloaded, or during the initial launch when the images are being downloaded and cached, the above progress bar dialog will be shown. _The timer will be paused(internally canceled) in such cases and will be resumed once done_. The Progress dialog shown is as per the layout designed [here](/app/src/main/res/layout/progress_bar_layout.xml) managed by the DialogFragment [ProgressDialogFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/ProgressDialogFragment.java). The images are downloaded for the current and its following question using the `android.os.AsyncTask` managed by the Headless Fragment [ImageDownloaderTaskFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/ImageDownloaderTaskFragment.java). At every question, the current image and the next image are kept in `android.util.LruCache` [BitmapImageCache](/app/src/main/java/com/example/kaushiknsanji/birdquiz/BitmapImageCache.java) which is used to restore the images during configuration changes.

_All the questions, options and keys are loaded from the [String array](/app/src/main/res/values/quiz_strings.xml) resources. The images for the MCQ-Checkbox based questions are loaded from the `/app/src/main/res/drawable` resource._

_As images are downloaded for each question, it is recommended to use the app in a non-metered connection with good connectivity. If the connectivity is bad, it will notify the user in a toast message, and in such cases for every question user will notice that the app is **freezing** leading to possible ANRs. This will happen since the ping test is done in the UI Thread, OOPS!!_

#### Textual Based Questions
For Textual based questions, the textual response is acquired using the `EditText` component as shown below. 

<img src="https://user-images.githubusercontent.com/26028981/27983103-691345a6-63d2-11e7-9ff6-0895d233b813.png" width="40%" />

When the question is answered correctly, a toast will be shown saying it is correct and the **SUBMIT** button changes to **NEXT**. Image is also revealed in this case for the user to enjoy ogling at the bird's beauty :stuck_out_tongue_winking_eye:

<img src="https://user-images.githubusercontent.com/26028981/27983108-8845a3c4-63d2-11e7-882f-89d480178cbc.png" width="40%" />   <img src="https://user-images.githubusercontent.com/26028981/27983110-8d73dafa-63d2-11e7-8918-869415f5dd2f.png" width="40%" />

_In case the user was pretty fast in answering the question and the image was not yet downloaded, the image will not be shown and the pending download will be cancelled with a toast message saying it could not complete. This is applicable for any kind of question._

#### MCQ-RadioButton Based Questions

<img src="https://user-images.githubusercontent.com/26028981/27983113-aa24d442-63d2-11e7-8494-2e6f48612f0b.png" width="40%" />   <img src="https://user-images.githubusercontent.com/26028981/27983123-c660b5ea-63d2-11e7-8759-42b1473cc03c.png" height="40%" width="70%"/>

The RadioButton Options appear one below the other in a portrait layout while in the landscape layout, it appears as a table of RadioButtons. _There will be a maximum of 4 options in MCQ based questions applicable for both RadioButton and Checkbox based questions. The layouts for CheckBox is designed in the same lines as that of RadioButton._

<img src="https://user-images.githubusercontent.com/26028981/27983127-dfeea940-63d2-11e7-93d3-478374710a25.png" width="40%" />    <img src="https://user-images.githubusercontent.com/26028981/27983145-fddb99d6-63d2-11e7-9c17-322105649d46.png" width="40%" />

[Level List Drawables](/app/src/main/res/drawable/option_level_list.xml) of Nine patch images are used as a background for the options of MCQ based questions. When selected, the color of the option turns orange indicating it is the user selected answer, and if the answer turns out to be correct, the color of the options turns green. This is accomplised manually by setting the level of the drawable accordingly through the `CompoundButton.OnCheckedChangeListener` attached to the options.

<img src="https://user-images.githubusercontent.com/26028981/27983154-5b8f71ba-63d3-11e7-8249-87f2c5d2651a.png" width="40%" />    <img src="https://user-images.githubusercontent.com/26028981/27983158-6e38f732-63d3-11e7-9ee8-ffc6f55cb97a.png" width="40%" />

On the first incorrect attempt, user will be able use the **SHOW HINT** button that gets enabled. On click of this, a textbox with a hint message and a button adjacent to it will be shown. The button here which is the `assistant` button (or the flag button), will scroll up to the hint image when clicked. On the second incorrect attempt, actual answer will be revealed in green along side the user selected answer shown in orange.

#### MCQ-CheckBox Based Questions

<img src="https://user-images.githubusercontent.com/26028981/27983163-82ae1b0c-63d3-11e7-9b3e-040d99f3b65d.png" width="40%" />    <img src="https://user-images.githubusercontent.com/26028981/27983165-92307bba-63d3-11e7-8bda-3cfc2e8cd0f2.png" width="40%" />

For CheckBox Based Questions, users can select mutiple options based on which the selected answers are evaluated. The design is similar to RadioButtons as discussed above and also has the `CompoundButton.OnCheckedChangeListener` attached to the options for changing the level of the drawable.

#### Information in general for all types of questions
* Whether it is a textual based question or an MCQ of the type RadioButton or CheckBox, once the answer is submitted and the **SUBMIT** button changes to **NEXT**, the options/components are disabled to prevent any further changes especially cosmetic changes.
* For MCQ based questions, there can be a maximum of 4 options with a minimum of 2 options per question.
* The EditText field for the textual based questions, the RadioButtons and CheckBoxes for MCQs are programmatically generated components. The Ids for such fields are generated and used by employing the [Id resource](/app/src/main/res/values/ids.xml).
* If the following question happens to be a RadioButton based and the current question was also RadiButton based, then when the next question appears, these RadioButtons will be reused with the text values changed. Same is applicable for CheckBox based question. If the following question has less number of options, extra option components will be deleted accordingly (vice-versa, the components will be added).
* If the following question happens to be a question with textual response and the current question was also a question with textual response, then the EditText field will be reused post the text content being reset.

#### When the Quiz Completes

Once the last question is answered, the **SUBMIT** button changes to **FINISH**. On click of **FINISH**, the following score dialog will be shown and the timer will be _Paused_(internally cancelled).

<img src="https://user-images.githubusercontent.com/26028981/27983169-a14aace2-63d3-11e7-9539-6c179ccccb3f.png" width="40%" />

The [layout](/app/src/main/res/layout/final_score_layout.xml) of the score dialog is managed by the DialogFragment [FinalScoreDialogFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/FinalScoreDialogFragment.java). The Dialog displays the final score with a brief message. This message changes with the percentage range of the score. The Dialog has two buttons -
- **QUIT** button will quit from the application by invoking finish on the `QuizActivity`.
- **RETAKE QUIZ** button will restart the app by launching the `WelcomeActivity`.

#### When the Quiz Timer times out

<img src="https://user-images.githubusercontent.com/26028981/27983170-b322ee98-63d3-11e7-9ced-72a5f9e801f4.png" width="40%" />

If the Quiz timer elapsed before the completion of the quiz, the final score will be displayed with the message as shown above.

#### Information in general, on the entire app
* Back is disabled _intentionally_ for all the dialogs. Dismiss on touch is also disabled.
* Back on activity will destroy/finish the activity/app.
* If home button is pressed, then the app will continue to run in the background. _**The Quiz timer will not be paused in this case which is done intentionally. It will continue to run until the user quits the application using back button.**_
* The quiz questions and its options are randomized.

---

## Branches in this Repository

* **[master](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/master)**
	* Contains the code submitted for review, along with any review suggestions incorporated.
* **[release_v1.0](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0)**
	* Removed Ping Test while evaluating Network Connectivity.
	* Modified to use the support version of `Fragment`, `DialogFragment`, `FragmentManager` and `AlertDialog`.
	* Made the inner class `ImageDownloaderTask` of [ImageDownloaderTaskFragment](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/java/com/example/kaushiknsanji/birdquiz/ImageDownloaderTaskFragment.java) as Static to avoid possible memory leaks, with related required changes. Optimized image downloads by reducing the required size of the Image and using parallel execution of AsyncTasks. Fixed bugs related to Downloaded Image not showing after configuration change.
	* Used String resources for Score and Question format, which is displayed in the [QuizActivity](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/java/com/example/kaushiknsanji/birdquiz/QuizActivity.java).
	* Made the inner class `MyCountDownTimer` of [CountDownLatchFragment](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/java/com/example/kaushiknsanji/birdquiz/CountDownLatchFragment.java) Static to avoid possible memory leaks, with required related changes.
	* Used optimized URLs for Images for faster loading of Images.
	* Added Null checks for possible Null Pointers.
	* Enabled logging for debuggable build types only, through the use of custom [Logger](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/java/com/example/kaushiknsanji/birdquiz/Logger.java) which is a wrapper to the `android.util.Log`.
	* Modified to use `app:srcCompat` for loading drawables in ImageView.
	* Fixed the [final_score_layout](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/res/layout/final_score_layout.xml) of [FinalScoreDialogFragment](https://github.com/kaushiknsanji/Bird_Quiz_App/tree/release_v1.0/app/src/main/java/com/example/kaushiknsanji/birdquiz/FinalScoreDialogFragment.java) which was not displaying the final score properly.
	* Recreated App Icons.
	* Other minor changes to prepare the app for local release.
	
---

## Review from the Reviewer (Udacity)

![Review_Quiz_App](https://user-images.githubusercontent.com/26028981/65774028-16a9a180-e15b-11e9-8c83-449bf942946b.PNG)

---

## License

```
Copyright 2017 Kaushik N. Sanji

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
