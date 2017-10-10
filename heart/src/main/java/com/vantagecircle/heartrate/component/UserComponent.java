package com.vantagecircle.heartrate.component;

import com.vantagecircle.heartrate.activity.component.HeartActivityComponent;
import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.module.UserModule;
import com.vantagecircle.heartrate.scope.UserScope;

import dagger.Subcomponent;

/**
 * Created by bapidas on 09/10/17.
 */
@UserScope
@Subcomponent(
        modules = {
                UserModule.class
        }
)
public interface UserComponent {
    HeartActivityComponent plus (HeartActivityModule heartActivityModule);
}
