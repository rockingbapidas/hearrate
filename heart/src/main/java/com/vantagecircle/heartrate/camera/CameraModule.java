package com.vantagecircle.heartrate.camera;

import android.content.Context;
import android.os.Build;

import com.vantagecircle.heartrate.scope.CameraScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bapidas on 12/10/17.
 */
@Module
public class CameraModule {
    private Context mContext;

    public CameraModule(Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    @CameraScope
    CameraSupport
    provideCameraSupport(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(mContext);
        } else {
            return new CameraOld();
        }
    }
}
