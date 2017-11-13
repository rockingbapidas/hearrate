package com.vantagecircle.heartrate.core;

/**
 * Created by bapidas on 06/11/17.
 */

public interface TimerListener {
    void OnTimerStarted();
    void OnTimerRunning(long milliSecond);
    void OnTimerStopped();
}
