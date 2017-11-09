package com.vantagecircle.heartrate.fragment.module;

import android.content.Context;

import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.processing.ProcessingSupport;
import com.vantagecircle.heartrate.scope.ActivityContext;
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
    Context
    provideContext(HeartFragment heartFragment){
        return heartFragment.getActivity().getApplicationContext();
    }

    @Provides
    HeartFragment
    provideHeartFragment(){
        return mHeartFragment;
    }

    @Provides
    ProcessingSupport
    provideProcessingSupport() {
        return new Processing();
    }

    @Provides
    CameraSupport
    provideCameraSupport(Context mContext, ProcessingSupport processingSupport) {
        return new CameraOld(mContext, processingSupport);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(mContext, processingSupport);
        } else {
            return new CameraOld(mContext, processingSupport);
        }*/
    }

    @Provides
    HeartRate provideHeartSupport(CameraSupport cameraSupport) {
        return new HeartRate(cameraSupport);
    }

}
