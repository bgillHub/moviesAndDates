package com.example.android.brianspopularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.brianspopularmovies.data.AppPreferences;
import com.example.android.brianspopularmovies.data.MoviesContract;
import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.example.android.brianspopularmovies.utilities.NotificationUtils;
import com.example.android.brianspopularmovies.utilities.OpenMovieDataUtils;

import java.net.URL;

/**
 * Created by gilli on 9/26/2017.
 */

public class SyncTask {
    synchronized public static void syncFavorites(Context context) {

        try {
                ContentResolver mContentResolver = context.getContentResolver();

                /* Delete old weather data because we don't need to keep multiple days' data */
                mContentResolver.delete(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null);


        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}
