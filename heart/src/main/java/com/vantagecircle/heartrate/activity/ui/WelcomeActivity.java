package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.component.DaggerWelcomeActivityComponent;
import com.vantagecircle.heartrate.activity.component.WelcomeActivityComponent;
import com.vantagecircle.heartrate.activity.handlers.WelcomeActivityHandlers;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity {
    private final String TAG = WelcomeActivity.class.getSimpleName();
    private WelcomeActivityComponent welcomeActivityComponent;
    private WelcomeActivityPresenter welcomeActivityPresenter;
    public WelcomeActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        init();
    }

    protected WelcomeActivityComponent setupActivityComponent() {
        if (welcomeActivityComponent == null) {
            welcomeActivityComponent = DaggerWelcomeActivityComponent.builder()
                    .welcomeActivityModule(new WelcomeActivityModule(this))
                    .appComponent(HeartApplication.get(this).getAppComponent())
                    .build();
        }
        return welcomeActivityComponent;
    }

    @Override
    protected void init() {
        welcomeActivityPresenter = new WelcomeActivityPresenter(this);
        mBinding.setWelcomeActivityHandlers(new WelcomeActivityHandlers(welcomeActivityPresenter));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
