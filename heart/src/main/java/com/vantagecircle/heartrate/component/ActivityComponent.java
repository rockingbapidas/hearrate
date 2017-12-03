package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.activity.ui.MainActivity;
import com.vantagecircle.heartrate.module.ActivityModule;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.annotation.PerActivity;

import dagger.Component;

/**
 * Created by bapidas on 14/11/17.
 */
@PerActivity
@Component(dependencies = HeartComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(WelcomeActivity welcomeActivity);
    void inject(MainActivity mainActivity);
}
