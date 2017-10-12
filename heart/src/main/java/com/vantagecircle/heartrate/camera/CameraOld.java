package com.vantagecircle.heartrate.camera;

import android.hardware.Camera;

/**
 * Created by bapidas on 12/10/17.
 */
@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {
    private Camera camera;

    @Override
    public CameraSupport open(final int cameraId) {
        this.camera = Camera.open(cameraId);
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }
}
