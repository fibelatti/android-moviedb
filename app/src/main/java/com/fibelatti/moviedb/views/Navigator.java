package com.fibelatti.moviedb.views;

import android.app.Activity;
import android.content.Intent;

import com.fibelatti.moviedb.Constants;
import com.fibelatti.moviedb.models.Movie;
import com.fibelatti.moviedb.views.activities.MovieDetailActivity;

public class Navigator {
    Activity activity;

    public Navigator(Activity activity) {
        this.activity = activity;
    }

    public void startMovieDetailActivity(Movie movie) {
        Intent intent = new Intent(activity, MovieDetailActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_MOVIE, movie);
        activity.startActivity(intent);
    }
}
