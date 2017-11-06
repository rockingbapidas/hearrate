package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 06/11/17.
 */

public interface StatusListener {
    void OnCheckStarted();
    void OnCheckRunning();
    void OnCheckStopped();
}
