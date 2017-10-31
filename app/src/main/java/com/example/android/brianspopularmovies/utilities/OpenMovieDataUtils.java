package com.example.android.brianspopularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.brianspopularmovies.data.AppPreferences;
import com.example.android.brianspopularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by gilli on 8/15/2017.
 */

public final class OpenMovieDataUtils {

    private  OpenMovieDataUtils(){}
    private static final String OWM_MESSAGE_CODE = "cod";

    //ToDo: this method collects the previous favorites!!
    public static ContentValues[] getMoviePages(Context context, String jsonResponse) throws JSONException {
        JSONObject movieJson = new JSONObject(jsonResponse);
        /* Is there an error? */
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray jsonArray = movieJson.getJSONArray("results");
        //AppPreferences.setLocationDetails(context, cityLatitude, cityLongitude);

        ContentValues[] appContentValues = new ContentValues[jsonArray.length()];

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mObject = (JSONObject) jsonArray.get(i);
           String movieUrl = String.valueOf((int) mObject.get("id"));


            ContentValues appValues = new ContentValues();
            appValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movieUrl);

            appContentValues[i] = appValues;
        }

        return appContentValues;
    }

    public static JSONArray getMoviePagesNew(Context context, String jsonResponse) throws JSONException {
        JSONObject movieJson = new JSONObject(jsonResponse);
        /* Is there an error? */
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray jsonArray = movieJson.getJSONArray("results");

        return jsonArray;
    }
}
