package com.vantagecircle.heartrate.camera;

/**
 * Created by bapidas on 22/11/17.
 */

public interface PreviewListener {
    void OnPreviewCallback(byte[] data, int mWidth, int mHeight);
}
