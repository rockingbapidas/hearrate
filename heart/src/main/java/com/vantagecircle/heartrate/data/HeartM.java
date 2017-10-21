package com.vantagecircle.heartrate.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.vantagecircle.heartrate.BR;

/**
 * Created by bapidas on 13/10/17.
 */

public class HeartM extends BaseObservable  {
    private int beatsArrayAvg;
    private int beatsArrayCnt;
    private int beatsAvg;
    private String beatsPerMinuteValue;

    public int getBeatsArrayAvg() {
        return beatsArrayAvg;
    }

    public int getBeatsArrayCnt() {
        return beatsArrayCnt;
    }

    public int getBeatsAvg() {
        return beatsAvg;
    }

    @Bindable
    public String getBeatsPerMinuteValue() {
        return beatsPerMinuteValue;
    }

    public void setBeatsArrayAvg(int beatsArrayAvg) {
        this.beatsArrayAvg = beatsArrayAvg;
    }

    public void setBeatsArrayCnt(int beatsArrayCnt) {
        this.beatsArrayCnt = beatsArrayCnt;
    }

    public void setBeatsAvg(int beatsAvg) {
        this.beatsAvg = beatsAvg;
    }

    public void setBeatsPerMinuteValue(String beatsPerMinuteValue) {
        this.beatsPerMinuteValue = beatsPerMinuteValue;
        notifyPropertyChanged(BR.beatsPerMinuteValue);
    }
}
