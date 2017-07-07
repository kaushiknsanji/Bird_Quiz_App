# Quiz of Aves

This App has been developed as part of the **Udacity Android Basics Nanodegree Course** for the Exercise Project **"Quiz App"**. **Quiz of Aves** is a _Quiz on Birds_. 

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

* `android.os.AsyncTask` for downloading the images for each of the questions. `Fragment` has been used for managing this Custom `AsyncTask`.
* `android.util.LruCache` for caching the Bitmaps downloaded.
* `android.os.CountDownTimer` for the Quiz Timer. `Fragment` has been used for managing the `CountDownTimer`, designed as a latch that adds functionality such as _Pause_ and _Resume_.
* `DialogFragment` to display the Number Picker Dialog for the user to select/enter the number of questions to attempt.
* `DialogFragment` for displaying the Progress of Image Download, with a custom progress bar layout.
* `DialogFragment` for displaying the Final score at the end of the quiz or when the quiz timer elapses.
* Custom Fonts/Typefaces have been used in Dialogs.
* Vector images have been used in certain places.
* Mipmap images for the App icons.
* Intents for moving from one activity to the other.
* Id resource for the components generated programmatically.
* Nine patch images used as a background image for the question and option fields.
* Level List Drawable for decorating the options.
* State list drawable of shape drawables with gradient for the Submit/Hint buttons.
* String array resources for storing the questions, their options and keys.

---

## Design and working of the App

The first screen displayed when the app is launched, is the welcome screen as shown below. This displays the important information regarding the quiz. 

The questions can be choice based questions *(single/multi select option)* and also *textual questions*. When the question is choice based, it can have a max of *4* selectable options. 

---

### Changes done post submission

---

### Changes planned post submission

* Need to move the `` contents to Database and fetch the content from database.
