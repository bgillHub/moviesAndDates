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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.brianspopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;



/**
 * Created by gilli on 8/10/2017.
 */

class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private final List<MoviePoster> movies;
    private final String baseImgUrl = "http://image.tmdb.org/t/p/w185/";
    private Cursor mCursor;

    public void swapCursor(Cursor data) {
        mCursor = data;
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
            movieImage = (ImageView) view.findViewById(R.id.movie_image);
            //ToDo: Onclick, go to moviedetails activity with the poster as an extra
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            try {
                new TrailerFetch().execute(this);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Unable To Retrive Movie Info", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void loadDetails(String passTitle, String passRating, String passDate, String url, String plot, String videoURL, int movieId) {
        Intent intent = new Intent(MainActivity.getAppContext(), MovieDetails.class);
        intent.putExtra("passedTitle",passTitle);
        intent.putExtra("passedVote",passRating);
        intent.putExtra("passedDate",passDate);
        intent.putExtra("passedImage",url);
        intent.putExtra("passedPlot", plot);
        intent.putExtra("passedVideo", videoURL);
        intent.putExtra("passedId", movieId);
        MainActivity.getAppContext().startActivity(intent);
    }

    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
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
        // posterHolder.movieImage.setImageURI(movies.get(position).imageURL);
    }


    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.size();
    }

    private class TrailerFetch extends AsyncTask<PosterAdapterViewHolder, Void, Void>{


        @Override
        protected Void doInBackground(PosterAdapterViewHolder... params) {
            PosterAdapterViewHolder sel = params[0];
            int n = sel.movieID;
            URL videoURL = NetworkUtils.getYouTubeUrl(MainActivity.getAppContext(), n);
            JSONObject ytURL = null;
            String vidString = null;
            try {
                ytURL = new JSONObject(NetworkUtils.getResponseFromHttpUrl(videoURL));
                vidString = (String) ytURL.getJSONArray("results").getJSONObject(0).get("key");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadDetails(sel.movieTitle, sel.movieRating, sel.movieDate, sel.movieURL, sel.moviePlot, vidString, n);
            return null;
        }

    }

    PosterAdapter(List<MoviePoster> movies){
        this.movies = movies;
    }
}
