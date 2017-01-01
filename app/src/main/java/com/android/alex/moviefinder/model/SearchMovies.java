package com.android.alex.moviefinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alex on 29/12/2016.
 */

public class SearchMovies {

    @SerializedName("Search")
    private List<Movie> search;

    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }
}
