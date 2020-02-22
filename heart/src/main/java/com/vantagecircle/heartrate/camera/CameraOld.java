package com.vantagecircle.heartrate.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;

import com.vantagecircle.heartrate.processing.ProcessingSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by bapidas on 12/10/17.
 */
@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {
    private Activity mActivity;
    private Camera mCamera;
    private int mCameraId = 0;
    private PowerManager.WakeLock mWakeLock;
    private PreviewListener mPreviewListener;
    private final Object mObject = new Object();
    private SurfaceTexture mSurfaceTexture = null;
    private ProcessingSupport mProcessingSupport;

    public CameraOld(Activity mActivity, ProcessingSupport mProcessingSupport) {
        Log.e("TAG", "CameraOld Run");
        this.mActivity = mActivity;
        PowerManager powerManager = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            this.mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen " + System.currentTimeMillis());
        }
        this.mProcessingSupport = mProcessingSupport;
    }

    @Override
    public CameraSupport open() {
        if (mCamera == null) {
            try {
                this.mWakeLock.acquire(10 * 60 * 1000L);
                this.mCamera = Camera.open(mCameraId);
                setCameraOutputs();
            } catch (Exception e) {
                throw new RuntimeException("Interrupted while trying to open camera.", e);
            }
        }
        return this;
    }

    @Override
    public void close() {
        try {
            if (this.mCamera != null) {
                this.mWakeLock.release();
                this.mCamera.setPreviewCallbackWithBuffer(null);
                this.mCamera.stopPreview();
                this.mCamera.release();
                this.mCamera = null;
                releaseSurface();
            }
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to close camera.", e);
        }
    }

    @Override
    public void addOnPreviewListener(PreviewListener callBack) {
        this.mPreviewListener = callBack;
    }

    @Override
    public boolean isCameraInUse() {
        return mCamera != null;
    }

    private void setCameraOutputs() {
        try {
            if (mCamera == null) {
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);

            int rotation = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, cameraInfo);
            switch (mActivity.getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_90:
                    rotation = 90;
                    break;
                case Surface.ROTATION_180:
                    rotation = 180;
                    break;
                case Surface.ROTATION_270:
                    rotation = 270;
                    break;
                case Surface.ROTATION_0:
                    break;
            }
            this.mCamera.setDisplayOrientation(cameraInfo.facing == 1 ?
                    (360 - ((rotation + cameraInfo.orientation) % 360)) % 360 :
                    ((cameraInfo.orientation - rotation) + 360) % 360);

            Camera.Size size = getSmallestPreviewSize(parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }

            List supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
            if (supportedPreviewFpsRange != null) {
                int fpsRange = supportedPreviewFpsRange.size();
                if (fpsRange > 0) {
                    int[] iArr = (int[]) supportedPreviewFpsRange.get(fpsRange - 1);
                    if (iArr != null && iArr.length > 1) {
                        parameters.setPreviewFpsRange(iArr[0], iArr[1]);
                    }
                }
            }

            List supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes
                    .contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            }

            int bitsPerPixel = ImageFormat.getBitsPerPixel(parameters.getPreviewFormat());
            if (bitsPerPixel % 8 != 0) {
                bitsPerPixel = ((bitsPerPixel / 8) + 1) * 8;
            }
            if (size != null) {
                bitsPerPixel = (bitsPerPixel * (size.width * size.height)) / 8;
            }

            List supportedFlashModes = parameters.getSupportedFlashModes();
            boolean isFlash = !(supportedFlashModes == null || supportedFlashModes.isEmpty()
                    || (supportedFlashModes.size() == 1 && supportedFlashModes.get(0)
                    .equals(Camera.Parameters.FLASH_MODE_OFF)));
            if (isFlash) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }

            this.mCamera.addCallbackBuffer(new byte[bitsPerPixel]);
            this.mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            this.mCamera.setPreviewTexture(createSurfaceTexture());
            this.mCamera.setParameters(parameters);
            this.mCamera.startPreview();
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to setup camera.", e);
        }
    }

    private SurfaceTexture createSurfaceTexture() {
        SurfaceTexture surfaceTexture;
        synchronized (this.mObject) {
            if (this.mSurfaceTexture != null) {
                this.mSurfaceTexture.release();
                this.mSurfaceTexture = null;
            }
            this.mSurfaceTexture = new SurfaceTexture(0);
            surfaceTexture = this.mSurfaceTexture;
        }
        return surfaceTexture;
    }

    private void releaseSurface() {
        synchronized (this.mObject) {
            if (this.mSurfaceTexture != null) {
                try {
                    Method method = this.mSurfaceTexture.getClass().getMethod("release");
                    method.invoke(this.mSurfaceTexture);
                } catch (Throwable th) {
                    throw new RuntimeException("Interrupted while trying " +
                            "to get surface method.", th.getCause());
                } finally {
                    this.mSurfaceTexture = null;
                }
            }
        }
    }

    private Camera.Size getSmallestPreviewSize(Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size != null) {
                int i2 = size.width + size.height;
                if (result != null) {
                    if (i2 < result.width + result.height) {
                        //TODO
                        //No need to any task here
                    }
                }
                result = size;
            }
            size = result;
            result = size;
        }
        return result;
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            //pixel calculation done here
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (data != null && size != null) {
                mPreviewListener.OnPreviewData(data);
                if (mProcessingSupport != null) {
                    int value = mProcessingSupport.YUV420SPtoRedAvg(data.clone(), size.width, size.height);
                    mPreviewListener.OnPreviewCount(value);
                }
                cam.addCallbackBuffer(data);
            }
        }
    };
}
