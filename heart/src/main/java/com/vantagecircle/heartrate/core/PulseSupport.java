package com.vantagecircle.heartrate.core;

import android.view.SurfaceHolder;

/**
 * Created by bapidas on 22/11/17.
 */

public interface PulseSupport {
    PulseSupport startMeasure();
    void stopMeasure();
    PulseSupport setSurfaceHolder(SurfaceHolder mSurfaceHolder);
    PulseSupport setMeasurementTime(int time);
    PulseSupport setBeep(boolean beepEnable);
    void addOnPulseListener(PulseListener mPulseListener);
}
