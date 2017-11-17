package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.annotation.PerFragment;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;
import com.vantagecircle.heartrate.module.FragmentModule;

import dagger.Component;

/**
 * Created by bapidas on 14/11/17.
 */
@PerFragment
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(HeartFragment heartFragment);
    void inject(HistoryFragment historyFragment);
}
