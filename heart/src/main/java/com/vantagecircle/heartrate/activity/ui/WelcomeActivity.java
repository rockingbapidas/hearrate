package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.ActivityComponent;
import com.vantagecircle.heartrate.activity.ActivityModule;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.DaggerActivityComponent;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity {
    private final String TAG = WelcomeActivity.class.getSimpleName();
    private ActivityComponent mActivityComponent;
    private WelcomeActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        init();
    }

    @Override
    protected void setActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .appComponent(HeartApplication.get(this).getAppComponent())
                    .build();
        }
        mActivityComponent.inject(this);
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
