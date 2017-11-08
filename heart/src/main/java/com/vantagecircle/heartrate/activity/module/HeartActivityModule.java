package com.vantagecircle.heartrate.activity.module;

import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class HeartActivityModule {
    private HeartActivity heartActivity;

    public HeartActivityModule(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    @Provides
    @ActivityScope
    HeartActivity
    provideHeartActivity() {
        return heartActivity;
    }

    @Provides
    @ActivityScope
    HeartActivityPresenter
    provideHeartActivityPresenter(HeartActivity heartActivity) {
        return new HeartActivityPresenter(heartActivity);
    }
}
