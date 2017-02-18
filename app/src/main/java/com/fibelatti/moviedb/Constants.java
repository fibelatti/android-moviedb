package com.fibelatti.moviedb;

import java.util.Arrays;
import java.util.List;

public interface Constants {
    String PLAY_STORE_BASE_URL = "http://play.google.com/store/apps/details";

    String INTENT_EXTRA_MOVIE_ID = "com.fibelatti.moviedb.MOVIE_ID";
    String INTENT_EXTRA_MOVIE = "com.fibelatti.moviedb.MOVIE";

    String LOCALE_EN = "en";
    String LOCALE_PT = "pt";
    String LOCALE_ES = "es";

    List<String> SUPPORTED_LOCALES = Arrays.asList(LOCALE_EN, LOCALE_PT, LOCALE_ES);
}
