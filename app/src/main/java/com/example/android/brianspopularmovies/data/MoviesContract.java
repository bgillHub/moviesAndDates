package com.example.android.brianspopularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gilli on 9/26/2017.
 */

public class MoviesContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String CONTENT_AUTHORITY = "";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_WEATHER = "movies";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        public static final String TABLE_NAME = "FAVORITES";


        public static final String COLUMN_MOVIE = "movie_url";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_MOVIE_RATING="movie_rating";
        public static final String COLUMN_MOVIE_POSTER="movie_poster";
        public static final String COLUMN_MOVIE_TRAILER="movie_trailer";



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
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForFavorites() {
        return "";
        }
    }

}
