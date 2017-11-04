package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 03/11/17.
 */

public interface HeartSupport {
    HeartSupport startPulseCheck(PulseListener pulseListener);
    HeartSupport setPulseTimeLimit(long timeLimit, long countInterval);
    boolean isTimerRunning();
    void stopPulseCheck();
}
