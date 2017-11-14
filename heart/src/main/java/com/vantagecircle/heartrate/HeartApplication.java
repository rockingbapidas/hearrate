package com.vantagecircle.heartrate;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by bapidas on 09/10/17.
 */

public class HeartApplication extends Application {
    private AppComponent appComponent;

    public static HeartApplication get(Context context) {
        return (HeartApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initAppComponent();
    }

    private void initAppComponent(){
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
