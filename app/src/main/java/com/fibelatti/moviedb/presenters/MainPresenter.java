package com.fibelatti.moviedb.presenters;

import android.app.Activity;
import android.content.Context;

import com.fibelatti.moviedb.BuildConfig;
import com.fibelatti.moviedb.apiInterfaces.MovieService;
import com.fibelatti.moviedb.helpers.ServicesHelper;
import com.fibelatti.moviedb.models.Movie;
import com.fibelatti.moviedb.models.Search;
import com.fibelatti.moviedb.views.Navigator;

import rx.Observable;

public class MainPresenter
        implements IMainPresenter {

    private Context context;
    private Navigator navigator;

    private int currentPage;

    private MainPresenter(Context context) {
        this.context = context;
    }

    public static MainPresenter createPresenter(Context context) {
        return new MainPresenter(context);
    }

    @Override
    public void onCreate() {
        this.currentPage = 1;
        this.navigator = new Navigator((Activity) context);
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
    public Observable<Movie> refresh() {
        this.currentPage = 1;
        return ServicesHelper
                .getMovieService()
                .searchPopularMovies(
                        BuildConfig.MOVIEDB_API_KEY,
                        MovieService.LANGUAGE_ENGLISH,
                        currentPage)
                .flatMapIterable(Search::getResults)
                .flatMap(searchResult -> ServicesHelper
                        .getMovieService()
                        .getMovieById(
                                searchResult.getId(),
                                BuildConfig.MOVIEDB_API_KEY,
                                MovieService.LANGUAGE_ENGLISH
                        ));
    }

    @Override
    public Observable<Movie> getNextPage() {
        this.currentPage++;
        return ServicesHelper
                .getMovieService()
                .searchPopularMovies(
                        BuildConfig.MOVIEDB_API_KEY,
                        MovieService.LANGUAGE_ENGLISH,
                        currentPage)
                .flatMapIterable(Search::getResults)
                .flatMap(searchResult -> ServicesHelper
                        .getMovieService()
                        .getMovieById(
                                searchResult.getId(),
                                BuildConfig.MOVIEDB_API_KEY,
                                MovieService.LANGUAGE_ENGLISH
                        ));
    }

    @Override
    public void goToMovie(Movie movie) {
        navigator.startMovieDetailActivity(movie);
    }
}
