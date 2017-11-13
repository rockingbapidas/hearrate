package com.vantagecircle.heartrate.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.vantagecircle.heartrate.BR;

/**
 * Created by SiD on 11/11/2017.
 */

public class HistoryModel extends BaseObservable {
    private String heartRate;
    private String dateString;
    private String timeString;

    public HistoryModel(String heartRate, String dateString, String timeString) {
        this.heartRate = heartRate;
        this.dateString = dateString;
        this.timeString = timeString;
    }

    @Bindable
    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
        notifyPropertyChanged(BR.heartRate);
    }

    @Bindable
    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
        notifyPropertyChanged(BR.dateString);
    }

    @Bindable
    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
        notifyPropertyChanged(BR.timeString);
    }
}
