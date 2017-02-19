package com.fibelatti.moviedb.presenters;

import com.fibelatti.moviedb.models.SearchVideoResult;

import rx.Observable;

public interface IMovieDetailPresenter {
    void onCreate();

    void onPause();

    void onResume();

    void onDestroy();

    Observable<SearchVideoResult> getVideo(int movieId);
}
