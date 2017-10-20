package com.vantagecircle.heartrate.data;

/**
 * Created by bapidas on 09/10/17.
 */

public class UserM {
    private String userId;
    private String userName;

    public UserM(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
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
