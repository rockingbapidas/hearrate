package com.vantagecircle.heartrate.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.SurfaceHolder;

import com.vantagecircle.heartrate.camera.PreviewListener;
import com.vantagecircle.heartrate.core.processing.Comparator;
import com.vantagecircle.heartrate.core.processing.Calculation;
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
    private Activity mActivity;
    private Thread mThread;

    private boolean isPixelFound = false;
    private boolean isStarted;
    private boolean isBeepEnable;
    private boolean isRunning;

    private int mMeasurementTime;
    private int mHeartBeat;
    private int beepCount;
    private int sWidth;
    private int sHeight;

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

    private double aK;
    private double aH;

    private long graphStartTime = 0;
    private long beepStartTime = 0;
    private long mStartTime;

    public PulseManager(Activity mActivity, CameraSupport mCameraSupport) {
        this.mActivity = mActivity;
        this.mCameraSupport = mCameraSupport;
        this.mDecimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    }

    @Override
    public void setSurface(SurfaceHolder mSurfaceHolder) {
        this.mSurfaceHolder = mSurfaceHolder;
        this.mSurfaceHolder.addCallback(mSurfaceCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mActivity.getWindow().addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        }
    }

    @Override
    public PulseSupport startMeasure() {
        this.startThread();

        this.mCameraSupport.openCamera();
        this.mCameraSupport.prepareCamera();
        this.mCameraSupport.changedSurface(sWidth, sHeight);
        this.mCameraSupport.createSurface(mSurfaceHolder);
        this.mCameraSupport.addOnPreviewListener(mPreviewListener);
        this.mCameraSupport.startPreview();

        this.isPixelFound = false;
        this.zeroIndexPixelArray.clear();
        this.firstIndexPixelArray.clear();
        this.secondIndexPixelArray.clear();
        this.thirdIndexPixelArray.clear();

        this.mCameraSupport.disableAutoExposureLock();
        this.mCameraSupport.setLightIntensity(50);
        this.mCameraSupport.addSensorListener();

        this.isStarted = true;
        this.isRunning = true;

        return this;
    }

    @Override
    public void stopMeasure() {
        this.clear();
        this.mCameraSupport.stopPreview();
        this.mCameraSupport.releaseCamera();
        this.stopThread();
        this.mCameraSupport.removeSensorListener();
    }

    @Override
    public void resumeMeasure() {
        this.isStarted = true;
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
        Comparator.result = null;
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            /*mCameraSupport.createSurface(mSurfaceHolder);
            mCameraSupport.addOnPreviewListener(mPreviewListener);*/
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            sWidth = i1;
            sHeight = i2;
            /*mCameraSupport.changedSurface(sWidth, sHeight);*/
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
           /* mSurfaceHolder.removeCallback(this);
            mSurfaceHolder.getSurface().release();*/
        }
    };

    private Runnable mBackgroundThread = new Runnable() {
        @Override
        public void run() {
            backgroundProcess();
        }
    };

    private void backgroundProcess() {
        while (this.mThread != null) {
            if (this.isPixelFound && this.beatsAverageArray.size() > this.aX) {
                Comparator.compare(this.timeStampDiffArray, this.beatsAverageArray);
                int length = Comparator.result.length - Comparator.samples.length;
                for (int i = 0; i < FastMath.min(300, length - this.aX); i++) {
                    this.beatsArray[299 - i] = -Comparator.result[(length - 1) - i];
                }
                double[] a = Comparator.sort(this.beatsArray);
                this.mHeartBeat = (int) (a[0] * 60.0d);
                this.aH = a[1];

                if (System.currentTimeMillis() - this.graphStartTime > 30) {
                    this.graphStartTime = System.currentTimeMillis();
                    System.arraycopy(this.beatsArray, 150, this.graphArray, 0, 150);
                    this.graphArray = Comparator.getArray(this.graphArray);
                    //
                }

                if (((Comparator.result != null) & this.isBeepEnable)) {
                    while (this.beepCount < Comparator.result.length - (Comparator.samples.length / 2)) {
                        if (this.aH > 0.01d) {
                            if ((Comparator.result[this.beepCount + 1] > 0.0d & Comparator.result[this.beepCount + -1] < 0.0d) && ((double) (System.currentTimeMillis() - this.beepStartTime)) > (1.0d / a[0]) * 750.0d) {
                                //
                                this.beepStartTime = System.currentTimeMillis();
                            }
                        }
                        this.beepCount++;
                    }
                }
            }
        }
    }

    private PreviewListener mPreviewListener = new PreviewListener() {
        @Override
        public void OnPreviewCallback(byte[] data, int mWidth, int mHeight) {
            double[] doubles = Calculation.calculate(data.clone(), mWidth, mHeight);
            calculatePulse(doubles);
        }
    };

    private void calculatePulse(double[] doubles) {
        double thirdIndexPixel = doubles[3];
        this.currentTimeStamp = (double) (System.currentTimeMillis() - this.mStartTime);
        this.thirdIndexPixelArray.add((int) thirdIndexPixel);
        this.manageFlash();

        if (!this.isPixelFound & this.thirdIndexPixelArray.size() > 25) {
            this.mCameraSupport.disableFlash();
            this.thirdIndexPixelArray.clear();
        }

        if (!this.isPixelFound & this.isStarted) {
            this.zeroIndexPixelArray.add(doubles[0]);
            this.firstIndexPixelArray.add(doubles[1]);
            this.secondIndexPixelArray.add(doubles[2]);
            if (this.isValidPixel()) {

                //new code
                if (this.mPulseListener != null) {
                    this.mPulseListener.OnPulseCheckStarted();
                }

                this.initVariables();
            }
        }

        if (this.isPixelFound) {
            if (doubles[0] < 0.95d & doubles[2] + doubles[1] <= 1.15d) {
                if (this.beatsAverageArray.size() > this.aW) {

                    //new code
                    if (this.mPulseListener != null) {
                        this.mPulseListener.OnPulseCheckStopped();
                    }

                    this.clear();
                }

                if (this.currentTimeStamp > 9000.0d) {
                    if (this.aH > 0.01d & this.mHeartBeat < 210 & this.mHeartBeat > 35) {

                        //new code
                        if (this.mPulseListener != null) {
                            this.mPulseListener.OnPulseCheckFinished(this.mDecimalFormat.format((long) this.mHeartBeat), false);
                        }

                        this.isRunning = false;
                        this.isStarted = true;
                    } else { //new code
                        if (this.mPulseListener != null) {
                            this.mPulseListener.OnPulseCheckError();
                        }
                    }
                }
            } else if (this.currentTimeStamp > ((double) this.mMeasurementTime)) {
                if (this.aH > 0.01d & this.mHeartBeat < 210 & this.mHeartBeat > 35) {

                    //new code
                    if (this.mPulseListener != null) {
                        this.mPulseListener.OnPulseCheckFinished(this.mDecimalFormat.format((long) this.mHeartBeat), true);
                    }

                    this.clear();
                    this.isRunning = false;
                    this.isStarted = true;
                } else { //new code
                    if (this.mPulseListener != null) {
                        this.mPulseListener.OnPulseCheckError();
                    }
                }
            }
        }

        if (this.isPixelFound) {
            if (!this.mCameraSupport.isAutoExposureLockEnabled() & this.beatsAverageArray.size() > this.aV) {
                this.mCameraSupport.enableAutoExposureLock();
                this.aW = this.beatsAverageArray.size();
                this.aX = this.beatsAverageArray.size() + 25;
            }

            int size = this.timeStampDiffArray.size();
            if (size <= 1) {
                this.beatsAverageArray.add(thirdIndexPixel);
                this.timeStampDiffArray.add(this.currentTimeStamp);
            } else if (this.currentTimeStamp > this.timeStampDiffArray.get(size - 1)) {
                this.beatsAverageArray.add(thirdIndexPixel);
                this.timeStampDiffArray.add(this.currentTimeStamp);
            }
            if (this.aH > 0.01d & this.mHeartBeat > 35 & this.currentTimeStamp > this.aK & this.mHeartBeat < 210) {

                //new code
                if (this.mPulseListener != null) {
                    this.mPulseListener.OnPulseCheckRate(this.mDecimalFormat.format((long) this.mHeartBeat));
                }

                this.aK = this.currentTimeStamp + 500.0d;
            }
        }
    }
}
