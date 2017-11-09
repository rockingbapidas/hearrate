package com.vantagecircle.heartrate.activity.presenter;

import android.content.Intent;

import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.activity.ui.WelcomeActivity;

/**
 * Created by bapidas on 10/10/17.
 */

public class WelcomeActivityPresenter {
    private WelcomeActivity welcomeActivity;

    public WelcomeActivityPresenter(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    public void goToHeartScreen() {
        welcomeActivity.startActivity(new Intent(welcomeActivity, HeartActivity.class));
    }
}
