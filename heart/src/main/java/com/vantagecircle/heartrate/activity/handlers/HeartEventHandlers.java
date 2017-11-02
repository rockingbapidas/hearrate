package com.vantagecircle.heartrate.activity.handlers;

import android.databinding.ObservableField;
import android.util.Log;

import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;

/**
 * Created by bapidas on 10/10/17.
 */

public class HeartEventHandlers {
    private final static String TAG = HeartEventHandlers.class.getSimpleName();
    private HeartActivityPresenter heartActivityPresenter;

    public HeartEventHandlers(HeartActivityPresenter heartActivityPresenter) {
        this.heartActivityPresenter = heartActivityPresenter;
    }

    public void onStartClick() {
        heartActivityPresenter.handleClick();
    }
}
