package com.vantagecircle.heartrate.activity.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.TabLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.module.HeartActivityModule;
import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import javax.inject.Inject;

public class HeartActivity extends BaseActivity {
    private final String TAG = HeartActivity.class.getSimpleName();
    @Inject
    HeartActivityPresenter mHeartActivityPresenter;

    public TabLayout mTabLayout;
    public ViewPager mViewPager;
    public Toolbar mToolBar;
    public ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        askPermission();
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
        mToolBar = findViewById(R.id.toolbar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        mHeartActivityPresenter.setup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        HeartApplication.get(this).releaseUserComponent();
    }
}
