package com.vantagecircle.heartrate.fragment.module;

import android.content.Context;

import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;
import com.vantagecircle.heartrate.scope.ActivityContext;
import com.vantagecircle.heartrate.scope.ApplicationContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 08/11/17.
 */
@Module
public class HistoryFragmentModule {
    private HistoryFragment mHistoryFragment;

    public HistoryFragmentModule(HistoryFragment mHistoryFragment) {
        this.mHistoryFragment = mHistoryFragment;
    }

    @Provides
    @ActivityContext
    Context provideContext(){
        return mHistoryFragment.getActivity();
    }

    @Provides
    HistoryFragment provideHeartFragment(){
        return mHistoryFragment;
    }
}
