package com.vantagecircle.heartrate;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.component.AppComponent;
import com.vantagecircle.heartrate.component.DaggerAppComponent;
import com.vantagecircle.heartrate.component.UserComponent;
import com.vantagecircle.heartrate.data.UserM;
import com.vantagecircle.heartrate.module.AppModule;
import com.vantagecircle.heartrate.module.UserModule;

/**
 * Created by bapidas on 09/10/17.
 */

public class HeartApplication extends Application {
    private AppComponent appComponent;
    private UserComponent userComponent;

    public static HeartApplication get(Context context) {
        return (HeartApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAppComponent();
    }

    private void initAppComponent(){
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void createUserComponent(UserM userM) {
        userComponent = appComponent.plus(new UserModule(userM));
    }

    public void releaseUserComponent() {
        userComponent = null;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }
}
