package com.example.android.brianspopularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by gilli on 9/26/2017.
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIES = 100;

    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of WeatherProvider and is a
     * common convention in Android programming.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] params, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {
            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.android.sunshine/weather/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
            case CODE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        params,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @Nullable ContentValues[] values)  {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        String movieTitle = value.getAsString(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE);
                        int movieId =
                                value.getAsInteger(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /* Users of the delete method will expect the number of rows deleted to be returned. */
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numRowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                db.beginTransaction();
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        selection+"= ?",
                        selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.MovieEntry.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Sunshine, we use CODE_WEATHER or CODE_WEATHER_WITH_DATE.
         */

        /* This URI is content://com.example.android.sunshine/weather/ */
        matcher.addURI(authority, MoviesContract.MovieEntry.PATH_MOVIES, CODE_MOVIES);

        /*
         * This URI would look something like content://com.example.android.sunshine/weather/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
         * that it should return the CODE_WEATHER_WITH_DATE code
         */

        return matcher;
    }

}
