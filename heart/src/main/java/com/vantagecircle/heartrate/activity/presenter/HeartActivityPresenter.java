package com.vantagecircle.heartrate.activity.presenter;

import android.Manifest;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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
    private CameraSupport cameraSupport;

    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];

    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];

    private double beats = 0;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private long startTime = 0;
    private TYPE currentType = TYPE.GREEN;

    private HeartM heartM;
    private int errorCount = 0;
    private int successCount = 0;
    private CountDownTimer countDownTimer;
    private boolean isTimeRunning;

    public HeartActivityPresenter(HeartActivity heartActivity, CameraSupport cameraSupport) {
        this.heartActivity = heartActivity;
        this.cameraSupport = cameraSupport;
    }

    public void askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ToolsUtils.getInstance().isHasPermissions(heartActivity, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Permission already accepted");
            } else {
                ActivityCompat.requestPermissions(heartActivity, new String[]{Manifest.permission.CAMERA},
                        Constant.REQUEST_CAMERA_PERMISSION);
            }
        } else {
            Log.d(TAG, "No need permission");
        }
    }

    public void handleClick() {
        if (heartM != null && heartM.isStarted()) {
            stop();
        } else {
            start();
        }
    }

    private void start() {
        if (!cameraSupport.isCameraInUse()) {
            errorCount = 0;
            successCount = 0;
            if (heartM != null) {
                heartM.setBeatsPerMinuteValue("-----");
                heartM.setStarted(true);
                heartActivity.bindHeartRate(heartM);
            } else {
                heartM = new HeartM();
                heartM.setBeatsPerMinuteValue("-----");
                heartM.setStarted(true);
                heartActivity.bindHeartRate(heartM);
            }
            startTime = System.currentTimeMillis();
            startTimer();
            cameraSupport.open().setPreviewCallBack(new CameraCallBack() {
                @Override
                public void onFrameCallback(int pixelAverageCount) {
                    calculateHeartRate(pixelAverageCount);
                }
            });
        }
    }

    public void stop() {
        if (cameraSupport.isCameraInUse()) {
            cameraSupport.close();
            if (heartM != null) {
                heartM.setStarted(false);
                heartActivity.bindHeartRate(heartM);
            }
            if (isTimeRunning) {
                countDownTimer.cancel();
                isTimeRunning = false;
            }
        }
    }

    private void startTimer() {
        isTimeRunning = true;
        countDownTimer = new CountDownTimer(20000, 500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                isTimeRunning = false;
                stop();
                if (errorCount > successCount) {
                    Log.e(TAG, "Heart rate  pulse is inaccurate");
                } else {
                    Log.e(TAG, "Heart rate pulse is accurate");
                }
            }
        };
        countDownTimer.start();
    }

    private void calculateHeartRate(int imgAvg) {
        if (!processing.compareAndSet(false, true)) {
            return;
        }

        if (imgAvg == 0 || imgAvg >= 255) {
            heartM.setDetectHeartRate(false);
            heartActivity.bindHeartRate(heartM);

            errorCount++;
            processing.set(false);
        } else if (imgAvg < 170) {
            heartM.setDetectHeartRate(false);
            heartActivity.bindHeartRate(heartM);

            errorCount++;
            processing.set(false);
        } else {
            heartM.setDetectHeartRate(true);
            heartActivity.bindHeartRate(heartM);

            successCount++;
            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int anAverageArray : averageArray) {
                if (anAverageArray > 0) {
                    averageArrayAvg += anAverageArray;
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;

            if (totalTimeInSecs >= 10) {

                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int aBeatsArray : beatsArray) {
                    if (aBeatsArray > 0) {
                        beatsArrayAvg += aBeatsArray;
                        beatsArrayCnt++;
                    }
                }

                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                String beatsPerMinuteValue = String.valueOf(beatsAvg);
                heartM.setBeatsPerMinuteValue(beatsPerMinuteValue);
                heartActivity.bindHeartRate(heartM);
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    }

    private void createGraph() {
        LineChart mChart = heartActivity.findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setData(generateLineData());
        mChart.animateX(3000);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

        Typeface tf = Typeface.createFromAsset(heartActivity.getAssets(),
                "OpenSans-Light.ttf");
        Legend l = mChart.getLegend();
        l.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setAxisMaximum(1.2f);
        leftAxis.setAxisMinimum(-1.2f);

        mChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
    }

    private LineData generateLineData() {
        Typeface tf = Typeface.createFromAsset(heartActivity.getAssets(),
                "OpenSans-Light.ttf");
        ArrayList<ILineDataSet> sets = new ArrayList<>();

        LineDataSet ds = new LineDataSet(FileUtils
                .loadEntriesFromAssets(heartActivity.getAssets(), "cosine.txt"),
                "Cosine function");

        ds.setLineWidth(3f);
        ds.setDrawCircles(false);
        ds.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);

        sets.add(ds);
        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }
}
