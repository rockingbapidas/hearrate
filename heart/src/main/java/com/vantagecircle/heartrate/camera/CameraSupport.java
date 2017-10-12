package com.vantagecircle.heartrate.camera;

/**
 * Created by bapidas on 12/10/17.
 */

public interface CameraSupport {
    CameraSupport open(int cameraId);
    int getOrientation(int cameraId);
}
