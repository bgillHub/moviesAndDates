package com.example.android.brianspopularmovies;


/**
 * Created by gilli on 8/15/2017.
 */

class MoviePoster {
    final String title;
    final String imageURL;
    final String releaseDate;
    final String vote;
    final String plot;
    final int id;

    MoviePoster(String title, String imageURL, String releaseDate, int vote, String plot, int id){
        this.title = title;
        this.imageURL = imageURL;
        this.releaseDate = releaseDate;
        this.vote = String.valueOf(vote);
        this.plot = plot;
        this.id = id;
    }
}
