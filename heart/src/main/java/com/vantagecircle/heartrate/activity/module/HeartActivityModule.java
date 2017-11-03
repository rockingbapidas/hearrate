package com.vantagecircle.heartrate.activity.module;

import android.os.Build;
import android.util.Log;

import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraNew;
import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.processing.ProcessingSupport;
import com.vantagecircle.heartrate.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 09/10/17.
 */
@Module
public class HeartActivityModule {
    private final static String TAG = HeartActivityModule.class.getSimpleName();
    private HeartActivity heartActivity;

    public HeartActivityModule(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    @Provides
    @ActivityScope
    HeartActivity
    provideHeartActivity() {
        return heartActivity;
    }

    @Provides
    @ActivityScope
    ProcessingSupport
    provideProcessingSupport() {
        return new Processing();
    }

    @Provides
    @ActivityScope
    CameraSupport
    provideCameraSupport(HeartActivity heartActivity, ProcessingSupport processingSupport) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(heartActivity, processingSupport);
        } else {
            return new CameraOld(heartActivity, processingSupport);
        }
    }

    @Provides
    @ActivityScope
    HeartSupport
    provideHeartSupport(CameraSupport cameraSupport) {
        return new HeartRate(cameraSupport);
    }


    @Provides
    @ActivityScope
    HeartActivityPresenter
    provideHeartActivityPresenter(HeartActivity heartActivity, HeartSupport heartSupport) {
        return new HeartActivityPresenter(heartActivity, heartSupport);
    }
}
