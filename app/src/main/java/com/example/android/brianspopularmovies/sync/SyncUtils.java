package com.example.android.brianspopularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.brianspopularmovies.data.MoviesContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import com.firebase.jobdispatcher.Driver;
import java.util.concurrent.TimeUnit;

/**
 * Created by gilli on 9/26/2017.
 */

public class SyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String SYNC_TAG = "movies-sync";

    private static boolean sInitialized;


    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context)
    {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job syncJob = dispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncJob);
    }

    synchronized public static void initialize(@NonNull final Context context)
    {
        if (sInitialized) return;
        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri movieQueryUri = MoviesContract.MovieEntry.CONTENT_URI;
                String[] projectionColumns = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};
                String selectionStatement = MoviesContract.MovieEntry.getSqlSelectForFavorites();
                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                    cursor.close();
                    Log.d("CURSOR","MAKIN CURSOR");
                }
                else cursor.close();
            }
        });
        System.out.print("Fail?");
        checkForEmpty.start();
    }

    private static void startImmediateSync(@NonNull final Context context)
    {
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);

    }
}
