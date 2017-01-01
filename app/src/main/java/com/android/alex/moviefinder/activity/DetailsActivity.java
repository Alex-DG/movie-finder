package com.android.alex.moviefinder.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.alex.moviefinder.dialog.LoadingDialog;
import com.android.alex.moviefinder.R;
import com.android.alex.moviefinder.application.MovieApp;
import com.android.alex.moviefinder.model.Movie;
import com.android.alex.moviefinder.network.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 30/12/2016.
 */

public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = "DetailsActivity";
    public static final String IMDB_ID = "IMDB_ID";
    public static final String MOVIE_TITLE = "MOVIE_TITLE";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;

    // Top informations
    @BindView(R.id.detail_run_time)
    TextView runTime;
    @BindView(R.id.detail_genre)
    TextView genre;
    @BindView(R.id.detail_released)
    TextView released;

    // Body informations
    @BindView(R.id.detail_poster)
    ImageView poster;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.detail_rating)
    TextView rating;
    @BindView(R.id.detail_votes)
    TextView votes;
    @BindView(R.id.detail_plot)
    TextView plot;
    @BindView(R.id.detail_director)
    TextView director;
    @BindView(R.id.detail_writer)
    TextView writer;
    @BindView(R.id.detail_actors)
    TextView actors;
    @BindView(R.id.detail_awards)
    TextView awards;

    private LoadingDialog loadingDialog;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        String imdbId = getIntent().getStringExtra(IMDB_ID);
        String title = getIntent().getStringExtra(MOVIE_TITLE) == null ? "" : getIntent().getStringExtra(MOVIE_TITLE);

        collapsingToolbar.setTitle(title);

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (poster.getDrawable() != null) {
                    zoomImageFromThumb(poster, poster.getDrawable());
                }
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        getMovieByID(imdbId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // Go back on main activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMovieByID(String id) {

        Call<Movie> call = MovieApp.getApiInterface().getMovieByID(id);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Log.i(TAG, "onResponse");
                loadingDialog.dismiss();

                if (response != null) {
                    Movie movie = response.body();
                    if (movie != null) {
                        showDetails(movie);
                    }
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.i(TAG, "onFailure : " + t.getMessage());
                loadingDialog.dismiss();
            }
        });
    }

    private void showDetails(Movie movie) {

        // Movie poster
        Picasso.with(this).load(movie.getPosterURL())
                .into(poster,
                        new ImageLoadedCallback(progressBar, poster));

        // Others details
        runTime.setText(movie.getRuntime());
        genre.setText(movie.getGenre());
        released.setText(movie.getYear());

        rating.setText(movie.getImdbRating() + "/10");
        votes.setText("(votes: " + movie.getImdbVotes() + ")");

        plot.setText(movie.getPlot());

        director.setText(movie.getDirector());
        writer.setText(movie.getWriter());
        actors.setText(movie.getActors());
        awards.setText(movie.getAwards());
    }

    private void zoomImageFromThumb(final View thumbView, Drawable imgDrawable) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        expandedImageView.setImageDrawable(imgDrawable);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}
