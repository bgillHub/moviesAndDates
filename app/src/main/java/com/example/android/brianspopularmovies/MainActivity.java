package com.example.android.brianspopularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.brianspopularmovies.data.MoviesContract;
import com.example.android.brianspopularmovies.sync.SyncUtils;
import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.example.android.brianspopularmovies.utilities.OpenMovieDataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>
{
    private static Context context;
    private  RecyclerView moviesArea;
    private static final int ID_LOADER = 44;
    private static PosterAdapter mPosterAdapter;
    public static final String[] MAIN_MOVIE = {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
    };
    public static ArrayList<Integer> favorites;

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_LOADER, null, this);
        SyncUtils.initialize(this);
        new MovieLoader(this, 1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
        mPosterAdapter = new PosterAdapter(null);
        moviesArea = (RecyclerView) findViewById(R.id.recyclerview_movies);
        moviesArea.setAdapter(mPosterAdapter);
        moviesArea.setHasFixedSize(true);
        moviesArea.setLayoutManager(new GridLayoutManager(this, 3));
        getSupportLoaderManager().initLoader(ID_LOADER, null, this);
        SyncUtils.initialize(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_LOADER:
                Uri mQueryUri = MoviesContract.MovieEntry.CONTENT_URI;
                String sortOrder = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
                String selection = MoviesContract.MovieEntry.getSqlSelectForFavorites();
                return new CursorLoader(this,
                        mQueryUri,
                        MAIN_MOVIE,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        data.moveToFirst();
        favorites = new ArrayList<>();
        try {
            while (data.moveToNext()) {
                favorites.add(data.getInt(0));
            }
        } finally {
            data.close();
        }
        new MovieLoader(this, 1);

        //ToDo: Add params for long term settings?

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            case R.id.sort_movies_rating:
                new MovieLoader(this,1);
                return true;
            case R.id.sort_movies_votes:
                new MovieLoader(this,2);
                return true;
            default:
                new MovieLoader(this,0);
                return true;
        }
    }
    private void showErrorMessage(){
        Toast.makeText(this, "There Was An Error!", Toast.LENGTH_LONG).show();
    }
    private void showMovieCards(JSONArray movieData) throws JSONException {
        ArrayList<MoviePoster> movies = new ArrayList<>();
        System.out.println("Showing cards!");
        for (int i = 0; i < movieData.length(); i++){
           // movies.add(i);
            JSONObject singleDatum = movieData.getJSONObject(i);
            String title = singleDatum.getString("title");
            int id = singleDatum.getInt("id");
            String posterPath = singleDatum.getString("poster_path");
            String release = singleDatum.getString("release_date");
            String plot = singleDatum.getString("overview");
            int vote = singleDatum.getInt("vote_average");
            movies.add(new MoviePoster(title, posterPath, release, vote, plot,id));
        }
        mPosterAdapter = new PosterAdapter(movies);
        moviesArea.setAdapter(mPosterAdapter);
        Toast.makeText(getAppContext(), "Search Complete", Toast.LENGTH_LONG).show();
    }

    public static Context getAppContext(){
        return context;
    }

    private class MovieLoader extends AsyncTaskLoader{

        private Boolean dataIsReady;
        int CHOSEN =1;
        MovieLoader(Context context, int param) {
            super(context);
            CHOSEN = param;
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            if (CHOSEN==0) {
                try {
                    final JSONArray movies = new JSONArray();
                    for (int i :favorites)
                    {
                    URL movieRequestURL = NetworkUtils.getSingleURL(context, i);
                    JSONObject movieJSON = new JSONObject(NetworkUtils
                            .getResponseFromHttpUrl(movieRequestURL));
                    movies.put(movieJSON);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showMovieCards(movies);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                return true;
            }
            else{
                try {
                URL movieRequestURL = NetworkUtils.getUrl(context, CHOSEN);

                String jsonMovies = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);
                final JSONArray m = OpenMovieDataUtils
                        .getMoviePagesNew(context, jsonMovies);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showMovieCards(m);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
            }
        }
        @Override
        protected void onStartLoading() {
            if(dataIsReady) {
                deliverResult(data);
            } else {
                forceLoad();
            }
        }
        @Override
        protected void onStopLoading() {
            cancelLoad();
            dataIsReady = true;
        }
    }

}
