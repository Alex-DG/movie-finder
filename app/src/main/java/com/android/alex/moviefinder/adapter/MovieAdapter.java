package com.android.alex.moviefinder.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.alex.moviefinder.R;
import com.android.alex.moviefinder.activity.DetailsActivity;
import com.android.alex.moviefinder.model.Movie;
import com.android.alex.moviefinder.network.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> dataset;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public @BindView(R.id.card_view) CardView cardView;
        public @BindView(R.id.movie_poster) ImageView poster;
        public @BindView(R.id.movie_title) TextView title;
        public @BindView(R.id.movie_year) TextView year;
        public @BindView(R.id.movie_type) TextView type;
        public @BindView(R.id.progress_bar) ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MovieAdapter(Activity activity) {
        this.activity = activity;
    }

    public MovieAdapter(List<Movie> myDataset) {
        dataset = myDataset;
    }

    public void swapData(List<Movie> movieList) {

        if (dataset == null) {
            dataset = new ArrayList<>();
        }

        dataset.clear();
        dataset.addAll(movieList);
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (dataset != null) {

            final Movie movie = dataset.get(position);
            if (movie != null) {

                // Load movie poster
                Picasso.with(activity).load(movie.getPosterURL())
                        .into(holder.poster,
                                new ImageLoadedCallback(holder.progressBar, holder.poster));

                holder.title.setText(movie.getTitle());
                holder.year.setText(movie.getYear());
                holder.type.setText("Type: " + movie.getType());

                // Open details activity
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.IMDB_ID, movie.getImdbID());
                        intent.putExtra(DetailsActivity.MOVIE_TITLE, movie.getTitle());
                        activity.startActivity(intent);
                    }
                });
            }
        }

    }

    public int getItemCount() {
        return dataset.size();
    }
}

