package com.bapidas.heartrate.di.component

import com.bapidas.heartrate.di.annotation.PerFragment
import com.bapidas.heartrate.di.module.FragmentModule
import com.bapidas.heartrate.ui.fragment.ui.HeartFragment
import com.bapidas.heartrate.ui.fragment.ui.HistoryFragment
import dagger.Component

/**
 * Created by bapidas on 14/11/17.
 */
@PerFragment
@Component(dependencies = [AppComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(heartFragment: HeartFragment)

    fun inject(historyFragment: HistoryFragment)
}