package com.fibelatti.moviedb.helpers;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class AnalyticsHelper
        implements IAnalyticsHelper {
    private final String ANALYTICS_KEY_LOADED_NEW_PAGE = "Requested a new page";
    private final String ANALYTICS_PARAM_PAGE_NUMBER = "Page number";

    private final String ANALYTICS_KEY_VIEW_MOVIE_DETAILS = "Movie Details";
    private final String ANALYTICS_PARAM_MOVIE_NAME = "Movie name";

    private static AnalyticsHelper instance;

    private AnalyticsHelper() {
    }

    public static AnalyticsHelper getInstance() {
        if (instance == null) {
            instance = new AnalyticsHelper();
        }
        return instance;
    }

    @Override
    public void fireLoadNewPageEvent(int pageRequested) {
        Answers.getInstance().logCustom(new CustomEvent(ANALYTICS_KEY_LOADED_NEW_PAGE)
                .putCustomAttribute(ANALYTICS_PARAM_PAGE_NUMBER, pageRequested));
    }

    @Override
    public void fireViewMovieDetailEvent(String movieName) {
        Answers.getInstance().logCustom(new CustomEvent(ANALYTICS_KEY_VIEW_MOVIE_DETAILS)
                .putCustomAttribute(ANALYTICS_PARAM_MOVIE_NAME, movieName));
    }
}
