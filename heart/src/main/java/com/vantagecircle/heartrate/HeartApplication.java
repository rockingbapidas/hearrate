package com.vantagecircle.heartrate;

import android.app.Application;
import android.content.Context;

import com.vantagecircle.heartrate.component.AppComponent;
import com.vantagecircle.heartrate.module.AppModule;

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
}
