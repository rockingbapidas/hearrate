package com.vantagecircle.heartrate.component;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.data.DatabaseHelper;
import com.vantagecircle.heartrate.annotation.ApplicationContext;
import com.vantagecircle.heartrate.model.Heart;
import com.vantagecircle.heartrate.module.HeartModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bapidas on 09/10/17.
 */
@Singleton
@Component(modules = {HeartModule.class})
public interface HeartComponent {
    void inject(HeartApplication heartApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    DataManager getDataManager();

    DatabaseHelper getDatabaseHelper();

    Heart getHeart();
}
