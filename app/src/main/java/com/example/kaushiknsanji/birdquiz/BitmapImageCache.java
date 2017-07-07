package com.example.kaushiknsanji.birdquiz;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Application level class that stores the Bitmaps downloaded in a cache memory
 *
 * @author <a href="mailto:kaushiknsanji@gmail.com">Kaushik N Sanji</a>
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
