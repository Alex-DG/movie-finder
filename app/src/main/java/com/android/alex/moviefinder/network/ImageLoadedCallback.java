package com.android.alex.moviefinder.network;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;


public class ImageLoadedCallback implements Callback {

    private ProgressBar progressBar;
    private ImageView imageView;

    public  ImageLoadedCallback(ProgressBar progressBar, ImageView imageView){
        this.progressBar = progressBar;
        this.imageView = imageView;
    }


    @Override
    public void onSuccess() {
        manageVisibility();

    }

    @Override
    public void onError() {
        manageVisibility();
        imageView.setImageResource(android.R.drawable.ic_menu_report_image);
    }

    private void manageVisibility() {
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }
}