package com.vantagecircle.heartrate;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.data.DatabaseHelper;
import com.vantagecircle.heartrate.scope.ApplicationContext;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bapidas on 09/10/17.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(HeartApplication heartApplication);
    @ApplicationContext
    Context getContext();
    Application getApplication();
    DataManager getDataManager();
    DatabaseHelper getDatabaseHelper();
}