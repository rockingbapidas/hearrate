package com.vantagecircle.heartrate.activity.presenter;

import android.os.Build;
import android.util.Log;

import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraCallBack;
import com.vantagecircle.heartrate.camera.CameraNew;
import com.vantagecircle.heartrate.camera.CameraOld;
import com.vantagecircle.heartrate.data.HeartM;
import com.vantagecircle.heartrate.processing.Processing;
import com.vantagecircle.heartrate.utils.TYPE;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bapidas on 10/10/17.
 */

public class HeartActivityPresenter {
    private final String TAG = HeartActivityPresenter.class.getSimpleName();
    private HeartActivity heartActivity;

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

    private CameraNew cameraNew;
    private CameraOld cameraOld;
    private HeartM heartM;

    public HeartActivityPresenter(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    public void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startTime = System.currentTimeMillis();
            heartM = new HeartM();
            cameraNew = new CameraNew(heartActivity);
            cameraNew.open().setPreviewCallBack(new CameraCallBack() {
                @Override
                public void onFrameCallback(byte[] data, int width, int height) {
                    Log.d(TAG, "Using width = " + width + " height = " + height);
                    calculateHeartRate(data, width, height);
                }
            });
        } else {
            startTime = System.currentTimeMillis();
            heartM = new HeartM();
            cameraOld = new CameraOld(heartActivity);
            cameraOld.open().setPreviewCallBack(new CameraCallBack() {
                @Override
                public void onFrameCallback(byte[] data, int width, int height) {
                    Log.d(TAG, "Using width = " + width + " height = " + height);
                    calculateHeartRate(data, width, height);
                }
            });
        }
    }

    public void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            cameraNew.close();
        } else {
            cameraOld.close();
        }
    }

    private void calculateHeartRate(byte[] data, int width, int height){
        if (!processing.compareAndSet(false, true))
            return;
        int imgAvg = Processing.getInstance().decodeYUV420SPtoRedAvg(data.clone(), width, height);
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false);
            return;
        }
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
            heartActivity.updateHeartRate(heartM);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }
}
