package com.example.android.brianspopularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.brianspopularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity {
    ImageButton faveButton;
    private  boolean globalState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        System.out.println("Started Details Activity");
        TextView movieTitle = (TextView) this.findViewById(R.id.selected_movie_title);
        TextView movieVotes = (TextView) this.findViewById(R.id.selected_movie_rating);
        TextView moviePlot = (TextView) this.findViewById(R.id.selected_movie_plot);
        TextView movieDate = (TextView) this.findViewById(R.id.selected_movie_release);
        ImageView movieImage = (ImageView) this.findViewById(R.id.selected_movie_image);
        faveButton = (ImageButton) this.findViewById(R.id.selected_movie_favorite);
        globalState = false;
        ImageButton trailerPlay = (ImageButton) this.findViewById(R.id.selected_movie_trailer);
        ImageButton reviewPop = (ImageButton) this.findViewById(R.id.selected_movie_reviews);
        final Intent intent = getIntent();
        final int movieId = intent.getIntExtra("passedId", -1);
        final ArrayList movieReviews =  intent.getParcelableArrayListExtra("passedReviews");
        Cursor faves = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (faves != null) {
            faves.moveToFirst();
        while (faves.moveToNext())
        {
            if (faves.getInt(0) == movieId) {
                globalState = true;
                stateChange(true);
            }
        }
        faves.close();
        }
        final String url = intent.getStringExtra("passedVideo");
        if ( url!= null)
        trailerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + url));
                    try {
                        v.getContext().startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        v.getContext().startActivity(webIntent);
                    }
                }
            }
        );
        final String title = intent.getStringExtra("passedTitle");
        faveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    //ToDo Remove form the db
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
                String reviewString = "";
                for (int i = 0; i < movieReviews.size(); i++)
                {
                    reviewString+= "'"+movieReviews.get(i)+"'" + "\n\n";
                }
                if (movieReviews.size() ==0) reviewString = getString(R.string.no_reviews);
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetails.this);
                builder.setMessage(reviewString)
                        .setTitle(getString(R.string.reviews_title));
                AlertDialog dialog = builder.create();
                dialog.show();
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
            movieVotes.setText(vote);
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

}
