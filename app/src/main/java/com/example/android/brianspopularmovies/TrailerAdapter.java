package com.example.android.brianspopularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gilli on 11/6/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>  {

private final List<VideoCard> thumbnails;


class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView trailerImage;
    TextView trailerLabel;
    TrailerAdapterViewHolder(View view) {
        super(view);
        trailerImage = view.findViewById(R.id.trailer_image);
        trailerLabel = view.findViewById(R.id.trailer_title);
        view.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        try {

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Unable To Retrieve Movie Info", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        CardView view = (CardView) inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder thumbnailHolder, int position) {
        VideoCard loadee = thumbnails.get(position);
        String title = loadee.title;
        thumbnailHolder.trailerLabel.setText(title);
        final String vidKey = loadee.vidKey;
        String imageUrl = "http://img.youtube.com/vi/" + vidKey + "/0.jpg";
        Picasso.with(MainActivity.getAppContext()).load(imageUrl).into(thumbnailHolder.trailerImage);
        thumbnailHolder.trailerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vidKey));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + vidKey));
                try {
                    v.getContext().startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    v.getContext().startActivity(webIntent);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        if (thumbnails == null) return 0;
        return thumbnails.size();
    }
    TrailerAdapter(List<VideoCard> thumbnails){
        this.thumbnails = thumbnails;
    }
}