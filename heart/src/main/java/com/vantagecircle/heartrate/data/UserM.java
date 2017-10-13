package com.vantagecircle.heartrate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bapidas on 09/10/17.
 */

public class UserM implements Parcelable {
    private String userId;
    private String userName;

    public UserM(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    private UserM(Parcel in) {
        userId = in.readString();
        userName = in.readString();
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
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
