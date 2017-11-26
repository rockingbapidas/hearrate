package com.vantagecircle.heartrate.core;

import android.view.SurfaceHolder;

/**
 * Created by bapidas on 22/11/17.
 */

public interface PulseSupport {
    void setSurface(SurfaceHolder mSurfaceHolder);

    PulseSupport setMeasurementTime(int time);

    PulseSupport setBeep(boolean beepEnable);

    void addOnPulseListener(PulseListener mPulseListener);

    PulseSupport startMeasure();

    void resumeMeasure();

    void stopMeasure();
}
