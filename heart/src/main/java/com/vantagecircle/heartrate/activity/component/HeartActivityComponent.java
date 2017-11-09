package com.vantagecircle.heartrate.activity.component;

import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.component.AppComponent;
import com.vantagecircle.heartrate.scope.ActivityContext;
import com.vantagecircle.heartrate.scope.PerActivity;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by bapidas on 09/10/17.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = HeartActivityModule.class)
public interface HeartActivityComponent {
    void inject(HeartActivity heartActivity);
}
