package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 03/11/17.
 */

public interface PulseListener {
    void OnPulseDetected(int success);
    void OnPulseDetectFailed(int failed);
    void OnPulseResult(String pulse);
    void OnPulseCheckStop();
}
