package com.vantagecircle.heartrate.module;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.scope.ApplicationContext;
import com.vantagecircle.heartrate.scope.DatabaseInfo;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 06/10/17.
 */
@Module
public class AppModule {
    private Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }


    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return "heart-rate.db";
    }

    @Provides
    @DatabaseInfo
    Integer provideDatabaseVersion() {
        return 1;
    }
}
