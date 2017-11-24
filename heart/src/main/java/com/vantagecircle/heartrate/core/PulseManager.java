package com.vantagecircle.heartrate.core;

import android.view.SurfaceHolder;

import com.vantagecircle.heartrate.camera.PreviewListener;
import com.vantagecircle.heartrate.processing.ArrayTransform;
import com.vantagecircle.heartrate.processing.RGBCalculation;
import com.vantagecircle.heartrate.camera.CameraSupport;

import org.apache.commons.math3.util.FastMath;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by bapidas on 22/11/17.
 */

public class PulseManager implements PulseSupport {
    private static final String TAG = PulseManager.class.getSimpleName();
    private CameraSupport mCameraSupport;
    private SurfaceHolder mSurfaceHolder;
    private PulseListener mPulseListener;
    private DecimalFormat mDecimalFormat;
    private Thread mThread;

    private boolean isPixelFound = false;
    private boolean isStarted;
    private boolean isBeepEnable;
    private boolean isRunning;

    private int mMeasurementTime;
    private int mHeartBeat;
    private int beepCount;

    private int aV;
    private int aW;
    private int aX;

    private ArrayList<Double> zeroIndexPixelArray = new ArrayList<>();
    private ArrayList<Double> firstIndexPixelArray = new ArrayList<>();
    private ArrayList<Double> secondIndexPixelArray = new ArrayList<>();
    private ArrayList<Integer> thirdIndexPixelArray = new ArrayList<>();
    private ArrayList<Double> timeStampDiffArray = new ArrayList<>();
    private ArrayList<Double> beatsAverageArray = new ArrayList<>();

    private double[] beatsArray = new double[300];
    private double[] graphArray = new double[150];
    private double currentTimeStamp;
    private double thirdIndexPixel;

    private double aK;
    private double aH;

    private long graphStartTime = 0;
    private long beepStartTime = 0;
    private long mStartTime;

    public PulseManager(CameraSupport mCameraSupport) {
        this.mCameraSupport = mCameraSupport;
        this.mDecimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    }

    @Override
    public PulseSupport startMeasure() {
        this.startThread();
        this.mCameraSupport.openCamera();
        this.isPixelFound = false;
        this.zeroIndexPixelArray.clear();
        this.firstIndexPixelArray.clear();
        this.secondIndexPixelArray.clear();
        this.mCameraSupport.prepareCamera();
        this.thirdIndexPixelArray.clear();
        this.isStarted = true;
        this.isRunning = true;

        this.mCameraSupport.addOnPreviewListener(mPreviewListener);
        this.mCameraSupport.startPreview();
        return this;
    }

    @Override
    public void stopMeasure() {
        this.clear();
        this.mCameraSupport.releaseCamera();
        this.stopThread();
    }

    @Override
    public PulseSupport setSurfaceHolder(SurfaceHolder mSurfaceHolder) {
        this.mSurfaceHolder = mSurfaceHolder;
        this.mSurfaceHolder.addCallback(mSurfaceCallback);
        return this;
    }

    @Override
    public PulseSupport setMeasurementTime(int time) {
        if (time == 0) {
            this.mMeasurementTime = 15 * 1000;
        } else {
            this.mMeasurementTime = time * 1000;
        }
        return this;
    }

    @Override
    public PulseSupport setBeep(boolean beepEnable) {
        this.isBeepEnable = beepEnable;
        return this;
    }

    @Override
    public void addOnPulseListener(PulseListener mPulseListener) {
        this.mPulseListener = mPulseListener;
    }


    private void startThread() {
        if (this.mThread == null) {
            this.mThread = new Thread(mBackgroundThread);
            this.mThread.setPriority(1);
            this.mThread.start();
        }
    }

    private void stopThread() {
        if (this.mThread != null) {
            this.mThread.interrupt();
            this.mThread = null;
        }
    }

