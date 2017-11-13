package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 03/11/17.
 */

public interface HeartSupport {
    HeartSupport startPulseCheck(long timeLimit);
    HeartSupport startPulseCheck();
    HeartSupport addOnResultListener(PulseListener pulseListener);
    HeartSupport addOnFingerListener(FingerListener fingerListener);
    HeartSupport addOnTimerListener(TimerListener timerListener);
    void stopPulseCheck();
}
