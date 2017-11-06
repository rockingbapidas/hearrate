package com.vantagecircle.heartrate.core;

import android.os.CountDownTimer;

import com.vantagecircle.heartrate.camera.CameraCallBack;
import com.vantagecircle.heartrate.camera.CameraSupport;
import com.vantagecircle.heartrate.utils.TYPE;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bapidas on 03/11/17.
 */

public class HeartRate implements HeartSupport, CameraCallBack {
    private static final String TAG = HeartRate.class.getSimpleName();

    private CameraSupport cameraSupport;
    private StatusListener statusListener;
    private PulseListener pulseListener;

    private long timeLimit = 0;
    private boolean isTimeRunning = false;
    private CountDownTimer countDownTimer;

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

    private int errorCount = 0;
    private int successCount = 0;

    public HeartRate(CameraSupport cameraSupport) {
        this.cameraSupport = cameraSupport;
    }

    @Override
    public void startPulseCheck(long timeLimit, PulseListener pulseListener) {
        this.timeLimit = timeLimit;
        this.pulseListener = pulseListener;
        if (cameraSupport != null) {
            if (!cameraSupport.isCameraInUse()) {
                errorCount = 0;
                successCount = 0;
                startTime = System.currentTimeMillis();
                startTimer();
                cameraSupport.open().setOnPreviewListener(this);
                if (statusListener != null) {
                    statusListener.OnCheckStarted();
                }
            } else {
                throw new RuntimeException("Camera is already in use state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
    }

    @Override
    public void startPulseCheck(PulseListener pulseListener) {
        this.pulseListener = pulseListener;
        if (cameraSupport != null) {
            if (!cameraSupport.isCameraInUse()) {
                errorCount = 0;
                successCount = 0;
                startTime = System.currentTimeMillis();
                startTimer();
                cameraSupport.open().setOnPreviewListener(this);
                if (statusListener != null) {
                    statusListener.OnCheckStarted();
                }
            } else {
                throw new RuntimeException("Camera is already in use state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
    }

    @Override
    public void setOnStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void OnPixelAverage(int pixelAverage) {
        calculatePulse(pixelAverage);
    }

    @Override
    public void stopPulseCheck() {
        if (cameraSupport != null) {
            if (cameraSupport.isCameraInUse()) {
                cameraSupport.close();
                if (isTimeRunning) {
                    countDownTimer.cancel();
                    isTimeRunning = false;
                }
                if (statusListener != null) {
                    statusListener.OnCheckStopped();
                }
            } else {
                throw new RuntimeException("Camera is already in close state");
            }
        } else {
            throw new RuntimeException("Camera Support is null");
        }
    }

    private void startTimer() {
        if (timeLimit != 0) {
            countDownTimer = new CountDownTimer(timeLimit, 1000) {

                public void onTick(long millisUntilFinished) {
                    isTimeRunning = true;
                    if (statusListener != null) {
                        statusListener.OnCheckRunning();
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
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        if (pixelAverage == 0 || pixelAverage >= 255) {
            errorCount++;
            processing.set(false);

            if (pulseListener != null) {
                pulseListener.OnPulseDetectFailed(errorCount);
            }
        } else if (pixelAverage < 170) {
            errorCount++;
            processing.set(false);

            if (pulseListener != null) {
                pulseListener.OnPulseDetectFailed(errorCount);
            }
        } else {
            successCount++;
            if (pulseListener != null) {
                pulseListener.OnPulseDetected(successCount);
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

            if (totalTimeInSecs >= 5) {
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

                if (pulseListener != null) {
                    pulseListener.OnPulseResult(beatsPerMinuteValue);
                }
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    }
}
