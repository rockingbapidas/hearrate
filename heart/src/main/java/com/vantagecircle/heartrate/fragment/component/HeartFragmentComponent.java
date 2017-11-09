package com.vantagecircle.heartrate.fragment.component;

import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.scope.FragmentScope;

import dagger.Subcomponent;

/**
 * Created by bapidas on 08/11/17.
 */
@FragmentScope
@Subcomponent(
        modules = {
                HeartFragmentModule.class
        }
)
public interface HeartFragmentComponent {
    HeartFragment inject(HeartFragment heartFragment);
}
