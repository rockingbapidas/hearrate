package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 23/11/17.
 */

public interface PulseListener {
    void OnPulseCheckStarted();
    void OnPulseCheckStopped();
    void OnPulseCheckFinished(String mPulseRate, boolean isComplete);
    void OnPulseCheckRate(String mPulseRate);
    void OnPulseCheckError();
}
