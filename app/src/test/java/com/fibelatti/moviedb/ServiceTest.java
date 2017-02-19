package com.fibelatti.moviedb;

import android.content.Context;
import android.util.Log;

import com.fibelatti.moviedb.apiInterfaces.MovieService;
import com.fibelatti.moviedb.helpers.ServicesHelper;
import com.fibelatti.moviedb.models.Movie;
import com.fibelatti.moviedb.models.Search;
import com.fibelatti.moviedb.presenters.IMainPresenter;
import com.fibelatti.moviedb.presenters.IMovieDetailPresenter;
import com.fibelatti.moviedb.presenters.MainPresenter;
import com.fibelatti.moviedb.presenters.MovieDetailPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

public class ServiceTest {
    @Mock
    Context fakeContext;
    IMainPresenter mainPresenter;
    IMovieDetailPresenter movieDetailPresenter;

    @Before
    public void setUp() throws Exception {
        // Override RxJava schedulers
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());

        // Override RxAndroid schedulers
        final RxAndroidPlugins rxAndroidPlugins = RxAndroidPlugins.getInstance();
        rxAndroidPlugins.registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        mainPresenter = MainPresenter.createPresenter(fakeContext);
        movieDetailPresenter = MovieDetailPresenter.createPresenter(fakeContext);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();

        mainPresenter = null;
        movieDetailPresenter = null;
    }

    @Test
    public void fetchMoviesWithApiKey() throws Exception {
        // Adding sleep to avoid HTTP 429 when running all tests
        Thread.sleep(3000);

        List<Movie> movies = new ArrayList<>();

        mainPresenter.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    assertEquals(movies.isEmpty(), false);
                    assertEquals(movies.size(), 20);
                })
                .subscribe(
                        movies::add,
                        throwable -> Log.e("Error", throwable.getMessage())
                );
    }

    @Test
    public void fetchMoviesWithoutApiKey() throws Exception {
        // Adding sleep to avoid HTTP 429 when running all tests
        Thread.sleep(3000);

        List<Movie> movies = new ArrayList<>();

        ServicesHelper
                .getMovieService()
                .searchPopularMovies(
                        "",
                        MovieService.LANGUAGE_ENGLISH,
                        1)
                .flatMapIterable(Search::getResults)
                .flatMap(searchResult -> ServicesHelper
                        .getMovieService()
                        .getMovieById(
                                searchResult.getId(),
                                "",
                                MovieService.LANGUAGE_ENGLISH
                        ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    assertEquals(movies.isEmpty(), true);
                    assertEquals(movies.size(), 0);
                })
                .subscribe(
                        movies::add,
                        throwable -> assertEquals(throwable.getMessage().isEmpty(), false)
                );
    }

    @Test
    public void fetchVideo() throws Exception {
        // Adding sleep to avoid HTTP 429 when running all tests
        Thread.sleep(3000);

        List<Movie> movies = new ArrayList<>();

        mainPresenter.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    assertEquals(movies.isEmpty(), false);
                    assertEquals(movies.size(), 20);

                    movieDetailPresenter.getVideo(movies.get(0).getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    searchVideoResult -> {
                                        assertEquals(searchVideoResult.getKey().isEmpty(), false);
                                    },
                                    throwable -> Log.e("Error", throwable.getMessage())
                            );
                })
                .subscribe(
                        movies::add,
                        throwable -> Log.e("Error", throwable.getMessage())
                );
    }
}