package com.bapidas.heartrate.di.component

import android.app.Application
import android.content.Context
import com.bapidas.heartrate.HeartApplication
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.data.DatabaseHelper
import com.bapidas.heartrate.di.annotation.ApplicationContext
import com.bapidas.heartrate.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by bapidas on 09/10/17.
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(heartApplication: HeartApplication?)

    @get:ApplicationContext
    val context: Context
    val application: Application
    val dataManager: DataManager
    val databaseHelper: DatabaseHelper
}