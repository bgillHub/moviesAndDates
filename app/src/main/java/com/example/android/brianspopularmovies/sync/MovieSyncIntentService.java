package com.example.android.brianspopularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by gilli on 9/26/2017.
 */

public class MovieSyncIntentService extends IntentService {
    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncTask.syncFavorites(this);
    }
}
