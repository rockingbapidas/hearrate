package com.vantagecircle.heartrate.activity.ui;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.handlers.HeartEventHandlers;
import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.data.HeartM;
import com.vantagecircle.heartrate.databinding.ActivityMainBinding;
import com.vantagecircle.heartrate.utils.Constant;

import javax.inject.Inject;

public class HeartActivity extends BaseActivity {
    private final String TAG = HeartActivity.class.getSimpleName();
    @Inject
    HeartActivityPresenter heartActivityPresenter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainHandlers(new HeartEventHandlers(heartActivityPresenter));
        init();
    }

    @Override
    protected void setupActivityComponent() {
        HeartApplication.get(this)
                .getUserComponent()
                .plus(new HeartActivityModule(this))
                .inject(this);
    }

    @Override
    protected void init() {
        super.init();
        heartActivityPresenter.askPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        heartActivityPresenter.checkPermission(requestCode, grantResults);
    }

    //bind heart rate to the view
    public void bindHeartRate(HeartM heartM) {
        if (heartM != null)
            binding.setHeart(heartM);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