    private void backgroundProcess() {
        while (this.mThread != null) {
            if (this.isPixelFound && this.beatsAverageArray.size() > this.aX) {
                ArrayTransform.compare(this.timeStampDiffArray, this.beatsAverageArray);
                int length = ArrayTransform.result.length - ArrayTransform.samples.length;
                for (int i = 0; i < FastMath.min(300, length - this.aX); i++) {
                    this.beatsArray[299 - i] = -ArrayTransform.result[(length - 1) - i];
                }
                double[] a = ArrayTransform.sort(this.beatsArray);
                this.mHeartBeat = (int) (a[0] * 60.0d);
                this.aH = a[1];

                if (System.currentTimeMillis() - this.graphStartTime > 30) {
                    this.graphStartTime = System.currentTimeMillis();
                    System.arraycopy(this.beatsArray, 150, this.graphArray, 0, 150);
                    this.graphArray = ArrayTransform.m15895a(this.graphArray);
                    //can render measure graph here
                }

                if (((ArrayTransform.result != null) & this.isBeepEnable)) {
                    while (this.beepCount < ArrayTransform.result.length - (ArrayTransform.samples.length / 2)) {
                        if (this.aH > 0.01d) {
                            if ((ArrayTransform.result[this.beepCount + 1] > 0.0d & ArrayTransform.result[this.beepCount + -1] < 0.0d) && ((double) (System.currentTimeMillis() - this.beepStartTime)) > (1.0d / a[0]) * 750.0d) {
                                //can play beep sound here
                                this.beepStartTime = System.currentTimeMillis();
                            }
                        }
                        this.beepCount++;
                    }
                }
            }
        }
    }

    private boolean isValidPixel() {
        if (this.firstIndexPixelArray.size() <= 5) {
            return false;
        }
        double[] doubles = new double[]{
                (Collections.min(this.zeroIndexPixelArray.subList(this.firstIndexPixelArray.size() - 5, this.firstIndexPixelArray.size()))),
                (Collections.min(this.firstIndexPixelArray.subList(this.firstIndexPixelArray.size() - 5, this.firstIndexPixelArray.size()))),
                (Collections.min(this.secondIndexPixelArray.subList(this.firstIndexPixelArray.size() - 5, this.firstIndexPixelArray.size())))
        };
        return doubles[0] > 0.95d & doubles[2] + doubles[1] > 1.15d & !this.isPixelFound;
    }

    private void calculatePulse(double[] doubles) {
        //store third number index value
        this.thirdIndexPixel = doubles[3];
        //store timestamp subtracts from current and start
        this.currentTimeStamp = (double) (System.currentTimeMillis() - this.mStartTime);
        //add new third index data from red pixel array
        this.thirdIndexPixelArray.add((int) this.thirdIndexPixel);
        //turn on Flash
        this.manageFlash();
        //off manageFlash if pixel not correct and third index is greater than 25
        if (!this.isPixelFound & this.thirdIndexPixelArray.size() > 25) {
            this.mCameraSupport.disableFlash();
            this.thirdIndexPixelArray.clear();
        }
        //check array if pixel not found yet and it already started
        if (!this.isPixelFound & this.isStarted) {
            this.zeroIndexPixelArray.add(doubles[0]);
            this.firstIndexPixelArray.add(doubles[1]);
            this.secondIndexPixelArray.add(doubles[2]);
            if (this.isValidPixel()) {
                if (this.mPulseListener != null) {
                    this.mPulseListener.OnPulseCheckStarted();
                }
                this.initVariables();
            }
        }
        //check i pixel found and post heart rate
        if (this.isPixelFound) {
            if (doubles[0] < 0.95d & doubles[2] + doubles[1] <= 1.15d) {
                if (this.beatsAverageArray.size() > this.aW) {
                    if (this.mPulseListener != null) {
                        this.mPulseListener.OnPulseCheckStopped();
                    }
                    this.clear();
                }
                //check if current timestamp getter than 9 second
                if (this.currentTimeStamp > 9000.0d) {
                    if (this.aH > 0.01d & this.mHeartBeat < 210 & this.mHeartBeat > 35) {
                        if (this.mPulseListener != null) {
                            this.mPulseListener.OnPulseCheckFinished(this.mDecimalFormat.format((long) this.mHeartBeat), false);
                        }
                        this.isRunning = false;
                        this.isStarted = true;
                    }
                }
            } else if (this.currentTimeStamp > ((double) this.mMeasurementTime)) {
                //check if heart rate id getter than 35 and less than 210 and post
                if (this.aH > 0.01d & this.mHeartBeat < 210 & this.mHeartBeat > 35) {
                    if (this.mPulseListener != null) {
                        this.mPulseListener.OnPulseCheckFinished(this.mDecimalFormat.format((long) this.mHeartBeat), true);
                    }
                    this.clear();
                    this.isRunning = false;
                    this.isStarted = true;
                }
            }
        }
        //check if pixel found and post heart rate
        if (this.isPixelFound) {
            if (!this.mCameraSupport.isAutoExposureLockEnabled() & this.beatsAverageArray.size() > this.aV) {
                this.mCameraSupport.enableAutoExposureLock();
                this.aW = this.beatsAverageArray.size();
                this.aX = this.beatsAverageArray.size() + 25;
            }

            int size = this.timeStampDiffArray.size();
            if (size <= 1) {
                this.beatsAverageArray.add(this.thirdIndexPixel);
                this.timeStampDiffArray.add(this.currentTimeStamp);
            } else if (this.currentTimeStamp > this.timeStampDiffArray.get(size - 1)) {
                this.beatsAverageArray.add(this.thirdIndexPixel);
                this.timeStampDiffArray.add(this.currentTimeStamp);
            }
            if (this.aH > 0.01d & this.mHeartBeat > 35 & this.currentTimeStamp > this.aK & this.mHeartBeat < 210) {
                if (this.mPulseListener != null) {
                    this.mPulseListener.OnPulseCheckRate(this.mDecimalFormat.format((long) this.mHeartBeat));
                }
                this.aK = this.currentTimeStamp + 500.0d;
            }
        }
    }

