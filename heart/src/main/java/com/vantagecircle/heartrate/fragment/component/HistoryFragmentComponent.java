package com.vantagecircle.heartrate.fragment.component;

import com.vantagecircle.heartrate.activity.component.HeartActivityComponent;
import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.fragment.module.HistoryFragmentModule;
import com.vantagecircle.heartrate.fragment.ui.HistoryFragment;
import com.vantagecircle.heartrate.scope.PerFragment;

import dagger.Component;

/**
 * Created by bapidas on 08/11/17.
 */
@PerFragment
@Component(dependencies = HeartActivityComponent.class, modules = HistoryFragmentModule.class)
public interface HistoryFragmentComponent {
    HistoryFragment inject(HistoryFragment historyFragment);
}
