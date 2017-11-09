package com.vantagecircle.heartrate.activity.handlers;

import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;

/**
 * Created by bapidas on 10/10/17.
 */

public class WelcomeActivityHandlers {
    private WelcomeActivityPresenter welcomeActivityPresenter;

    public WelcomeActivityHandlers(WelcomeActivityPresenter welcomeActivityPresenter) {
        this.welcomeActivityPresenter = welcomeActivityPresenter;
    }

    public void onCheckClick() {
        welcomeActivityPresenter.goToHeartScreen();
    }
}
