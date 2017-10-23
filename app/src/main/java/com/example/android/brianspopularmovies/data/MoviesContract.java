package com.example.android.brianspopularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gilli on 9/26/2017.
 */

public class MoviesContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String CONTENT_AUTHORITY = "com.example.android.brianspopularmovies";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_MOVIES = "movies";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final String TABLE_NAME = "FAVORITES";


        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_ID="movie_id";

        /**
         * @param movieurl Normalized date in milliseconds
         * @return Uri to query details about a single movie
         */
        public static Uri buildWeatherUriWithDate(String movieurl) {
            return CONTENT_URI.buildUpon()
                    .appendPath(movieurl)
                    .build();
        }

        /**

         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForFavorites() {
        return MovieEntry.COLUMN_MOVIE_ID;
        }
    }

}
