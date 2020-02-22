package com.vantagecircle.heartrate.activity.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.ActivityComponent;
import com.vantagecircle.heartrate.component.DaggerActivityComponent;
import com.vantagecircle.heartrate.databinding.MainActivityBinding;
import com.vantagecircle.heartrate.module.ActivityModule;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.ToolsUtils;


public class HeartActivity extends BaseActivity {
    private final String TAG = HeartActivity.class.getSimpleName();

    public ActionBar mActionBar;
    public MainActivityBinding mBinding;
    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        askPermission();
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
        HeartActivityPresenter mHeartActivityPresenter = new HeartActivityPresenter(this);
        mHeartActivityPresenter.setup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission(requestCode, grantResults);
    }

    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ToolsUtils.getInstance().isHasPermissions(this,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG, "Permission already accepted");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constant.REQUEST_CAMERA_PERMISSION);
            }
        } else {
            Log.d(TAG, "No need permission");
        }
    }

    public void checkPermission(int requestCode, int[] grantResults) {
        if (requestCode == Constant.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
            } else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "You have to give permission " +
                        "to access this window", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
