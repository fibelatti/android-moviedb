package com.fibelatti.moviedb.helpers;

public interface IAnalyticsHelper {
    void fireLoadNewPageEvent(int pageRequested);

    void fireViewMovieDetailEvent(String movieName);
}
