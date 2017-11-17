package com.vantagecircle.heartrate.camera;

/**
 * Created by SiD on 10/14/2017.
 */

public interface PreviewListener {
    void OnCameraRawData(byte[] data);
    void OnPixelAverage(int pixelAverage);
}
