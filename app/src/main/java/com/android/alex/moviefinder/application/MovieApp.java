package com.android.alex.moviefinder.application;

import android.app.Application;

import com.android.alex.moviefinder.network.ApiInterface;
import com.android.alex.moviefinder.network.MovieService;

/**
 * Created by Alex on 29/12/2016.
 */

public class MovieApp extends Application {

    private static MovieApp movieApp;
    private static ApiInterface apiInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        movieApp = this;
        apiInterface = MovieService.getClient().create(ApiInterface.class);
    }

    public static MovieApp getInstance() {
        return movieApp;
    }

    public static ApiInterface getApiInterface() {
        return apiInterface;
    }
}
