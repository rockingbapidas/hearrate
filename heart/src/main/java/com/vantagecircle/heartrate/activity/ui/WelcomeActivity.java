package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.handlers.WelcomeEventHandlers;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

import javax.inject.Inject;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity{
    private final String TAG = WelcomeActivity.class.getSimpleName();
    @Inject
    WelcomeActivityPresenter welcomeActivityPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WelcomeActivityBinding mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        mBinding.setWelcomeHandlers(new WelcomeEventHandlers(welcomeActivityPresenter));
    }

    @Override
    protected void setupActivityComponent() {
        Log.d(TAG, "setupActivityComponent");
        HeartApplication.get(this)
                .getAppComponent()
                .plus(new WelcomeActivityModule(this))
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
