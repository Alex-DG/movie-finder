package com.android.alex.moviefinder.network;

import com.android.alex.moviefinder.model.Movie;
import com.android.alex.moviefinder.model.SearchMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Alex on 27/12/2016.
 */

public interface ApiInterface {

    @GET("/")
    Call<SearchMovies> getMovieByName(@Query("s") String search);


    @GET("/")
    Call<Movie> getMovieByID(@Query("i") String id);
}
