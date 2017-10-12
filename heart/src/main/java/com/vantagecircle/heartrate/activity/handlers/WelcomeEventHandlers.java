package com.vantagecircle.heartrate.activity.handlers;

import android.util.Log;

import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.data.UserM;

/**
 * Created by bapidas on 10/10/17.
 */

public class WelcomeEventHandlers {
    private final static String TAG = WelcomeEventHandlers.class.getSimpleName();
    private WelcomeActivityPresenter welcomeActivityPresenter;

    public WelcomeEventHandlers(WelcomeActivityPresenter welcomeActivityPresenter) {
        Log.d(TAG, "WelcomeEventHandlers");
        this.welcomeActivityPresenter = welcomeActivityPresenter;
    }

    public void onCheckClick() {
        Log.d(TAG, "onCheckClick");
        welcomeActivityPresenter.createUser(new UserM("0944", "Bapi Das", "77"));
    }
}
