package com.vantagecircle.heartrate.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by bapidas on 22/11/17.
 */

public class CameraManager implements CameraSupport {
    private static final String TAG = CameraManager.class.getSimpleName();
    private Activity mActivity;
    private Camera mCamera;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private SurfaceHolder mSurfaceHolder;

    private int mCameraId = 0;
    private int mWidth;
    private int mHeight;
    private int mLightIntensity;

    private boolean isFlashEnabled;
    private boolean isAutoExposureLockEnabled;
    private boolean isFlashSupported;
    private boolean isAutoExposureLockSupported;

    private PreviewListener mPreviewListener;

    public CameraManager(Activity mActivity) {
        this.mActivity = mActivity;
        this.mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (this.mSensorManager != null) {
            this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }

    @Override
    public void openCamera() {
        try {
            this.mCamera = Camera.open(this.mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepareCamera() {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (parameters.getSupportedPreviewFpsRange() != null) {
            int[] fpsRange = getAccurateFps(parameters.getSupportedPreviewFpsRange());
            parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
        }
        this.mCamera.setParameters(parameters);

        if (parameters.getFlashMode() != null) {
            List supportedFlashModes = parameters.getSupportedFlashModes();
            this.isFlashSupported = !(supportedFlashModes == null || supportedFlashModes.isEmpty() || (supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)));
        }
        this.isAutoExposureLockSupported = parameters.isAutoExposureLockSupported();
        this.isFlashEnabled = false;
        this.disableAutoExposureLock();
        this.mLightIntensity = 50;
        if (this.mSensor != null && this.mSensorEventListener != null) {
            this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void setPreviewHolder(SurfaceHolder mSurfaceHolder) {
        if (this.mCamera == null) {
            return;
        }
        this.mSurfaceHolder = mSurfaceHolder;
        try {
            this.mCamera.setPreviewDisplay(this.mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mCamera.setPreviewCallback(this.mPreviewCallback);
    }

    @Override
    public void preparePreview(int mWidth, int mHeight) {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        Camera.Size a = this.getSmallestPreviewSize(mWidth, mHeight, parameters);
        if (a != null) {
            parameters.setPreviewSize(a.width, a.height);
        }
        this.mCamera.setParameters(parameters);

        int rotation = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(this.mCameraId, cameraInfo);
        switch (this.mActivity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }
        this.mCamera.setDisplayOrientation(cameraInfo.facing == 1 ?
                (360 - ((rotation + cameraInfo.orientation) % 360)) % 360 :
                ((cameraInfo.orientation - rotation) + 360) % 360);

        this.mWidth = parameters.getPreviewSize().width;
        this.mHeight = parameters.getPreviewSize().height;
    }

    @Override
    public void startPreview() {
        if (this.mCamera == null) {
            return;
        }
        this.mCamera.startPreview();
    }

    @Override
    public void stopPreview() {
        if (this.mCamera == null) {
            return;
        }
        this.mCamera.stopPreview();
    }

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size size = null;
        if (height <= width) {
            int i3 = height;
            height = width;
            width = i3;
        }
        for (Camera.Size result : parameters.getSupportedPreviewSizes()) {
            if (result.width > height || result.height > width || (size != null && result.width * result.height >= size.width * size.height)) {
                result = size;
            }
            size = result;
        }
        return size;
    }

    private int[] getAccurateFps(List<int[]> list) {
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

    @Override
    public void releaseCamera() {
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.mSensorManager.unregisterListener(this.mSensorEventListener);
        }
    }

    @Override
    public void releaseCallbacks() {
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    @Override
    public boolean isAutoExposureLockSupported() {
        return this.isAutoExposureLockSupported;
    }

    @Override
    public boolean isAutoExposureLockEnabled() {
        return this.isAutoExposureLockEnabled;
    }

    @Override
    public void enableAutoExposureLock() {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (!this.isAutoExposureLockEnabled & this.isAutoExposureLockSupported) {
            parameters.setAutoExposureLock(true);
            this.isAutoExposureLockEnabled = true;
        }
        this.mCamera.setParameters(parameters);
    }

    @Override
    public void disableAutoExposureLock() {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (this.isAutoExposureLockSupported) {
            parameters.setAutoExposureLock(false);
            this.isAutoExposureLockEnabled = false;
        }
        this.mCamera.setParameters(parameters);
    }

    @Override
    public boolean isFlashSupported() {
        return this.isFlashSupported;
    }

    @Override
    public boolean isFlashEnabled() {
        return this.isFlashEnabled;
    }

    @Override
    public void enableFlash() {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (!this.isFlashEnabled & this.isFlashSupported) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            this.isFlashEnabled = true;
        }
        this.mCamera.setParameters(parameters);
    }

    @Override
    public void disableFlash() {
        if (this.mCamera == null) {
            return;
        }
        Camera.Parameters parameters = this.mCamera.getParameters();
        if (this.isFlashSupported & this.isFlashEnabled) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.isFlashEnabled = false;
        }
        this.mCamera.setParameters(parameters);
    }

    @Override
    public int getLightIntensity() {
        return this.mLightIntensity;
    }

    @Override
    public void addOnPreviewListener(PreviewListener mPreviewListener) {
        this.mPreviewListener = mPreviewListener;
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            if (mPreviewListener != null) {
                mPreviewListener.OnPreviewCallback(bytes, mWidth, mHeight);
            }
        }
    };

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent mSensorEvent) {
            if (!isFlashEnabled & mSensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                float maximumRange = mSensorEvent.values[0] / mSensor.getMaximumRange();
                if (((double) maximumRange) < 0.002d) {
                    mLightIntensity = 30;
                } else if (((double) maximumRange) < 0.06d) {
                    mLightIntensity = 60;
                } else {
                    mLightIntensity = 120;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}
