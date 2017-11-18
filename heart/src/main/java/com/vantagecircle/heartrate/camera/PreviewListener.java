package com.vantagecircle.heartrate.camera;

/**
 * Created by SiD on 10/14/2017.
 */

public interface PreviewListener {
    void OnPreviewData(byte[] data);
    void OnPreviewCount(int count);
}
