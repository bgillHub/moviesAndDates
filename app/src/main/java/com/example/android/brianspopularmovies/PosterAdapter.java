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

import org.w3c.dom.Text;

import java.net.URI;
import java.util.List;

import static com.example.android.brianspopularmovies.R.id.movie_title;

/**
 * Created by gilli on 8/10/2017.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {

    List<MoviePoster> movies;
    String baseImgUrl = "http://image.tmdb.org/t/p/w185/";
    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView moviecv;
        TextView movieTitle;
        TextView movieVotes;
        TextView moviePlot;
        TextView movieDate;
        ImageView movieImage;
        URI imageURL;

        public PosterAdapterViewHolder(View view) {
            super(view);
            movieTitle = (TextView) view.findViewById(movie_title);
            movieVotes = (TextView) view.findViewById(R.id.movie_rating);
            moviePlot = (TextView) view.findViewById(R.id.movie_plot);
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
            loadDetails(v);
        }
    }

    private void loadDetails(View v) {
        TextView title = (TextView) v.findViewById(R.id.movie_title);
        Intent intent = new Intent(MainActivity.getAppContext(), MovieDetails.class);
        MainActivity.getAppContext().startActivity(intent);
    }

    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        CardView view = (CardView) inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new PosterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterAdapterViewHolder posterHolder, int position) {
        MoviePoster loadee = movies.get(position);
        posterHolder.movieTitle.setText(loadee.title);
        posterHolder.moviePlot.setText(loadee.plot);
        posterHolder.movieVotes.setText(loadee.vote);
        posterHolder.movieDate.setText(loadee.releaseDate);
        Picasso.with(MainActivity.getAppContext()).load(baseImgUrl + loadee.imageURL).into(posterHolder.movieImage);
        // posterHolder.movieImage.setImageURI(movies.get(position).imageURL);
    }


    @Override
    public int getItemCount() {
        if (movies == null) return 0;
        return movies.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    PosterAdapter(List<MoviePoster> movies){
        this.movies = movies;
    }
}
