package com.vantagecircle.heartrate.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Created by bapidas on 22/11/17.
 */

public interface CameraSupport {
    void openCamera();

    void prepareCamera();

    Camera getCamera();

    void createSurface(SurfaceHolder mSurfaceHolder);

    void changedSurface(int mWidth, int mHeight);

    void setRotation();

    void startPreview();

    void stopPreview();

    void releaseCamera();

    boolean isAutoExposureLockSupported();

    boolean isAutoExposureLockEnabled();

    void enableAutoExposureLock();

    void disableAutoExposureLock();

    boolean isFlashSupported();

    boolean isFlashEnabled();

    void enableFlash();

    void disableFlash();

    int getLightIntensity();

    void setLightIntensity(int intensity);

    void addSensorListener();

    void removeSensorListener();

    void addOnPreviewListener(PreviewListener mPreviewListener);

    Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters);

    int[] getAccurateFps(List<int[]> list);
}
