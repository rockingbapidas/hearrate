package com.vantagecircle.heartrate.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.PowerManager;
import android.util.Log;

import com.vantagecircle.heartrate.processing.ProcessingSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by bapidas on 12/10/17.
 */
@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {
    private Camera mCamera;
    private PowerManager.WakeLock mWakeLock;
    private CameraCallBack mCameraCallBack;
    private final Object mObject = new Object();
    private SurfaceTexture mSurfaceTexture = null;
    private ProcessingSupport mProcessingSupport;

    public CameraOld(Context mContext, ProcessingSupport mProcessingSupport) {
        Log.e("TAG", "CameraOld Run");
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            this.mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }
        this.mProcessingSupport = mProcessingSupport;
    }

    @Override
    public CameraSupport open() {
        if (mCamera == null) {
            try {
                this.mWakeLock.acquire(10 * 60 * 1000L);
                this.mCamera = Camera.open();
                setCamera();
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
    public void setOnPreviewListener(CameraCallBack callBack) {
        this.mCameraCallBack = callBack;
    }

    @Override
    public boolean isCameraInUse() {
        return mCamera != null;
    }

    private void setCamera() {
        try {
            if (mCamera == null) {
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();

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

            parameters.setPreviewFormat(ImageFormat.NV21);

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
            this.mCamera.addCallbackBuffer(new byte[bitsPerPixel]);
            this.mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
            this.mCamera.setPreviewTexture(surfaceTexture());
            this.mCamera.setParameters(parameters);
            this.mCamera.startPreview();

            List supportedFlashModes = parameters.getSupportedFlashModes();
            boolean isFlash = !(supportedFlashModes == null || supportedFlashModes.isEmpty()
                    || (supportedFlashModes.size() == 1 && supportedFlashModes.get(0)
                    .equals(Camera.Parameters.FLASH_MODE_OFF)));
            if (isFlash) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            this.mCamera.setParameters(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to setup camera.", e);
        }
    }

    private SurfaceTexture surfaceTexture() {
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
                    if (method != null) {
                        method.invoke(this.mSurfaceTexture);
                    }
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
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();
            int value = mProcessingSupport.YUV420SPtoRedAvg(data, size.width, size.height);
            mCameraCallBack.OnPixelAverage(value);
            cam.addCallbackBuffer(data);
        }
    };
}
