package com.example.android.brianspopularmovies;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;
import java.net.URL;

/**
 * Created by gilli on 8/15/2017.
 */

public class MoviePoster {
    String title;
    String imageURL;
    String releaseDate;
    String vote;
    String plot;

    MoviePoster(String title, String imageURL, String releaseDate, int vote, String plot){
        this.title = title;
        this.imageURL = imageURL;
        this.releaseDate = releaseDate;
        this.vote = String.valueOf(vote);
        this.plot = plot;
    }
}
