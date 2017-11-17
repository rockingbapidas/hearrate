package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.module.ActivityModule;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.annotation.PerActivity;

import dagger.Component;

/**
 * Created by bapidas on 14/11/17.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(WelcomeActivity welcomeActivity);
    void inject(HeartActivity heartActivity);
}
