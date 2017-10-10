package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.activity.component.WelcomeActivityComponent;
import com.vantagecircle.heartrate.module.AppModule;
import com.vantagecircle.heartrate.module.UserModule;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;

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
    WelcomeActivityComponent plus(WelcomeActivityModule welcomeActivityModule);
    UserComponent plus(UserModule userModule);
}
