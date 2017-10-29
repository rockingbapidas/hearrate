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
    private final Object object = new Object();
    private SurfaceTexture surfaceTexture = null;
    private ProcessingSupport processingSupport;

    public CameraOld(Context mContext, ProcessingSupport processingSupport) {
        Log.e("TAG", "CameraOld Run");
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            this.mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }
        this.processingSupport = processingSupport;
    }

    @Override
    public CameraSupport open() {
        if (this.mCamera == null) {
            try {
                this.mWakeLock.acquire(10 * 60 * 1000L);
                this.mCamera = Camera.open();
                setCamera();
            } catch (Exception e) {
                e.printStackTrace();
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
                synchronized (this.object) {
                    if (this.surfaceTexture != null) {
                        try {
                            Method method = this.surfaceTexture.getClass()
                                    .getMethod("release");
                            if (method != null) {
                                method.invoke(this.surfaceTexture);
                            }
                        } catch (Throwable th) {
                            Log.e("TAG", th.getMessage());
                        }
                        this.surfaceTexture = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPreviewCallBack(CameraCallBack callBack) {
        this.mCameraCallBack = callBack;
    }

    @Override
    public boolean isCameraInUse() {
        return mCamera != null;
    }

    private void setCamera() {
        try {
            if (this.mCamera == null) {
                return;
            }
            Camera.Parameters parameters = this.mCamera.getParameters();

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
            e.printStackTrace();
        }
    }

    private SurfaceTexture surfaceTexture() {
        SurfaceTexture surfaceTexture;
        synchronized (this.object) {
            if (this.surfaceTexture != null) {
                this.surfaceTexture.release();
                this.surfaceTexture = null;
            }
            this.surfaceTexture = new SurfaceTexture(0);
            surfaceTexture = this.surfaceTexture;
        }
        return surfaceTexture;
    }

    private Camera.Size getSmallestPreviewSize(Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size != null) {
                int i2 = size.width + size.height;
                if (result != null) {
                    if (i2 < result.width + result.height) {
                        //TODO
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

            int value = processingSupport.YUV420SPtoRedAvg(data, size.width, size.height);
            mCameraCallBack.onFrameCallback(value);

            cam.addCallbackBuffer(data);
        }
    };
}
