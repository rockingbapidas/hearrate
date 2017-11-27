package com.vantagecircle.heartrate.module;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.annotation.ApplicationContext;
import com.vantagecircle.heartrate.annotation.DatabaseInfo;
import com.vantagecircle.heartrate.annotation.HeartData;
import com.vantagecircle.heartrate.model.Heart;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 06/10/17.
 */
@Module
public class HeartModule {
    private Application mApplication;
    private String gender;
    private int year;

    public HeartModule(Application application) {
        this.mApplication = application;
    }

    public HeartModule(Application application, String gender, int year) {
        this.mApplication = application;
        this.gender = gender;
        this.year = year;
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

    @Provides
    @HeartData
    String provideGender() {
        if (gender == null) {
            return "male";
        } else {
            return gender;
        }
    }

    @Provides
    @HeartData
    Integer provideBirthYear() {
        if (year == 0) {
            return 1991;
        } else {
            return year;
        }
    }
}
