package com.fibelatti.moviedb.helpers;

import com.fibelatti.moviedb.apiInterfaces.MovieService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServicesHelper {
    private static final Object syncLock = new Object();

    private static Retrofit retrofit;
    private static MovieService movieService;

    private ServicesHelper() {
    }

    public static MovieService getMovieService() {
        if (movieService == null) {
            synchronized (syncLock) {
                if (movieService == null) {
                    if (retrofit == null) setUpRetrofit();
                    movieService = retrofit.create(MovieService.class);
                }
            }
        }
        return movieService;
    }

    private static void setUpRetrofit() {
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MovieService.BASE_URL)
                .build();
    }
}
