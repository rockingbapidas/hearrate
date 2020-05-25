package com.bapidas.heartrate.di.module

import android.content.Context
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.data.DatabaseHelper
import com.bapidas.heartrate.di.annotation.ApplicationContext
import com.bapidas.heartrate.di.annotation.DatabaseInfo
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return "heart-rate.db"
    }

    @Provides
    @DatabaseInfo
    fun provideDatabaseVersion(): Int {
        return 1
    }

    @Provides
    fun provideDatabaseHelper(
        @ApplicationContext context: Context,
        @DatabaseInfo name: String,
        @DatabaseInfo version: Int
    ): DataManager {
        return DataManager(DatabaseHelper(context, name, version))
    }
}