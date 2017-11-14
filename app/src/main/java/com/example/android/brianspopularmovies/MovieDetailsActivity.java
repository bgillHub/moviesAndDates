package com.example.android.brianspopularmovies;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.brianspopularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {
    private static String OPEN_TRAILERS;
    private static String REVIEW_STRING;
    ImageButton faveButton;
    private  boolean globalState;
    private static String OPEN_DETAILS;
    private ImageButton trailerPlay;
    private Dialog trailerDialog;
    private String reviewString = "";
    private AlertDialog detailsDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        System.out.println("Started Details Activity");
        OPEN_DETAILS = getString(R.string.open_details);
        OPEN_TRAILERS = getString(R.string.open_trailers);
        REVIEW_STRING = getString(R.string.review_string);
        TextView movieTitle = (TextView) this.findViewById(R.id.selected_movie_title);
        TextView movieVotes = (TextView) this.findViewById(R.id.selected_movie_rating);
        TextView moviePlot = (TextView) this.findViewById(R.id.selected_movie_plot);
        TextView movieDate = (TextView) this.findViewById(R.id.selected_movie_release);
        ImageView movieImage = (ImageView) this.findViewById(R.id.selected_movie_image);
        faveButton = (ImageButton) this.findViewById(R.id.selected_movie_favorite);
        globalState = false;
        trailerPlay = (ImageButton) this.findViewById(R.id.selected_movie_trailer);
        ImageButton reviewPop = (ImageButton) this.findViewById(R.id.selected_movie_reviews);
        final Intent intent = getIntent();
        final int movieId = intent.getIntExtra("passedId", -1);
        final ArrayList movieReviews =  intent.getParcelableArrayListExtra("passedReviews");
        Cursor faves = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (faves != null)
        {
            faves.moveToFirst();
            if (faves.getCount() > 0)
            {
                do
                {
                    int cursorInt = faves.getInt(0);
                    if (cursorInt == movieId)
                    {
                        globalState = true;
                        stateChange(true);
                    }
                }
                while (faves.moveToNext());
            }
            faves.close();
        }
        final ArrayList<String> urls = intent.getStringArrayListExtra("passedVideoKey");
        final ArrayList<String> titles = intent.getStringArrayListExtra("passedVideoTitle");
        if ( urls!= null)
        trailerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerDialog = new Dialog(MovieDetailsActivity.this);
                if (titles.size() <= 0)
                {
                    trailerDialog.setTitle(R.string.no_trailers);
                }
                else{
                ArrayList<VideoCard> mTrailers = new ArrayList();
                for (int i = 0; i < titles.size(); i ++){
                    mTrailers.add(new VideoCard( titles.get(i), urls.get(i)));
                }
                TrailerAdapter dialogAdapter = new TrailerAdapter(mTrailers);
                trailerDialog.setContentView(R.layout.trailer_dialog);
                RecyclerView mRecycle = (RecyclerView) trailerDialog.findViewById(R.id.trailer_list);
                mRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecycle.setAdapter(dialogAdapter);
                int i = dialogAdapter.getItemCount();
                dialogAdapter.notifyDataSetChanged();
                }
                trailerDialog.show();
                }
            }
        );
        final String title = intent.getStringExtra("passedTitle");
        faveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalState){
                    ContentValues newVal = new ContentValues();
                    newVal.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    newVal.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    int s = getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, new ContentValues[]{newVal});
                    stateChange(true);
                }
                else {
                    ContentValues newVal = new ContentValues();
                    newVal.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    int s = getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.COLUMN_MOVIE_ID,  new String[]{String.valueOf(movieId)});
                    stateChange(false);
                }
                System.out.println("Passed");
            }
        });
        reviewPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewString = "";
                for (int i = 0; i < movieReviews.size(); i++)
                {
                    reviewString+= "'"+movieReviews.get(i)+"'" + "\n\n";
                }
                if (movieReviews.size() ==0) reviewString = getString(R.string.no_reviews);
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                builder.setMessage(reviewString)
                        .setTitle(getString(R.string.reviews_title));
                detailsDialog = builder.create();
                detailsDialog.show();
            }
        });
        String vote = intent.getStringExtra("passedVote");
        String plot = intent.getStringExtra("passedPlot");
        String date = intent.getStringExtra("passedDate");
        String image = intent.getStringExtra("passedImage");
        if (title != null)
        {
            movieTitle.setText(title);
        }
        if (vote != null)
        {
            String voteString = vote + getString(R.string.rating_append);
            movieVotes.setText(voteString);
        }
        if (plot != null)
        {
            moviePlot.setText(plot);
        }
        if (date != null)
        {
            movieDate.setText(date);
        }
        if (image != null)
        {
            String baseImgUrl = "http://image.tmdb.org/t/p/w185/";
            Picasso.with(getApplicationContext()).load(baseImgUrl + image).into(movieImage);
            System.out.println("Image Found");
        }
        else System.out.println("No Image Found for Activity");
    }
    private void stateChange(boolean state) {
        if (state)
        {
        faveButton.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else {faveButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
        globalState = state;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (detailsDialog != null)
        {
            outState.putBoolean(OPEN_DETAILS,detailsDialog.isShowing());
            outState.putString(REVIEW_STRING,reviewString);
            detailsDialog.dismiss();

        }
        if (trailerDialog != null)
        {
            outState.putBoolean(OPEN_TRAILERS, trailerDialog.isShowing());
            trailerDialog.dismiss();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(OPEN_DETAILS)){
            reviewString = savedInstanceState.getString(REVIEW_STRING);
            AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
            builder.setMessage(reviewString)
                    .setTitle(getString(R.string.reviews_title));
            detailsDialog = builder.create();
            detailsDialog.show();
            detailsDialog.show();
        }
        else if (savedInstanceState.getBoolean(OPEN_TRAILERS))
        {
        trailerPlay.performClick();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
