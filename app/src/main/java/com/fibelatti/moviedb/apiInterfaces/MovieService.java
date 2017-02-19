package com.fibelatti.moviedb.apiInterfaces;

import com.fibelatti.moviedb.models.Movie;
import com.fibelatti.moviedb.models.Search;
import com.fibelatti.moviedb.models.SearchVideo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieService {
    String BASE_URL = "https://api.themoviedb.org/3/";
    String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500";
    String LANGUAGE_ENGLISH = "en-US";

    @GET("movie/popular")
    Observable<Search> searchPopularMovies(
            @Query("api_key") String api_key,
            @Query("language") String language,
            @Query("page") Integer page
    );

    @GET("movie/{movie_id}")
    Observable<Movie> getMovieById(
            @Path("movie_id") Integer movieId,
            @Query("api_key") String api_key,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/videos")
    Observable<SearchVideo> getMovieVideos(
            @Path("movie_id") Integer movieId,
            @Query("api_key") String api_key,
            @Query("language") String language
    );
}
