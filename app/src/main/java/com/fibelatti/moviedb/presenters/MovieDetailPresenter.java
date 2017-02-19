package com.fibelatti.moviedb.presenters;

import android.content.Context;

import com.fibelatti.moviedb.BuildConfig;
import com.fibelatti.moviedb.apiInterfaces.MovieService;
import com.fibelatti.moviedb.helpers.ServicesHelper;
import com.fibelatti.moviedb.models.SearchVideo;
import com.fibelatti.moviedb.models.SearchVideoResult;

import rx.Observable;

public class MovieDetailPresenter
        implements IMovieDetailPresenter {

    private Context context;

    private MovieDetailPresenter(Context context) {
        this.context = context;
    }

    public static MovieDetailPresenter createPresenter(Context context) {
        return new MovieDetailPresenter(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Observable<SearchVideoResult> getVideo(int movieId) {
        return ServicesHelper
                .getMovieService()
                .getMovieVideos(
                        movieId,
                        BuildConfig.MOVIEDB_API_KEY,
                        MovieService.LANGUAGE_ENGLISH)
                .flatMapIterable(SearchVideo::getResults)
                .filter(searchVideoResult -> searchVideoResult.getType().equals("Trailer"))
                .first();
    }
}
