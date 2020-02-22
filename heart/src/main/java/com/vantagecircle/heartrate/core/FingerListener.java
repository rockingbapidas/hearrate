package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 13/11/17.
 */

public interface FingerListener {
    void OnFingerDetected(int success);

    void OnFingerDetectFailed(int failed);
}
