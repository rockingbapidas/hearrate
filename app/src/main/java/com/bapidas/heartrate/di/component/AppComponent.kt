package com.bapidas.heartrate.di.component

import android.app.Application
import android.content.Context
import com.bapidas.heartrate.HeartApplication
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.di.annotation.ApplicationContext
import com.bapidas.heartrate.di.module.AppModule
import com.bapidas.heartrate.di.module.DatabaseModule
import dagger.Component

/**
 * Created by bapidas on 09/10/17.
 */
@Component(modules = [AppModule::class, DatabaseModule::class])
interface AppComponent {
    fun inject(heartApplication: HeartApplication?)

    @get:ApplicationContext
    val context: Context

    val application: Application

    val dataManager: DataManager
}