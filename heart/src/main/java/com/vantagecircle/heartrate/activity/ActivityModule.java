package com.vantagecircle.heartrate.activity;

import android.app.Activity;
import android.content.Context;

import com.vantagecircle.heartrate.annotation.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 14/11/17.
 */
@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }
}
