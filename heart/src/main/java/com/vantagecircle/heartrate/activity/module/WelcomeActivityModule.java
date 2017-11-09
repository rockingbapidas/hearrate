package com.vantagecircle.heartrate.activity.module;

import android.content.Context;

import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.scope.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class WelcomeActivityModule {
    private WelcomeActivity mWelcomeActivity;

    public WelcomeActivityModule(WelcomeActivity welcomeActivity) {
        this.mWelcomeActivity = welcomeActivity;
    }

    @Provides
    @ActivityContext
    Context
    provideContext() {
        return mWelcomeActivity;
    }

    @Provides
    WelcomeActivity
    provideWelcomeActivity() {
        return mWelcomeActivity;
    }
}
