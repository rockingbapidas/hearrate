package com.vantagecircle.heartrate.activity.presenter;

import android.content.Context;
import android.databinding.ObservableField;
import android.hardware.Camera;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.camera.CameraNew;
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

    private Camera camera = null;
    private PowerManager.WakeLock wakeLock = null;

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

    public HeartActivityPresenter(HeartActivity heartActivity) {
        this.heartActivity = heartActivity;
    }

    public void load() {
        Vibrator v = (Vibrator) heartActivity.getSystemService(Context.VIBRATOR_SERVICE);
        PowerManager pm = (PowerManager) heartActivity.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    public void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new CameraNew(heartActivity).open(0);
        }
        wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
        camera.setPreviewCallback(previewCallback);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        Camera.Size size = getSmallestPreviewSize(176, 144, parameters);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
            Log.d(TAG, "Using width = " + size.width + " height = " + size.height);
        }
        camera.setParameters(parameters);
        camera.startPreview();
    }

    public void stop() {
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();
            if (!processing.compareAndSet(false, true))
                return;
            int width = size.width;
            int height = size.height;

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
                Log.e(TAG, "beatsAvg ==== " + beatsAvg);
                String beatsPerMinuteValue = String.valueOf(beatsAvg);
                Log.e(TAG, "beatsPerMinuteValue ==== " + beatsPerMinuteValue);
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };
}
