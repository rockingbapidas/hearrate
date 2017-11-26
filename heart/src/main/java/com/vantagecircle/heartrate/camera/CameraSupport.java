package com.vantagecircle.heartrate.camera;

import android.view.SurfaceHolder;

/**
 * Created by bapidas on 22/11/17.
 */

public interface CameraSupport {
    void openCamera();
    void prepareCamera();
    void setPreviewHolder(SurfaceHolder mSurfaceHolder);
    void preparePreview(int mWidth, int mHeight);
    void startPreview();
    void stopPreview();
    void releaseCamera();
    void releaseCallbacks();
    boolean isAutoExposureLockSupported();
    boolean isAutoExposureLockEnabled();
    void enableAutoExposureLock();
    void disableAutoExposureLock();
    boolean isFlashSupported();
    boolean isFlashEnabled();
    void enableFlash();
    void disableFlash();
    int getLightIntensity();
    void addOnPreviewListener(PreviewListener mPreviewListener);
}
