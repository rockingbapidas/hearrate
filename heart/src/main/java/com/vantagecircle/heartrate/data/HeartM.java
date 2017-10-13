package com.vantagecircle.heartrate.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.vantagecircle.heartrate.BR;

/**
 * Created by bapidas on 13/10/17.
 */

public class HeartM extends BaseObservable implements Parcelable {
    private int beatsArrayAvg;
    private int beatsArrayCnt;
    private int beatsAvg;
    private String beatsPerMinuteValue;

    public HeartM(int beatsArrayAvg, int beatsArrayCnt, int beatsAvg, String beatsPerMinuteValue) {
        this.beatsArrayAvg = beatsArrayAvg;
        this.beatsArrayCnt = beatsArrayCnt;
        this.beatsAvg = beatsAvg;
        this.beatsPerMinuteValue = beatsPerMinuteValue;
    }

    private HeartM(Parcel in) {
        beatsArrayAvg = in.readInt();
        beatsArrayCnt = in.readInt();
        beatsAvg = in.readInt();
        beatsPerMinuteValue = in.readString();
    }

    public static final Creator<HeartM> CREATOR = new Creator<HeartM>() {
        @Override
        public HeartM createFromParcel(Parcel in) {
            return new HeartM(in);
        }

        @Override
        public HeartM[] newArray(int size) {
            return new HeartM[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(beatsArrayAvg);
        parcel.writeInt(beatsArrayCnt);
        parcel.writeInt(beatsAvg);
        parcel.writeString(beatsPerMinuteValue);
    }

    @Bindable
    public int getBeatsArrayAvg() {
        return beatsArrayAvg;
    }

    @Bindable
    public int getBeatsArrayCnt() {
        return beatsArrayCnt;
    }

    @Bindable
    public int getBeatsAvg() {
        return beatsAvg;
    }

    @Bindable
    public String getBeatsPerMinuteValue() {
        return beatsPerMinuteValue;
    }



    public void setBeatsArrayAvg(int beatsArrayAvg) {
        this.beatsArrayAvg = beatsArrayAvg;
        notifyPropertyChanged(BR.beatsArrayAvg);
    }

    public void setBeatsArrayCnt(int beatsArrayCnt) {
        this.beatsArrayCnt = beatsArrayCnt;
        notifyPropertyChanged(BR.beatsArrayCnt);
    }

    public void setBeatsAvg(int beatsAvg) {
        this.beatsAvg = beatsAvg;
        notifyPropertyChanged(BR.beatsAvg);
    }

    public void setBeatsPerMinuteValue(String beatsPerMinuteValue) {
        this.beatsPerMinuteValue = beatsPerMinuteValue;
        notifyPropertyChanged(BR.beatsPerMinuteValue);
    }
}
