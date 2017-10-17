package com.vantagecircle.heartrate.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.PowerManager;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Created by bapidas on 17/10/17.
 */
public class CameraOldTest {

    @Mock
    private Camera mCamera;
    @Mock
    private PowerManager.WakeLock mWakeLock;

    @Mock
    private Context mContext;

    @Test
    public void open() throws Exception {
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        mWakeLock.acquire();
        this.mCamera = Camera.open();
        setCamera(176, 144);
    }

    @Test
    public void close() throws Exception {
        mWakeLock.release();
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void setCamera(int width, int height){
        try {
            if(null == mCamera){
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();
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