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
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL requestURL = NetworkUtils.getUrl(context);

            /* Use the URL to retrieve the JSON */
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);

            /* Parse the JSON into a list of weather values */
            ContentValues[] movieValues = OpenMovieDataUtils.getMoviePages(context, jsonWeatherResponse);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (movieValues != null && movieValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver mContentResolver = context.getContentResolver();

                /* Delete old weather data because we don't need to keep multiple days' data */
                mContentResolver.delete(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                mContentResolver.bulkInsert(
                       MoviesContract.MovieEntry.CONTENT_URI,
                        movieValues);

                /*
                 * Finally, after we insert data into the ContentProvider, determine whether or not
                 * we should notify the user that the weather has been refreshed.
                 */
                boolean notificationsEnabled = AppPreferences.areNotificationsEnabled(context);

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
                long timeSinceLastNotification = AppPreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                /*
                 * We only want to show the notification if the user wants them shown and we
                 * haven't shown a notification in the past day.
                 */
               /* if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.(context);
                }*/

            /* If the code reaches this point, we have successfully performed our sync */

            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}
