package com.bapidas.heartrate.di.component

import com.bapidas.heartrate.di.annotation.PerActivity
import com.bapidas.heartrate.di.module.ActivityModule
import com.bapidas.heartrate.ui.activity.ui.HeartActivity
import dagger.Component

/**
 * Created by bapidas on 14/11/17.
 */
@PerActivity
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(heartActivity: HeartActivity?)
}