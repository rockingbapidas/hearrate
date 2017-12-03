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

public class HeartApplication {
    private static HeartApplication mInstance;
    private HeartComponent mHeartComponent;

    public static HeartApplication getInstance() {
        if (mInstance == null) {
            synchronized (HeartApplication.class) {
                if (mInstance == null) {
                    mInstance = new HeartApplication();
                }
            }
        }
        return mInstance;
    }

    public static HeartApplication get(Context context) {
        return mInstance;
    }

    public void initHeartComponent(Application mApplication) {
        if (mHeartComponent == null) {
            mHeartComponent = DaggerHeartComponent.builder()
                    .heartModule(new HeartModule(mApplication))
                    .build();
        }
        mHeartComponent.inject(this);
    }

    public void initHeartComponent(Application mApplication, String gender, int year) {
        if (mHeartComponent == null) {
            mHeartComponent = DaggerHeartComponent.builder()
                    .heartModule(new HeartModule(mApplication, gender, year))
                    .build();
        }
        mHeartComponent.inject(this);
    }

    public HeartComponent getHeartComponent() {
        return mHeartComponent;
    }
}
