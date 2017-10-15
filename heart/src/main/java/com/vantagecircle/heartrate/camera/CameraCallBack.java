package com.vantagecircle.heartrate.camera;

/**
 * Created by SiD on 10/14/2017.
 */

public interface CameraCallBack {
    void onFrameCallback(byte[] data, int width, int height);
}
