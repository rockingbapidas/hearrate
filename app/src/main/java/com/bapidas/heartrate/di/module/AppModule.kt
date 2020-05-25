package com.bapidas.heartrate.di.module

import android.app.Application
import android.content.Context
import com.bapidas.heartrate.di.annotation.ApplicationContext
import dagger.Module
import dagger.Provides

/**
 * Created by bapidas on 06/10/17.
 */
@Module
class AppModule(private val mApplication: Application) {
    @Provides
    @ApplicationContext
    fun provideContext(): Context {
        return mApplication
    }

    @Provides
    fun provideApplication(): Application {
        return mApplication
    }
}