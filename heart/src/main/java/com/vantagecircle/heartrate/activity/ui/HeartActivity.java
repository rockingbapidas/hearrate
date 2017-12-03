package com.vantagecircle.heartrate.activity.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.presenter.HeartActivityPresenter;
import com.vantagecircle.heartrate.component.ActivityComponent;
import com.vantagecircle.heartrate.component.DaggerActivityComponent;
import com.vantagecircle.heartrate.module.ActivityModule;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HeartActivity extends BaseActivity {
    private final String TAG = HeartActivity.class.getSimpleName();
    @BindView(R.id.tabLayout)
    public TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    public ViewPager mViewPager;
    @BindView(R.id.toolbar)
    public Toolbar mToolBar;

    public ActionBar mActionBar;
    private Unbinder mUnBinder;
    private ActivityComponent mActivityComponent;
    private HeartActivityPresenter mHeartActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mUnBinder = ButterKnife.bind(this);
        askPermission();
    }

    @Override
    protected void setActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .heartComponent(HeartApplication.get(this).getHeartComponent())
                    .build();
        }
        mActivityComponent.inject(this);
    }

    @Override
    protected void init() {
        mHeartActivityPresenter = new HeartActivityPresenter(this);
        mHeartActivityPresenter.setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.action_help){
            mHeartActivityPresenter.showHintDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ToolsUtils.getInstance().isHasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.BODY_SENSORS,
                    Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.BODY_SENSORS,
                        Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE}, Constant.REQUEST_ALL_PERMISSION);
            } else {
                init();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission(requestCode, grantResults);
    }

    private void checkPermission(int requestCode, int[] grantResults) {
        if (requestCode == Constant.REQUEST_ALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "This window need camera and sensor permission", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                init();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
