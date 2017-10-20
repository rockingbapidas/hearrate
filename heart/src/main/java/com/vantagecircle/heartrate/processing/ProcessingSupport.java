package com.vantagecircle.heartrate.processing;

/**
 * Created by bapidas on 12/10/17.
 */

public interface ProcessingSupport {
    String heartRate(byte[] data, int width, int height);
}
