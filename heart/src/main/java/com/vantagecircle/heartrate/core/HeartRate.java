package com.vantagecircle.heartrate.core;

import android.os.CountDownTimer;
import android.util.Log;

import com.vantagecircle.heartrate.camera.PreviewListener;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.utils.TYPE;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bapidas on 03/11/17.
 */

public class HeartRate implements HeartSupport, PreviewListener {
    private static final String TAG = HeartRate.class.getSimpleName();

    private CameraSupport mCameraSupport;
    private TimerListener mTimerListener;
    private PulseListener mPulseListener;
    private FingerListener mFingerListener;

    private long timeLimit = 0;
    private boolean isTimeRunning = false;
    private CountDownTimer countDownTimer;

    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];

    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final double[] beatsArray = new double[beatsArraySize];

    private double beats = 0;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private long startTime = 0;
    private TYPE currentType = TYPE.GREEN;

    private int errorCount = 0;
    private int successCount = 0;

    public HeartRate(CameraSupport mCameraSupport) {
        this.mCameraSupport = mCameraSupport;
    }

    public HeartSupport getHeartSupport() {
        return this;
    }

    @Override
    public HeartSupport startPulseCheck(long timeLimit) {
        this.timeLimit = timeLimit;
        if (mCameraSupport != null) {
            if (!mCameraSupport.isCameraInUse()) {
                errorCount = 0;
                successCount = 0;
                startTime = System.currentTimeMillis();
                startTimer();
                mCameraSupport.open().addOnPreviewListener(this);
                if (mTimerListener != null) {
                    mTimerListener.OnTimerStarted();
                }
            } else {
                throw new RuntimeException("Camera is already in use state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
        return this;
    }

    @Override
    public HeartSupport startPulseCheck() {
        if (mCameraSupport != null) {
            if (!mCameraSupport.isCameraInUse()) {
                errorCount = 0;
                successCount = 0;
                startTime = System.currentTimeMillis();
                startTimer();
                mCameraSupport.open().addOnPreviewListener(this);
                if (mTimerListener != null) {
                    mTimerListener.OnTimerStarted();
                }
            } else {
                throw new RuntimeException("Camera is already in use state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
        return this;
    }

    @Override
    public HeartSupport addOnResultListener(PulseListener pulseListener) {
        this.mPulseListener = pulseListener;
        return this;
    }

    @Override
    public HeartSupport addOnFingerListener(FingerListener fingerListener) {
        this.mFingerListener = fingerListener;
        return this;
    }

    @Override
    public HeartSupport addOnTimerListener(TimerListener timerListener) {
        this.mTimerListener = timerListener;
        return this;
    }

    @Override
    public void stopPulseCheck() {
        if (mCameraSupport != null) {
            if (mCameraSupport.isCameraInUse()) {
                mCameraSupport.close();
                if (isTimeRunning) {
                    countDownTimer.cancel();
                    isTimeRunning = false;
                }
                if (mTimerListener != null) {
                    mTimerListener.OnTimerStopped();
                }
            } else {
                throw new RuntimeException("Camera is already in close state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
    }

    @Override
    public void OnPreviewData(byte[] data) {
        //TODO
    }

    @Override
    public void OnPreviewCount(int count) {
        calculatePulse(count);
    }

    private void startTimer() {
        if (timeLimit != 0) {
            countDownTimer = new CountDownTimer(timeLimit, 500) {

                public void onTick(long millisUntilFinished) {
                    isTimeRunning = true;
                    if (mTimerListener != null) {
                        mTimerListener.OnTimerRunning(millisUntilFinished);
                    }
                }

                public void onFinish() {
                    isTimeRunning = false;
                    stopPulseCheck();
                }
            };
            countDownTimer.start();
        }
    }

    private void calculatePulse(int pixelAverage) {
        Log.e(TAG, "pixel === " + pixelAverage);
        if (!processing.compareAndSet(false, true)) {
            return;
        }

        if (pixelAverage == 0 || pixelAverage >= 255) {
            processing.set(false);
            return;
        }

        /*if (pixelAverage < 200) {
            errorCount++;
            if (mFingerListener != null) {
                mFingerListener.OnFingerDetectFailed(errorCount);
            }
            processing.set(false);
        } else {
            successCount++;
            if (mFingerListener != null) {
                mFingerListener.OnFingerDetected(successCount);
            }
        }*/

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
        if (pixelAverage < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
            }
        } else if (pixelAverage > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = pixelAverage;
        averageIndex++;

        if (newType != currentType) {
            currentType = newType;
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;

        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            Log.d(TAG, "BPS:" + bps);
            double dpm =  (bps * 60d);
            Log.d(TAG, "BPS:" + dpm);
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
            for (double aBeatsArray : beatsArray) {
                if (aBeatsArray > 0) {
                    beatsArrayAvg += aBeatsArray;
                    beatsArrayCnt++;
                }
            }

            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            String beatsPerMinuteValue = String.valueOf(beatsAvg);

            if (mPulseListener != null) {
                mPulseListener.OnPulseResult(beatsPerMinuteValue);
            }
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }
}
