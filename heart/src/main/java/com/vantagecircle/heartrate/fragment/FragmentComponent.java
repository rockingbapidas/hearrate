package com.vantagecircle.heartrate.fragment;

import com.vantagecircle.heartrate.AppComponent;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;
import com.vantagecircle.heartrate.annotation.PerFragment;

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
