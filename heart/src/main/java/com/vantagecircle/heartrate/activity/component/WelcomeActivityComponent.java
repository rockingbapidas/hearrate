package com.vantagecircle.heartrate.activity.component;

import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.component.AppComponent;
import com.vantagecircle.heartrate.scope.ActivityContext;
import com.vantagecircle.heartrate.scope.PerActivity;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by bapidas on 09/10/17.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = WelcomeActivityModule.class)
public interface WelcomeActivityComponent {
    void inject(WelcomeActivity welcomeActivity);
}
