package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.component.DaggerWelcomeActivityComponent;
import com.vantagecircle.heartrate.activity.component.WelcomeActivityComponent;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity {
    private final String TAG = WelcomeActivity.class.getSimpleName();
    private WelcomeActivityComponent welcomeActivityComponent;
    private WelcomeActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        init();
    }

    @Override
    protected void setActivityComponent() {
        if (welcomeActivityComponent == null) {
            welcomeActivityComponent = DaggerWelcomeActivityComponent.builder()
                    .welcomeActivityModule(new WelcomeActivityModule(this))
                    .appComponent(HeartApplication.get(this).getAppComponent())
                    .build();
        }
        welcomeActivityComponent.inject(this);
    }

    @Override
    protected void init() {
        WelcomeActivityPresenter welcomeActivityPresenter = new WelcomeActivityPresenter(this);
        mBinding.setWelcomePresenter(welcomeActivityPresenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
