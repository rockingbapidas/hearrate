package com.vantagecircle.heartrate.fragment.module;

import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.processing.ProcessingSupport;
import com.vantagecircle.heartrate.scope.ActivityScope;
import com.vantagecircle.heartrate.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 08/11/17.
 */
@Module
public class HeartFragmentModule {
    @Provides
    @FragmentScope
    ProcessingSupport
    provideProcessingSupport() {
        return new Processing();
    }

    @Provides
    @FragmentScope
    CameraSupport
    provideCameraSupport(HeartActivity heartActivity, ProcessingSupport processingSupport) {
        return new CameraOld(heartActivity, processingSupport);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(heartActivity, processingSupport);
        } else {
            return new CameraOld(heartActivity, processingSupport);
        }*/
    }

    @Provides
    @FragmentScope
    HeartSupport
    provideHeartSupport(CameraSupport cameraSupport) {
        return new HeartRate(cameraSupport);
    }
}
