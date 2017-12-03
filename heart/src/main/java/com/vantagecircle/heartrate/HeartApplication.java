package com.vantagecircle.heartrate;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.vantagecircle.heartrate.component.DaggerHeartComponent;
import com.vantagecircle.heartrate.component.HeartComponent;
import com.vantagecircle.heartrate.module.HeartModule;

import io.fabric.sdk.android.Fabric;

/**
 * Created by bapidas on 09/10/17.
 */

public class HeartApplication extends Application {
    private HeartComponent heartComponent;

    public static HeartApplication get(Context context) {
        return (HeartApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initHeartComponent();
    }

    private void initHeartComponent() {
        if (heartComponent == null) {
            heartComponent = DaggerHeartComponent.builder()
                    .heartModule(new HeartModule(this))
                    .build();
        }
        heartComponent.inject(this);
    }

    private void initHeartComponent(String gender, int year) {
        if (heartComponent == null) {
            heartComponent = DaggerHeartComponent.builder()
                    .heartModule(new HeartModule(this, gender, year))
                    .build();
        }
        heartComponent.inject(this);
    }

    public HeartComponent getHeartComponent() {
        return heartComponent;
    }
}
