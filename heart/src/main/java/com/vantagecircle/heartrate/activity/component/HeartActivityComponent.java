package com.vantagecircle.heartrate.activity.component;

import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.fragment.component.HeartFragmentComponent;
import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by bapidas on 09/10/17.
 */
@ActivityScope
@Subcomponent(
        modules = {
                HeartActivityModule.class
        }
)
public interface HeartActivityComponent {
    HeartActivity inject(HeartActivity heartActivity);
}
