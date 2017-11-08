package com.vantagecircle.heartrate.activity.handlers;

import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.data.UserM;

/**
 * Created by bapidas on 10/10/17.
 */

public class WelcomeActivityHandlers {
    private final static String TAG = WelcomeActivityHandlers.class.getSimpleName();
    private WelcomeActivityPresenter welcomeActivityPresenter;

    public WelcomeActivityHandlers(WelcomeActivityPresenter welcomeActivityPresenter) {
        this.welcomeActivityPresenter = welcomeActivityPresenter;
    }

    public void onCheckClick() {
        welcomeActivityPresenter.createUser(new UserM("0944", "Bapi Das"));
    }
}
