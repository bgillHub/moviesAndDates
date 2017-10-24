package com.example.android.brianspopularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gilli on 9/26/2017.
 */

public class DbHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "movies.db";
    public static final String TABLE_NAME = "Favorites";
    private static final int DATABASE_VERSION = 3;

    DbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    final String SQL_CREATE_FAVORITES =
            "CREATE TABLE "  +MoviesContract.MovieEntry.TABLE_NAME +
                    " (" +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE    + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
