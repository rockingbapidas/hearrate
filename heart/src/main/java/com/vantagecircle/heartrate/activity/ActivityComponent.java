package com.vantagecircle.heartrate.activity;

import com.vantagecircle.heartrate.AppComponent;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.scope.PerActivity;

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
