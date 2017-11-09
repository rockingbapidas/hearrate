package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.handlers.WelcomeActivityHandlers;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

import javax.inject.Inject;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity {
    private final String TAG = WelcomeActivity.class.getSimpleName();
    @Inject
    WelcomeActivityPresenter welcomeActivityPresenter;
    public WelcomeActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        mBinding.setWelcomeActivityHandlers(new WelcomeActivityHandlers(welcomeActivityPresenter));
    }

    @Override
    protected void setupActivityComponent() {
        HeartApplication.get(this)
                .getAppComponent()
                .plus(new WelcomeActivityModule(this))
                .inject(this);
    }

    @Override
    protected void init() {
        //no need to write any code here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
