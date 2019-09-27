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

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Application level class that stores the Bitmaps downloaded in a cache memory
 *
 * @author Kaushik N Sanji
 */
public class BitmapImageCache {

    //Declaring the Memory Cache for Bitmaps
    private static LruCache<String, Bitmap> mMemoryCache;

    static {
        //Static Constructor invoked only for the first time when loaded

        //Initializing the Memory Cache to have a max of two entries
        //one for the current question and the other for the following question
        mMemoryCache = new LruCache<>(2);
    }

    /**
     * Method to retrieve the Bitmap Image from the Memory Cache for the given Image URL
     *
     * @param imageURLStr The Image URL string whose Bitmap needs to be retrieved from Memory Cache
     * @return Bitmap containing the Image for the Image URL mentioned
     */
    public static Bitmap getBitmapFromCache(String imageURLStr) {
        return mMemoryCache.get(imageURLStr);
    }

    /**
     * Method that adds Bitmap Image to Memory Cache with the Image URL string as the Key
     *
     * @param imageURLStr The Image URL string source of the Bitmap used as a
     *                    Key to store in the Memory Cache
     * @param bitmap      Bitmap containing the Image downloaded from the URL passed
     */
    public static void addBitmapToCache(String imageURLStr, Bitmap bitmap) {
        if (getBitmapFromCache(imageURLStr) == null
                && bitmap != null) {
            mMemoryCache.put(imageURLStr, bitmap);
        }
    }

    /**
     * Method that clears the entire Memory Cache
     */
    public static void clearCache() {
        mMemoryCache.evictAll();
    }

}
