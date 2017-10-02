package com.example.android.brianspopularmovies.sync;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;
/**
 * Created by gilli on 9/26/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MovieFirebaseJobService extends JobService
{
    private AsyncTask<Void, Void, Void> mFetchTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters)
    {
        mFetchTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                Context context = getApplicationContext();
                SyncTask.syncFavorites(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        if (mFetchTask != null) {
            mFetchTask.cancel(true);
        }
        return true;
    }
}
