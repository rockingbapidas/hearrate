package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 03/11/17.
 */

public interface HeartSupport {
    HeartSupport startPulseCheck(long timeLimit, PulseListener pulseListener);
    HeartSupport startPulseCheck(PulseListener pulseListener);
    void setOnStatusListener(StatusListener statusListener);
    void stopPulseCheck();
}
