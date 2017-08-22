package com.example.android.brianspopularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.example.android.brianspopularmovies.utilities.OpenMovieDataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static Context context;
    private ProgressBar loadingSpinner;
    private RecyclerView moviesArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        LinearLayoutManager posterLayoutManager = new LinearLayoutManager(this);
        loadingSpinner = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        moviesArea = (RecyclerView) findViewById(R.id.recyclerview_movies);
        moviesArea.setLayoutManager(posterLayoutManager);
        new MovieFetcherTask().execute("");
    }
    private class MovieFetcherTask extends AsyncTask<String, Void, JSONArray> {
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
            URL movieRequestURL = NetworkUtils.buildUrl(params[0]);

            try {
                String jsonMovies = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                return OpenMovieDataUtils
                        .getMoviePages(jsonMovies);
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
        if (id== R.id.sort_movies_name_down){
            new MovieFetcherTask().execute("original_title.asc");
            return true;
        }
        if (id== R.id.sort_movies_name_up){
            new MovieFetcherTask().execute("original_title.desc");
            return true;
        }
        if (id == R.id.sort_movies_rating){
            new MovieFetcherTask().execute("vote_average.desc");
            return true;
        }
        if (id == R.id.sort_movies_terrible){
            new MovieFetcherTask().execute("vote_average.asc");
            return true;
        }
        if (id == R.id.sort_movies_votes){
            new MovieFetcherTask().execute("vote_count.desc");
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
        }
        PosterAdapter mAdapter = new PosterAdapter(movies);
        moviesArea.setAdapter(mAdapter);
        Toast.makeText(this, "Search Complete", Toast.LENGTH_LONG).show();
    }

    public static Context getAppContext(){
        return context;
    }
}
