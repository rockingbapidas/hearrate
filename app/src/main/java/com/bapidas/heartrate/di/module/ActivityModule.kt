package com.bapidas.heartrate.di.module

import android.app.Activity
import android.content.Context
import com.bapidas.heartrate.di.annotation.ActivityContext
import dagger.Module
import dagger.Provides

/**
 * Created by bapidas on 14/11/17.
 */
@Module
class ActivityModule(private val mActivity: Activity) {
    @Provides
    @ActivityContext
    fun provideContext(): Context {
        return mActivity
    }

    @Provides
    fun provideActivity(): Activity {
        return mActivity
    }
}