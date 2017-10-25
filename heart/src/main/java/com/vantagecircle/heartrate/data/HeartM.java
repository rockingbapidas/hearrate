package com.vantagecircle.heartrate.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.vantagecircle.heartrate.BR;

/**
 * Created by bapidas on 13/10/17.
 */

public class HeartM extends BaseObservable  {
    private String beatsPerMinuteValue;
    private boolean isDetectHeartRate;

    @Bindable
    public String getBeatsPerMinuteValue() {
        return beatsPerMinuteValue;
    }

    public void setBeatsPerMinuteValue(String beatsPerMinuteValue) {
        this.beatsPerMinuteValue = beatsPerMinuteValue;
        notifyPropertyChanged(BR.beatsPerMinuteValue);
    }

    @Bindable
    public boolean isDetectHeartRate() {
        return isDetectHeartRate;
    }

    public void setDetectHeartRate(boolean detectHeartRate) {
        isDetectHeartRate = detectHeartRate;
        notifyPropertyChanged(BR.detectHeartRate);
    }
}
