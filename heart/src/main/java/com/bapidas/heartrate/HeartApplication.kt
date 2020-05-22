package com.bapidas.heartrate

import android.app.Application
import android.content.Context
import com.bapidas.heartrate.di.component.AppComponent
import com.bapidas.heartrate.di.component.DaggerAppComponent
import com.bapidas.heartrate.di.module.AppModule

/**
 * Created by bapidas on 09/10/17.
 */
class HeartApplication : Application() {
    var appComponent: AppComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent?.inject(this)
    }

    companion object {
        @JvmStatic
        operator fun get(context: Context): HeartApplication {
            return context.applicationContext as HeartApplication
        }
    }
}