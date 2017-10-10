package com.vantagecircle.heartrate.activity.module;

import android.util.Log;

import com.vantagecircle.heartrate.activity.handlers.WelcomeEventHandlers;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class WelcomeActivityModule {
    private final static String TAG = WelcomeActivityModule.class.getSimpleName();
    private WelcomeActivity welcomeActivity;

    public WelcomeActivityModule(WelcomeActivity welcomeActivity) {
        Log.d(TAG, "WelcomeActivityModule");
        this.welcomeActivity = welcomeActivity;
    }

    @Provides
    @ActivityScope
    WelcomeActivity
    provideWelcomeActivity(){
        return welcomeActivity;
    }

    @Provides
    @ActivityScope
    WelcomeActivityPresenter
    provideWelcomeActivityPresenter(WelcomeActivity welcomeActivity){
        return new WelcomeActivityPresenter(welcomeActivity);
    }

    @Provides
    @ActivityScope
    WelcomeEventHandlers
    provideWelcomeEventHandler(WelcomeActivityPresenter welcomeActivityPresenter){
        return new WelcomeEventHandlers(welcomeActivityPresenter);
    }
}
