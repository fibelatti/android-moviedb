package com.fibelatti.moviedb;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

public class MovieDbApplication
        extends Application {
    public static final String TAG = MovieDbApplication.class.getSimpleName();
    public static MovieDbApplication app;

    public MovieDbApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(false)
                        .build()
                ).build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
