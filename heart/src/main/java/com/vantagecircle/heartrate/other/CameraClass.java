package com.vantagecircle.heartrate.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.Surface;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by bapidas on 19/11/17.
 */

public class CameraClass implements SensorEventListener {
    private Activity mActivity;

    private Sensor mSensor;
    private Vibrator mVibrator;
    private SensorManager mSensorManager;
    private DecimalFormat mDecimalFormat;
    private Camera mCamera;

    private boolean isSensorLight = false;
    private boolean isExposureLock = false;
    private boolean isFlashSupported;
    private boolean ai = false;
    private boolean ad;
    private boolean ae;
    private boolean f9373d;

    private int mFlashIntensity;
    private int mCameraId;
    private int mWidth;
    private int mHeight;
    private int mMeasurementTime;
    private int aV;
    private int aW;
    private int aX;
    private int f9375f;
    private int f9376h;
    private int f9371c;

    private ArrayList<Double> f9377i = new ArrayList<>();
    private ArrayList<Double> aa = new ArrayList<>();
    private ArrayList<Double> ab = new ArrayList<>();
    private ArrayList<Integer> bf = new ArrayList<>();
    private ArrayList<Double> aj = new ArrayList<>();
    private ArrayList<Double> ak = new ArrayList<>();

    private double be;
    private double ac;
    private double aK;
    private double aH;

    private double[] aT = new double[150];
    private double[] aU = new double[300];

    private long mStartTime;

    private CameraPreviewCallBack mCameraPreviewCallback;
    private CalculationSupport mCalculationSupport;

    private RunnableClass mRunnableClass;

    public CameraClass(Activity mActivity, CalculationSupport mCalculationSupport) {
        this.mActivity = mActivity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        }
        this.mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        this.mDecimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        this.mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (this.mSensorManager != null) {
            this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        this.mCalculationSupport = mCalculationSupport;
    }

    private void prepare() {
        //mRunnableClass = new RunnableClass(this);
        //mRunnableClass.m15901a();

        this.mCameraId = 0;
        if (openCamera()) {
            setFpsRange();
            setExposureLock();
            this.mSensorManager.registerListener(this, this.mSensor, 3);
            this.isSensorLight = false;

            this.bf.clear();
            this.mFlashIntensity = 50;
            this.ad = true;
            this.ae = true;

            init();
        }
    }

    private void release() {
        setExposureLock();
        releaseFlash();

        this.ai = false;
        this.f9377i.clear();
        this.aa.clear();
        this.ab.clear();

        releaseCallback();

        //mRunnableClass.m15902b();

        this.mSensorManager.unregisterListener(this);
    }

    private boolean openCamera() {
        try {
            releaseCamera();
            this.mCamera = Camera.open(this.mCameraId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void init() {
        int i4 = 0;
        if (this.mCamera != null) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            Camera.Size a = getSmallestPreviewSize(176, 144, parameters);
            if (a != null) {
                parameters.setPreviewSize(a.width, a.height);
            }
            this.mCamera.setParameters(parameters);
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(this.mCameraId, cameraInfo);
            switch (this.mActivity.getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    i4 = 0;
                    break;
                case Surface.ROTATION_90:
                    i4 = 90;
                    break;
                case Surface.ROTATION_180:
                    i4 = 180;
                    break;
                case Surface.ROTATION_270:
                    i4 = 270;
                    break;
            }
            this.mCamera.setDisplayOrientation(cameraInfo.facing == 1 ?
                    (360 - ((i4 + cameraInfo.orientation) % 360)) % 360 :
                    ((cameraInfo.orientation - i4) + 360) % 360);
            this.mWidth = parameters.getPreviewSize().width;
            this.mHeight = parameters.getPreviewSize().height;
            this.mCameraPreviewCallback = new CameraPreviewCallBack(this);
            this.mCamera.setPreviewCallback(this.mCameraPreviewCallback);
            this.mCamera.startPreview();
        }
    }

    private void setFpsRange() {
        this.ai = false;
        this.f9377i.clear();
        this.aa.clear();
        this.ab.clear();

        setExposureLock();
        if (this.mCamera != null) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            if (parameters.getSupportedPreviewFpsRange() != null) {
                int[] fpsRange = getFpsRange(parameters.getSupportedPreviewFpsRange());
                parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
            }
            this.mCamera.setParameters(parameters);
        }
        this.isFlashSupported = checkFlash();
    }

    private void setExposureLock() {
        if (this.mCamera != null) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            if (parameters.isAutoExposureLockSupported()) {
                parameters.setAutoExposureLock(false);
            }
            this.mCamera.setParameters(parameters);
            this.isExposureLock = false;
        }
    }

