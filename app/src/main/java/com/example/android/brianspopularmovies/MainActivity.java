package com.example.android.brianspopularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.example.android.brianspopularmovies.utilities.OpenMovieDataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar loadingSpinner;
    private  RecyclerView.Adapter posterAdapter;
    private RecyclerView moviesArea;
    private Button loader;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        LinearLayoutManager posterLayoutManager = new LinearLayoutManager(this);
        loadingSpinner = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesArea = (RecyclerView) findViewById(R.id.recyclerview_movies);
        moviesArea.setAdapter(posterAdapter);
        moviesArea.setLayoutManager(posterLayoutManager);
        new MovieFetcherTask().execute("");
    }
    public class MovieFetcherTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("GETTING READY");
            loadingSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String movies = params[0];
            URL movieRequestURL = NetworkUtils.buildUrl(movies);

            try {
                String jsonMovies = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                JSONArray moviePagesData = OpenMovieDataUtils
                        .getMovieCards(MainActivity.this, jsonMovies);

                return moviePagesData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray movies) {
            loadingSpinner.setVisibility(View.INVISIBLE);
            if (movies != null) {
                try {
                    showMovieCards(movies);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
                System.out.print("JSON RESPONSE: "+ movies);
            } else {
                showErrorMessage();
            }
        }
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
        int id = item.getItemId();

        if (id == R.id.refresh_movies) {
            new MovieFetcherTask().execute("");
            return true;
        }
        if (id== R.id.sort_movies_name){
            new MovieFetcherTask().execute("");
            return true;
        }
        if (id == R.id.sort_movies_rating){
            new MovieFetcherTask().execute("");
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            String posterPath = singleDatum.getString("poster_path");
            String release = singleDatum.getString("release_date");
            String plot = singleDatum.getString("overview");
            int vote = singleDatum.getInt("vote_average");
            movies.add(new MoviePoster(title, posterPath, release, vote, plot));
            System.out.println("Poster Added: " + title);
            System.out.println("Poster Added: " + release);
            System.out.println("Poster Added: " + plot);
        }
        PosterAdapter mAdapter = new PosterAdapter(movies);
        moviesArea.setAdapter(mAdapter);
        Toast.makeText(this, "Complete!", Toast.LENGTH_LONG);
    }

    public static  Context getAppContext(){
        return context;
    }
}
