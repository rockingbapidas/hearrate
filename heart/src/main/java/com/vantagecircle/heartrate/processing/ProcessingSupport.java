package com.vantagecircle.heartrate.processing;

import android.media.Image;

/**
 * Created by bapidas on 12/10/17.
 */

public interface ProcessingSupport {
    byte[] YUV_420_888toNV21(Image image);
    int YUV420SPtoRedAvg(byte[] yuv420sp, int width, int height);
    int YUV420SPtoRedSum(byte[] yuv420sp, int width, int height);
}
