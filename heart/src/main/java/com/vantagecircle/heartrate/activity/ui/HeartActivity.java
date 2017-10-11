package com.vantagecircle.heartrate.activity.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.vantagecircle.heartrate.data.UserM;
import com.vantagecircle.heartrate.databinding.ActivityMainBinding;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import javax.inject.Inject;

public class HeartActivity extends BaseActivity implements SensorEventListener {
    private final String TAG = HeartActivity.class.getSimpleName();
    @Inject
    UserM userM;
    @Inject
    HeartActivityPresenter heartActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
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
            if (ToolsUtils.getInstance().isHasPermissions(this, Manifest.permission.BODY_SENSORS)) {
                Log.d(TAG, "Permission already accepted");
                loadSensor();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS},
                        Constant.REQUEST_SENSOR_PERMISSION);
            }
        } else {
            Log.d(TAG, "No need permission");
            loadSensor();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUEST_SENSOR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
                loadSensor();
            } else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(getApplicationContext(), "You have to give permission to access this window",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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

    private void loadSensor(){
        SensorManager sMgr;
        sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor battito = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            battito = sMgr.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        }
        if (battito != null)
            Log.d(TAG, "load sensor");
        else
            Log.d(TAG, "no load sensor");
        //sMgr.registerListener(this, battito, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, battito,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = " Value sensor: " + (int)event.values[0];
            Log.d(TAG, "onSensorChanged ====  " + msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged ====== " + accuracy);
    }
}
