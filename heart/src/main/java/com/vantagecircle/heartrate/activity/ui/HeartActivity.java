package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.handlers.HeartEventHandlers;
import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.data.UserM;
import com.vantagecircle.heartrate.databinding.ActivityMainBinding;

import javax.inject.Inject;

public class HeartActivity extends BaseActivity {
    private final String TAG = HeartActivity.class.getSimpleName();
    @Inject
    UserM userM;
    @Inject
    HeartActivityPresenter heartActivityPresenter;
    @Inject
    HeartEventHandlers heartEventHandlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainHandlers(heartEventHandlers);
    }

    @Override
    protected void setupActivityComponent() {
        HeartApplication.get(this)
                .getUserComponent()
                .plus(new HeartActivityModule(this))
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        HeartApplication.get(this).releaseUserComponent();
    }
}
