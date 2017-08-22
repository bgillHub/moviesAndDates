package com.example.android.brianspopularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.brianspopularmovies.R;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
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
        Intent intent = getIntent();
        String title = intent.getStringExtra("passedTitle");
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
            Picasso.with(MainActivity.getAppContext()).load(baseImgUrl + image).into(movieImage);
            System.out.println("Image Found");
        }
        else System.out.println("No Image Found for Activity");
    }

}
