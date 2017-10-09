package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.activity.WelcomeComponent;
import com.vantagecircle.heartrate.module.AppModule;
import com.vantagecircle.heartrate.module.HeartModule;
import com.vantagecircle.heartrate.activity.WelcomeModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bapidas on 09/10/17.
 */
@Singleton
@Component(
        modules = {
                AppModule.class
        }
)
public interface AppComponent {
    WelcomeComponent plus(WelcomeModule welcomeModule);
    HeartComponent plus(HeartModule heartModule);
}
