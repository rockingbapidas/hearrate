package com.vantagecircle.heartrate.activity.presenter;

import android.content.Intent;
import android.util.Log;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;
import com.vantagecircle.heartrate.data.UserM;

/**
 * Created by bapidas on 10/10/17.
 */

public class WelcomeActivityPresenter {
    private final static String TAG = WelcomeActivityPresenter.class.getSimpleName();
    private WelcomeActivity welcomeActivity;

    public WelcomeActivityPresenter(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    public void createUser(UserM userM) {
        HeartApplication.get(welcomeActivity).createUserComponent(userM);
        welcomeActivity.startActivity(new Intent(welcomeActivity, HeartActivity.class));
    }
}
