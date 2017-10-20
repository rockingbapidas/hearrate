package com.vantagecircle.heartrate.activity.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.vantagecircle.heartrate.utils.ToolsUtils;

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
        Log.d(TAG, "setupActivityComponent");
        HeartApplication.get(this)
                .getUserComponent()
                .plus(new HeartActivityModule(this))
                .inject(this);
    }

    @Override
    protected void init() {
        super.init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ToolsUtils.getInstance().isHasPermissions(this, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Permission already accepted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        Constant.REQUEST_CAMERA_PERMISSION);
            }
        } else {
            Log.d(TAG, "No need permission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
            } else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(getApplicationContext(), "You have to give permission to access this window",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void updateHeartRate(HeartM heartM) {
        binding.setHeart(heartM);
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
