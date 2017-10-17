package com.vantagecircle.heartrate.camera;

import android.hardware.Camera;
import android.os.PowerManager;

import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by bapidas on 17/10/17.
 */
public class CameraOldTest {
    private Camera mCamera;
    private PowerManager.WakeLock mWakeLock;

    @Test
    public void open() throws Exception {
        mWakeLock = mock(PowerManager.WakeLock.class);
        mCamera = mock(Camera.class);
        setCamera(176, 144);
    }

    @Test
    public void close() throws Exception {
        mWakeLock = mock(PowerManager.WakeLock.class);
        mCamera = mock(Camera.class);
        mWakeLock.release();
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void setCamera(int width, int height){
        try {
            mCamera = mock(Camera.class);
            mWakeLock = mock(PowerManager.WakeLock.class);
            if(null == mCamera){
                return;
            }
            Camera.Parameters parameters = mock(Camera.Parameters.class);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
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
}