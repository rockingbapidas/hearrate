package com.vantagecircle.heartrate.fragment.module;

import android.content.Context;

import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.processing.ProcessingSupport;
import com.vantagecircle.heartrate.scope.ActivityContext;
import com.vantagecircle.heartrate.scope.ApplicationContext;
import com.vantagecircle.heartrate.scope.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 08/11/17.
 */
@Module
public class HeartFragmentModule {
    private HeartFragment mHeartFragment;

    public HeartFragmentModule(HeartFragment mHeartFragment) {
        this.mHeartFragment = mHeartFragment;
    }

    @Provides
    @ActivityContext
    Context provideContext(){
        return mHeartFragment.getActivity();
    }

    @Provides
    HeartFragment provideHeartFragment(){
        return mHeartFragment;
    }

    @Provides
    ProcessingSupport provideProcessingSupport() {
        return new Processing();
    }

    @Provides
    CameraSupport provideCameraSupport(@ActivityContext Context mContext, ProcessingSupport processingSupport) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(mContext, processingSupport);
        } else {
            return new CameraOld(mContext, processingSupport);
        }*/
        return new CameraOld(mContext, processingSupport);
    }

    @Provides
    HeartRate provideHeartRate(CameraSupport cameraSupport) {
        return new HeartRate(cameraSupport);
    }
}
