package com.fibelatti.moviedb.presenters;

import com.fibelatti.moviedb.models.Movie;

import rx.Observable;

public interface IMainPresenter {
    void onCreate();

    void onPause();

    void onResume();

    void onDestroy();

    Observable<Movie> refresh();

    Observable<Movie> getNextPage();

    void goToMovie(int movieListIndex);
}
