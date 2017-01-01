package com.android.alex.moviefinder.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.alex.moviefinder.application.MovieApp;
import com.android.alex.moviefinder.network.ApiInterface;
import com.android.alex.moviefinder.network.MovieService;
import com.android.alex.moviefinder.R;
import com.android.alex.moviefinder.adapter.MovieAdapter;
import com.android.alex.moviefinder.model.SearchMovies;
import com.android.alex.moviefinder.util.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.search_edit)
    EditText searchMovie;

    @BindView(R.id.search_btn)
    ImageView searchBtn;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.ic_popcorn)
    ImageView icPopcorn;

    private MovieAdapter movieAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        movieAdapter = new MovieAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieAdapter);

        searchMovie.setSingleLine(true);
        searchMovie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Perform search action
                    searchBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                icPopcorn.setVisibility(View.GONE);

                String title = searchMovie.getText().toString();
                getMovieByTitleRequest(title);
                AppUtil.hideKeyboard(searchMovie);
            }
        });

    }

    private void getMovieByTitleRequest(String title) {

        Call<SearchMovies> call = MovieApp.getApiInterface().getMovieByName(title);
        call.enqueue(new Callback<SearchMovies>() {
            @Override
            public void onResponse(Call<SearchMovies> call, Response<SearchMovies> response) {
                Log.i(TAG, "onResponse");

                if (response != null) {
                    SearchMovies searchMovies = response.body();

                    if (searchMovies != null && searchMovies.getSearch() != null && searchMovies.getSearch().size() > 0) {
                        manageVisibility(true);
                        movieAdapter.swapData(searchMovies.getSearch());
                    } else {
                        manageVisibility(false);
                    }
                } else {
                    manageVisibility(false);
                }

            }

            @Override
            public void onFailure(Call<SearchMovies> call, Throwable t) {
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });
    }

    private void manageVisibility(boolean isListVisible) {

        progressBar.setVisibility(View.GONE); // always hide progress bar on result

        if (isListVisible) {
            recyclerView.setVisibility(View.VISIBLE);
            icPopcorn.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            icPopcorn.setVisibility(View.VISIBLE);
            //icPopcorn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_popcorn_not_found));
        }
    }
}
