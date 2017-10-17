package com.example.android.brianspopularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
    private ProgressBar loadingSpinner;
    private static RecyclerView moviesArea;
    private static final int ID_LOADER = 44;
    private static PosterAdapter mPosterAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    public static final String[] MAIN_MOVIE = {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
        mPosterAdapter = new PosterAdapter(null);
        loadingSpinner = (ProgressBar) findViewById(R.id.pb_loading_indicator);
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
                /* URI for all rows of weather data in our weather table */
                Uri mQueryUri = MoviesContract.MovieEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
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
        mPosterAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        moviesArea.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) try {
            showMovieCards(new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //ToDo: Add params for long term settings?
        new MovieLoader(this,0);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movies_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_movies_rating:
              //  mLoader.CHOSEN = 1;
                //mLoader.loadInBackground();
                new MovieLoader(this,1);
                return true;
            case R.id.sort_movies_votes:
              //  mLoader.CHOSEN = 2;
              //  mLoader.loadInBackground();
                new MovieLoader(this,2);
                return true;
            default:
              //  ToDo: Load up the favorites
              //  mLoader.loadInBackground();
                new MovieLoader(this,0);
                return true;
        }
    }
    private void showErrorMessage(){
        Toast.makeText(this, "There Was An Error!", Toast.LENGTH_LONG).show();
    }
    private static void showMovieCards(JSONArray movieData) throws JSONException {
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

    class MovieLoader extends AsyncTaskLoader{

        private Boolean dataIsReady;
        int CHOSEN;
        private  int loadOption = 1;
        MovieLoader(Context context, int param) {
            super(context);
            CHOSEN = param;
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                URL movieRequestURL = NetworkUtils.buildUrl(CHOSEN);

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
