package com.vantagecircle.heartrate.activity.component;

import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by bapidas on 09/10/17.
 */
@ActivityScope
@Subcomponent(
        modules = {
                WelcomeActivityModule.class
        }
)
public interface WelcomeActivityComponent {
    WelcomeActivity inject(WelcomeActivity welcomeActivity);
}
