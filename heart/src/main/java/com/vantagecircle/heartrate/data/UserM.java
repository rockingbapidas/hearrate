package com.vantagecircle.heartrate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bapidas on 09/10/17.
 */

public class UserM implements Parcelable {
    private String userId;
    private String userName;
    private String heartRate;

    public UserM(String userId, String userName, String heartRate) {
        this.userId = userId;
        this.userName = userName;
        this.heartRate = heartRate;
    }

    private UserM(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        heartRate = in.readString();
    }

    public static final Creator<UserM> CREATOR = new Creator<UserM>() {
        @Override
        public UserM createFromParcel(Parcel in) {
            return new UserM(in);
        }

        @Override
        public UserM[] newArray(int size) {
            return new UserM[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(heartRate);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getHeartRate() {
        return heartRate;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }
}
