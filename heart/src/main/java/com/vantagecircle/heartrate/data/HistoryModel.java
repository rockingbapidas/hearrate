package com.vantagecircle.heartrate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SiD on 11/11/2017.
 */

public class HistoryModel implements Parcelable {
    private String id;
    private String heartRate;
    private long timeStamp;

    public HistoryModel(String id, String heartRate, long timeStamp) {
        this.id = id;
        this.heartRate = heartRate;
        this.timeStamp = timeStamp;
    }

    private HistoryModel(Parcel in) {
        id = in.readString();
        heartRate = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(heartRate);
        dest.writeLong(timeStamp);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
