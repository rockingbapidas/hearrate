package com.vantagecircle.heartrate.activity.module;

import android.content.Context;

import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.scope.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class HeartActivityModule {
    private HeartActivity mHeartActivity;

    public HeartActivityModule(HeartActivity heartActivity) {
        this.mHeartActivity = heartActivity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mHeartActivity;
    }

    @Provides
    HeartActivity provideHeartActivity() {
        return mHeartActivity;
    }
}
