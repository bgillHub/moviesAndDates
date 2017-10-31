package com.example.android.brianspopularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by gilli on 8/10/2017.
 */

class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private final List<MoviePoster> movies;
    private final String baseImgUrl = "http://image.tmdb.org/t/p/w185/";

    public void swapCursor(Cursor data) {
        notifyDataSetChanged();
    }
    class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String movieTitle;
        String movieDate;
        String movieRating;
        String moviePlot;
        String movieURL;
        int movieID;
        final ImageView movieImage;
        PosterAdapterViewHolder(View view) {
            super(view);
            movieImage = view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            try {
                new ContentFetch().execute(this);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Unable To Retrieve Movie Info", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    private void loadDetails(String passTitle, String passRating, String passDate, String url, String plot, String videoURL, int movieId, ArrayList reviews) {
        Intent intent = new Intent(MainActivity.getAppContext(), MovieDetailsActivity.class);
        intent.putExtra("passedTitle",passTitle);
        intent.putExtra("passedVote",passRating);
        intent.putExtra("passedDate",passDate);
        intent.putExtra("passedImage",url);
        intent.putExtra("passedPlot", plot);
        intent.putExtra("passedVideo", videoURL);
        intent.putExtra("passedId", movieId);
        intent.putExtra("passedReviews", reviews);
        MainActivity.getAppContext().startActivity(intent);
    }
    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        CardView view = (CardView) inflater.inflate(layoutIdForListItem, parent, false);
        return new PosterAdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(PosterAdapterViewHolder posterHolder, int position) {
        MoviePoster loadee = movies.get(position);
        posterHolder.movieTitle = loadee.title;
        posterHolder.movieDate = loadee.releaseDate;
        posterHolder.movieRating = loadee.vote;
        posterHolder.movieURL = loadee.imageURL;
        posterHolder.movieID = loadee.id;
        posterHolder.moviePlot = loadee.plot;
        Picasso.with(MainActivity.getAppContext()).load(baseImgUrl + loadee.imageURL).into(posterHolder.movieImage);
    }
    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.size();
    }
    private class ContentFetch extends AsyncTask<PosterAdapterViewHolder, Void, Void>{
        @Override
        protected Void doInBackground(PosterAdapterViewHolder... params) {
            PosterAdapterViewHolder sel = params[0];
            int n = sel.movieID;
            URL videoURL = NetworkUtils.getYouTubeUrl(MainActivity.getAppContext(), n);
            URL reviewURL = NetworkUtils.getReviewsUrl(MainActivity.getAppContext(), n);
            JSONObject ytURL = null;
            ArrayList<String> reviews = new ArrayList<>();
            String vidString = null;
            try {
                ytURL = new JSONObject(NetworkUtils.getResponseFromHttpUrl(videoURL));
                JSONObject revRes = new JSONObject(NetworkUtils.getResponseFromHttpUrl(reviewURL));
                JSONArray results = revRes.getJSONArray("results");
                for ( int i=0; i < results.length(); i ++)
                {
                    JSONObject rev = results.getJSONObject(i);
                    reviews.add(rev.getString("content"));
                }
                vidString = (String) ytURL.getJSONArray("results").getJSONObject(0).get("key");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            loadDetails(sel.movieTitle, sel.movieRating, sel.movieDate, sel.movieURL, sel.moviePlot, vidString, n, reviews);
            return null;
        }
    }
    PosterAdapter(List<MoviePoster> movies){
        this.movies = movies;
    }
}
