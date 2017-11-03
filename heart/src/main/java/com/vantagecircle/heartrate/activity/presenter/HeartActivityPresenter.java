package com.vantagecircle.heartrate.activity.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraCallBack;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.data.HeartM;
import com.vantagecircle.heartrate.utils.Constant;
import com.vantagecircle.heartrate.utils.TYPE;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bapidas on 10/10/17.
 */

public class HeartActivityPresenter {
    private final String TAG = HeartActivityPresenter.class.getSimpleName();
    private HeartActivity heartActivity;
    private HeartSupport heartSupport;
    private HeartM heartM;

    public HeartActivityPresenter(HeartActivity heartActivity, HeartSupport heartSupport) {
        this.heartActivity = heartActivity;
        this.heartSupport = heartSupport;
    }

    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ToolsUtils.getInstance().isHasPermissions(heartActivity,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG, "Permission already accepted");
            } else {
                ActivityCompat.requestPermissions(heartActivity,
                        new String[]{Manifest.permission.CAMERA},
                        Constant.REQUEST_CAMERA_PERMISSION);
            }
        } else {
            Log.d(TAG, "No need permission");
        }
    }

    public void checkPermission(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted");
            } else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(heartActivity, "You have to give permission " +
                        "to access this window", Toast.LENGTH_SHORT).show();
                heartActivity.finish();
            }
        }
    }

    public void handleClick() {
        if (heartM != null && heartM.isStarted()) {
            heartM.setStarted(false);
            heartActivity.bindHeartRate(heartM);
            stop();
        } else {
            if (heartM != null) {
                heartM.setStarted(true);
            } else {
                heartM = new HeartM();
                heartM.setStarted(true);
            }
            heartActivity.bindHeartRate(heartM);
            start();
        }
    }

    private void start() {
        heartSupport.startPulseCheck(new PulseListener() {
            @Override
            public void OnPulseDetected(int success) {
                Log.e(TAG, "OnPulseDetected == " + success);
            }

            @Override
            public void OnPulseDetectFailed(int failed) {
                Log.e(TAG, "OnPulseDetectFailed == " + failed);
            }

            @Override
            public void OnPulseResult(String pulse) {
                Log.e(TAG, "OnPulseResult == " + pulse);
            }

            @Override
            public void OnPulseCheckStop() {
                Log.e(TAG, "OnPulseCheckStop == ");
            }
        }).setPulseTimeLimit(20000, 1000);
    }

    private void stop() {
        heartSupport.stopPulseCheck();
    }
}
