package com.vantagecircle.heartrate.fragment;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.vantagecircle.heartrate.camera.CameraNew;
import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.processing.ProcessingSupport;
import com.vantagecircle.heartrate.annotation.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 14/11/17.
 */
@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @Provides
    @ActivityContext
    Context provideContext(){
        return mFragment.getActivity();
    }

    @Provides
    Fragment provideFragment(){
        return mFragment;
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
