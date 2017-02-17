package com.fibelatti.moviedb;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.fibelatti.raffler.db.Database;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class MovieDbApplication
        extends Application {
    public static final String TAG = TemplateApplication.class.getSimpleName();
    public static MovieDbApplication app;
    public static Database db;

    public MovieDbApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(Database.settingsDao.getCrashReportEnabled())
                        .build()
                ).build());
    }

    @Override
    public void onTerminate() {
        db.close();
        super.onTerminate();
    }
}