    private void manageFlash() {
        if (this.mCameraSupport.getLightIntensity() == 30 & !isRunning) {
            this.isStarted = false;
            this.isRunning = true;
            //
        }
        if (!this.mCameraSupport.isFlashEnabled() & this.mCameraSupport.isFlashSupported() & this.isStarted) {
            int intensity = this.mCameraSupport.getLightIntensity();
            int size = this.thirdIndexPixelArray.size();
            if (size > 10) {
                intensity = Collections.max(this.thirdIndexPixelArray.subList(size - 10, size));
            }
            if (intensity < mCameraSupport.getLightIntensity()) {
                this.mCameraSupport.enableFlash();
                this.thirdIndexPixelArray.clear();
                this.mCameraSupport.disableAutoExposureLock();
                this.aV = this.beatsAverageArray.size() + 25;
                this.aW = this.aV;
                this.aX = this.aV + 25;
            }
        }
    }

    private void clear() {
        this.mCameraSupport.disableAutoExposureLock();
        //
        this.mCameraSupport.disableFlash();
        this.thirdIndexPixelArray.clear();
        this.isPixelFound = false;
        this.zeroIndexPixelArray.clear();
        this.firstIndexPixelArray.clear();
        this.secondIndexPixelArray.clear();
    }

    private void initVariables() {
        //
        Arrays.fill(this.beatsArray, 0.0d);
        this.mStartTime = System.currentTimeMillis();
        this.currentTimeStamp = 0.0d;

        this.beatsAverageArray.clear();
        this.timeStampDiffArray.clear();

        this.isPixelFound = true;

        this.aK = 1000.0d;
        this.beepCount = 1;
        this.aH = 0.0d;
        this.aV = 25;
        this.aW = this.aV;
        this.aX = this.aV + 25;

        ArrayTransform.result = null;
    }


    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mCameraSupport.setPreviewHolder(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mCameraSupport.preparePreview(i1, i2);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mCameraSupport.releaseCamera();
        }
    };

    private Runnable mBackgroundThread = new Runnable() {
        @Override
        public void run() {
            backgroundProcess();
        }
    };

    private PreviewListener mPreviewListener = new PreviewListener() {
        @Override
        public void OnPreviewCallback(byte[] data, int mWidth, int mHeight) {
            double[] doubles = RGBCalculation.calculate(data.clone(), mWidth, mHeight);
            calculatePulse(doubles);
        }
    };
}
