package com.vantagecircle.heartrate.module;

import android.app.Application;

import com.vantagecircle.heartrate.scope.ApplicationScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 06/10/17.
 */
@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    Application
    provideApplication() {
        return application;
    }
}