    private void changeExposureLock() {
        int i = 0;
        if (((!this.isExposureLock ? 1 : 0) & (this.ak.size() > this.aV ? 1 : 0)) != 0) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            boolean isAutoExposureLockSupported = parameters.isAutoExposureLockSupported();
            if (!parameters.getAutoExposureLock()) {
                i = 1;
            }
            if ((i != 0 & isAutoExposureLockSupported)) {
                parameters.setAutoExposureLock(true);
            }
            this.mCamera.setParameters(parameters);
            this.isExposureLock = true;
            this.aW = this.ak.size();
            this.aX = this.ak.size() + 25;
        }
    }

    private boolean checkFlash() {
        if (this.mCamera == null) {
            return false;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (parameters.getFlashMode() == null) {
            return false;
        }
        List supportedFlashModes = parameters.getSupportedFlashModes();
        return !(supportedFlashModes == null || supportedFlashModes.isEmpty()
                || (supportedFlashModes.size() == 1 && supportedFlashModes.get(0)
                .equals(Camera.Parameters.FLASH_MODE_OFF)));
    }

    private int[] getFpsRange(List<int[]> list) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < list.size(); i3++) {
            if (list.get(i3)[1] * list.get(i3)[0] > i2) {
                i2 = list.get(i3)[0] * list.get(i3)[1];
                i = i3;
            }
        }
        return list.get(i);
    }

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size size = null;
        if (height <= width) {
            int i3 = height;
            height = width;
            width = i3;
        }
        for (Camera.Size result : parameters.getSupportedPreviewSizes()) {
            if (result.width > height || result.height > width || (size != null &&
                    result.width * result.height >= size.width * size.height)) {
                result = size;
            }
            size = result;
        }
        return size;
    }

    @Override
    public void onSensorChanged(SensorEvent mSensorEvent) {
        int count = mSensorEvent.sensor.getType() != Sensor.TYPE_LIGHT ? 0 : 1;
        int sensor = this.isSensorLight ? 0 : 1;

        if ((sensor & count) != 0) {
            float maximumRange = mSensorEvent.values[0] / mSensor.getMaximumRange();
            if (((double) maximumRange) < 0.002d) {
                this.mFlashIntensity = 30;
            } else if (((double) maximumRange) < 0.06d) {
                this.mFlashIntensity = 60;
            } else {
                this.mFlashIntensity = 120;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void flashMode() {
        int i = 0;
        if (((this.mFlashIntensity == 30 ? 1 : 0) & (!ae ? 1 : 0)) != 0) {
            ad = false;
            ae = true;
        }
        if ((((!this.isSensorLight) & this.isFlashSupported) & ad)) {
            int i2 = this.mFlashIntensity;
            int size = this.bf.size();
            if (size > 10) {
                i2 = Collections.max(this.bf.subList(size - 10, size));
            }
            i2 = i2 < this.mFlashIntensity ? 1 : 0;

            i = 1;

            if ((i2 & i) != 0) {
                Camera.Parameters parameters = this.mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                this.mCamera.setParameters(parameters);
                this.bf.clear();
                this.isSensorLight = true;
                setExposureLock();
                this.aV = this.ak.size() + 25;
                this.aW = this.aV;
                this.aX = this.aV + 25;
            }
        }
    }

    private void releaseCamera() {
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private void releaseCallback() {
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private void releaseFlash() {
        if (((this.mCamera != null) & this.isFlashSupported) & this.isSensorLight) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.mCamera.setParameters(parameters);
            this.isSensorLight = false;

            this.bf.clear();
        }
    }


    private class CameraPreviewCallBack implements Camera.PreviewCallback {

        private final CameraClass mCameraClass;

        private CameraPreviewCallBack(CameraClass mCameraClass) {
            this.mCameraClass = mCameraClass;
        }

        private boolean check() {
            int value = 1;
            if (this.mCameraClass.aa.size() <= 5) {
                return false;
            }
            double[] doubles = new double[]{
                    (Collections.min(this.mCameraClass.f9377i.subList(
                            this.mCameraClass.aa.size() - 5,
                            this.mCameraClass.aa.size()))),
                    (Collections.min(this.mCameraClass.aa.subList(
                            this.mCameraClass.aa.size() - 5,
                            this.mCameraClass.aa.size()))),
                    (Collections.min(this.mCameraClass.ab.subList(
                            this.mCameraClass.aa.size() - 5,
                            this.mCameraClass.aa.size())))
            };
            int count = (doubles[0] > 0.95d ? 1 : 0) & (!this.mCameraClass.ai ? 1 : 0);
            if (doubles[2] + doubles[1] <= 1.15d) {
                value = 0;
            }
            return count > value;
        }

        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            double[] doubles = mCalculationSupport.calculate(bytes.clone(), mWidth, mHeight);
            this.mCameraClass.ac = doubles[3];
            this.mCameraClass.be = (double) (System.currentTimeMillis() - this.mCameraClass.mStartTime);
            this.mCameraClass.bf.add((int) this.mCameraClass.ac);
            this.mCameraClass.flashMode();
            if (((!this.mCameraClass.ai ? 1 : 0) & (this.mCameraClass.bf.size() > 25 ? 1 : 0)) != 0) {
                this.mCameraClass.releaseFlash();
            }


            if (((!this.mCameraClass.ai) & this.mCameraClass.ad)) {
                this.mCameraClass.f9377i.add(doubles[0]);
                this.mCameraClass.aa.add(doubles[1]);
                this.mCameraClass.ab.add(doubles[2]);
                if (check()) {
                    this.mCameraClass.m15910X();
                }
            }


            if (this.mCameraClass.ai) {
                if (((doubles[0] > 0.95d ? 1 : 0) & (doubles[2] + doubles[1] > 1.15d ? 1 : 0)) == 0) {
                    if (this.mCameraClass.ak.size() > this.mCameraClass.aW) {
                        this.mCameraClass.ab();
                    }
                    if (this.mCameraClass.be > 9000.0d) {
                        if (((this.mCameraClass.aH > 0.01d ? 1 : 0) &
                                ((this.mCameraClass.f9371c < 210 ? 1 : 0) &
                                        (this.mCameraClass.f9371c > 35 ? 1 : 0))) != 0) {

                            //set heart value to text view
                            this.mCameraClass.aa();

                            this.mCameraClass.ae = false;
                            this.mCameraClass.ad = true;
                            this.mCameraClass.mVibrator.vibrate(50);
                        }
                    }
                } else if (this.mCameraClass.be > ((double) this.mCameraClass.mMeasurementTime)) {
                    if (((this.mCameraClass.aH > 0.01d ? 1 : 0) &
                            ((this.mCameraClass.f9371c < 210 ? 1 : 0) &
                                    (this.mCameraClass.f9371c > 35 ? 1 : 0))) != 0) {
                        this.mCameraClass.ab();

                        //set heart value to text view
                        this.mCameraClass.aa();

                        this.mCameraClass.ae = false;
                        this.mCameraClass.ad = true;
                        this.mCameraClass.mVibrator.vibrate(50);
                    }
                }
            }


            if (this.mCameraClass.ai) {
                this.mCameraClass.changeExposureLock();
                int size = this.mCameraClass.aj.size();
                if (size <= 1) {
                    this.mCameraClass.ak.add(this.mCameraClass.ac);
                    this.mCameraClass.aj.add(this.mCameraClass.be);
                } else if (this.mCameraClass.be > this.mCameraClass.aj.get(size - 1)) {
                    this.mCameraClass.ak.add(this.mCameraClass.ac);
                    this.mCameraClass.aj.add(this.mCameraClass.be);
                }
                if (((this.mCameraClass.aH > 0.01d ? 1 : 0) & (((this.mCameraClass.f9371c > 35 ? 1 : 0) &
                        (this.mCameraClass.be > this.mCameraClass.aK ? 1 : 0)) &
                        (this.mCameraClass.f9371c < 210 ? 1 : 0))) != 0) {

                    //set heart value to text view
                    this.mCameraClass.aa();

                    this.mCameraClass.aK = this.mCameraClass.be + 500.0d;
                }
            }
        }
    }

    private void ab() {
        setExposureLock();
        releaseFlash();
        this.ai = false;
        this.f9377i.clear();
        this.aa.clear();
        this.ab.clear();
    }

    private void aa() {
        Log.e("TAG", "Main Result === " + mDecimalFormat.format((long) f9371c));
    }

    private void m15910X() {
        Arrays.fill(this.aU, 0.0d);
        this.mStartTime = System.currentTimeMillis();
        this.be = 0.0d;
        this.ak.clear();
        this.aj.clear();
        this.ai = true;
        this.aK = 1000.0d;
        this.f9376h = 1;
        this.aH = 0.0d;

        this.aV = 25;
        this.aW = this.aV;
        this.aX = this.aV + 25;
    }

    private class RunnableClass implements Runnable {
        private int f9359a;
        private final CameraClass mCameraClass;
        private Thread f9361c;
        private long f9362d = 0;
        private long f9363e = 0;

        RunnableClass(CameraClass mCameraClass) {
            this.mCameraClass = mCameraClass;
        }

        private void m15901a() {
            if (this.f9361c == null) {
                this.f9361c = new Thread(this);
                this.f9361c.setPriority(1);
                this.f9361c.start();
            }
            this.f9359a = 0;
        }

        private void m15902b() {
            if (this.f9361c != null) {
                this.f9361c.interrupt();
                this.f9361c = null;
            }
        }

        @Override
        public void run() {
            /*while (this.f9361c != null) {
                if (this.mCameraClass.ai && this.mCameraClass.ak.size() > this.mCameraClass.aX) {
                    C2593p.m15994a(this.mCameraClass.aj, this.mCameraClass.ak);
                    int length = C2593p.f9423b.length - C2593p.f9422a.length;
                    for (int i = 0; i < C2618b.m16025a(300, length - this.mCameraClass.aX); i++) {
                        this.mCameraClass.aU[299 - i] = -C2593p.f9423b[(length - 1) - i];
                    }
                    double[] a = C2593p.m15995a(this.mCameraClass.aU);
                    this.mCameraClass.f9371c = (int) (a[0] * 60.0d);
                    this.mCameraClass.aH = a[1];

                    if (System.currentTimeMillis() - this.f9362d > 30) {
                        this.f9362d = System.currentTimeMillis();
                        System.arraycopy(this.mCameraClass.aU, 150, this.mCameraClass.aT, 0, 150);
                        this.mCameraClass.aT = C2562a.m15895a(this.mCameraClass.aT);
                    }


                    if (((C2593p.f9423b != null) & this.mCameraClass.f9373d)) {
                        while (this.mCameraClass.f9376h < C2593p.f9423b.length - (C2593p.f9422a.length / 2)) {
                            if (this.mCameraClass.aH > 0.01d) {
                                if (((C2593p.f9423b[this.mCameraClass.f9376h + 1] > 0.0d ? 1 : 0) &
                                        (C2593p.f9423b[this.mCameraClass.f9376h + -1] < 0.0d ? 1 : 0)) != 0 &&
                                        ((double) (System.currentTimeMillis() - this.f9363e)) > (1.0d / a[0]) * 750.0d) {

                                    this.f9363e = System.currentTimeMillis();
                                }
                            }
                            this.mCameraClass.f9376h++;
                        }
                    }


                }
            }*/
        }
    }
}
