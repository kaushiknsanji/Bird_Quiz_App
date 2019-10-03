# Quiz of Aves - v1.0

![GitHub](https://img.shields.io/github/license/kaushiknsanji/Bird_Quiz_App) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/Bird_Quiz_App) ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/Bird_Quiz_App) ![GitHub Releases](https://img.shields.io/github/downloads/kaushiknsanji/Bird_Quiz_App/v1.0/total)

This is the Release version 1.0 of the **Quiz of Aves** App.

## Changes done in this Release

* Removed Ping Test while evaluating Network Connectivity.
* Modified to use the support version of `Fragment`, `DialogFragment`, `FragmentManager` and `AlertDialog`.
* Made the inner class `ImageDownloaderTask` of [ImageDownloaderTaskFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/ImageDownloaderTaskFragment.java) as Static to avoid possible memory leaks, with related required changes. Optimized image downloads by reducing the required size of the Image and using parallel execution of AsyncTasks. Fixed bugs related to Downloaded Image not showing after configuration change.
* Used String resources for Score and Question format, which is displayed in the [QuizActivity](/app/src/main/java/com/example/kaushiknsanji/birdquiz/QuizActivity.java).
* Made the inner class `MyCountDownTimer` of [CountDownLatchFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/CountDownLatchFragment.java) Static to avoid possible memory leaks, with required related changes.
* Used optimized URLs for Images for faster loading of Images.
* Added Null checks for possible Null Pointers.
* Enabled logging for debuggable build types only, through the use of custom [Logger](/app/src/main/java/com/example/kaushiknsanji/birdquiz/Logger.java) which is a wrapper to the `android.util.Log`.
* Modified to use `app:srcCompat` for loading drawables in ImageView.
* Fixed the [final_score_layout](/app/src/main/res/layout/final_score_layout.xml) of [FinalScoreDialogFragment](/app/src/main/java/com/example/kaushiknsanji/birdquiz/FinalScoreDialogFragment.java) which was not displaying the final score properly.
* Recreated App Icons.

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
