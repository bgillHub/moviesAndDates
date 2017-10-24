package com.example.android.brianspopularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gilli on 9/26/2017.
 * For the most part, just provides static values for Content Values and the SQL DB
 */

public class MoviesContract {

    public static final class MovieEntry implements BaseColumns {

        static final String CONTENT_AUTHORITY = "com.example.android.brianspopularmovies";
        static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        static final String PATH_MOVIES = "movies";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        static final String TABLE_NAME = "FAVORITES";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_ID="movie_id";

        public static String getSqlSelectForFavorites() {
        return MovieEntry.COLUMN_MOVIE_ID;
        }
    }

}
