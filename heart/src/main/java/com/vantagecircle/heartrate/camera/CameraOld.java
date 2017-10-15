package com.vantagecircle.heartrate.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by bapidas on 12/10/17.
 */
@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {
    private final String TAG = CameraOld.class.getSimpleName();
    private Camera camera;
    private PowerManager.WakeLock wakeLock;
    private CameraCallBack cameraCallBack;

    public CameraOld(Context mContext) {
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    @Override
    public CameraSupport open() {
        wakeLock.acquire();
        this.camera = Camera.open();
        setCamera(480, 320);
        return this;
    }

    @Override
    public void close() {
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void setPreviewCallBack(CameraCallBack callBack) {
        this.cameraCallBack = callBack;
    }

    private void setCamera(int width, int height){
        try {
            if(null == camera){
                return;
            }
            camera.setPreviewCallback(mPreviewCallback);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width = " + size.width + " height = " + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception e){
            e.printStackTrace();
        }
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

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();
            cameraCallBack.onFrameCallback(data, size.width, size.height);
        }
    };
}
