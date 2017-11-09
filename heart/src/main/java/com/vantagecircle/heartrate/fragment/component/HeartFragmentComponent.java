package com.vantagecircle.heartrate.fragment.component;

import com.vantagecircle.heartrate.activity.component.HeartActivityComponent;
import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.scope.PerFragment;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by bapidas on 08/11/17.
 */
@PerFragment
@Component(dependencies = HeartActivityComponent.class, modules = HeartFragmentModule.class)
public interface HeartFragmentComponent {
    HeartFragment inject(HeartFragment heartFragment);
}
