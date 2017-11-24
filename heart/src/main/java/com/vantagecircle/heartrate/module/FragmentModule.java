package com.vantagecircle.heartrate.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.vantagecircle.heartrate.annotation.ActivityContext;
import com.vantagecircle.heartrate.camera.CameraManager;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.PulseManager;
import com.vantagecircle.heartrate.core.PulseSupport;

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
    Activity provideContext(){
        return mFragment.getActivity();
    }

    @Provides
    Fragment provideFragment(){
        return mFragment;
    }

    @Provides
    CameraSupport provideCameraSupport(@ActivityContext Activity mActivity) {
        return new CameraManager(mActivity);
    }

    @Provides
    PulseSupport providePulseSupport(CameraSupport cameraSupport) {
        return new PulseManager(cameraSupport);
    }
}
