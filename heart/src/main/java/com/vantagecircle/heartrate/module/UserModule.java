package com.vantagecircle.heartrate.module;

import com.vantagecircle.heartrate.data.UserM;
import com.vantagecircle.heartrate.scope.UserScope;
import com.vantagecircle.heartrate.utils.ImageProcessing;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class UserModule {
    private UserM userM;

    public UserModule(UserM userM) {
        this.userM = userM;
    }

    @Provides
    @UserScope
    UserM
    provideUser() {
        return userM;
    }

    @Provides
    @UserScope
    ImageProcessing
    provideImageProcessing() {
        return new ImageProcessing();
    }
}
