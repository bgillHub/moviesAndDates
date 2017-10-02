package com.example.android.brianspopularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gilli on 9/26/2017.
 */

public class DbHelper extends SQLiteOpenHelper{

    public static final String DB_NAME = "weather.db";
    public static final String TABLE_NAME = "Favorites";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    final String SQL_CREATE_FAVORITES =
            "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MoviesContract.MovieEntry.COLUMN_MOVIE    + " TEXT NOT NULL, " +
                    " UNIQUE (" + MoviesContract.MovieEntry.COLUMN_MOVIE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}