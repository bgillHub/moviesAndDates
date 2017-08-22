package com.example.android.brianspopularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.brianspopularmovies.R.id.movie_rating;
import static com.example.android.brianspopularmovies.R.id.movie_release;
import static com.example.android.brianspopularmovies.R.id.movie_title;

/**
 * Created by gilli on 8/10/2017.
 */

class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    private final List<MoviePoster> movies;
    private final String baseImgUrl = "http://image.tmdb.org/t/p/w185/";
    class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView movieTitle;
        final TextView movieVotes;
        String moviePlot;
        String movieURL;
        final TextView movieDate;
        final ImageView movieImage;

        PosterAdapterViewHolder(View view) {
            super(view);
            movieTitle = (TextView) view.findViewById(movie_title);
            movieVotes = (TextView) view.findViewById(R.id.movie_rating);
            movieDate = (TextView) view.findViewById(R.id.movie_release);
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
            loadDetails(v, movieURL, moviePlot);
        }
    }

    private void loadDetails(View v, String url, String plot) {
        Intent intent = new Intent(MainActivity.getAppContext(), MovieDetails.class);
        String passTitle = (String) ((TextView) v.findViewById(movie_title)).getText();
        String passRating =(String) ((TextView) v.findViewById(movie_rating)).getText();
        String passDate = (String) ((TextView) v.findViewById(movie_release)).getText();
        intent.putExtra("passedTitle",passTitle);
        intent.putExtra("passedVote",passRating);
        intent.putExtra("passedDate",passDate);
        intent.putExtra("passedImage",url);
        intent.putExtra("passedPlot", plot);
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
        posterHolder.movieTitle.setText(loadee.title);
        posterHolder.movieVotes.setText(loadee.vote);
        posterHolder.movieDate.setText(loadee.releaseDate);
        posterHolder.movieURL = loadee.imageURL;
        posterHolder.moviePlot = loadee.plot;
        Picasso.with(MainActivity.getAppContext()).load(baseImgUrl + loadee.imageURL).into(posterHolder.movieImage);
        // posterHolder.movieImage.setImageURI(movies.get(position).imageURL);
    }


    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.size();
    }

    PosterAdapter(List<MoviePoster> movies){
        this.movies = movies;
    }
}
