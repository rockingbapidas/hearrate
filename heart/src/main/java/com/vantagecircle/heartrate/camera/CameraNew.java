package com.vantagecircle.heartrate.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

/**
 * Created by bapidas on 12/10/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraNew implements CameraSupport {
    private Context mContext;
    private CameraDevice camera;
    private CameraManager manager;

    public CameraNew(Context context) {
        this.mContext = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public CameraSupport open(int cameraId) {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                String[] cameraIds = manager.getCameraIdList();
                manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        CameraNew.this.camera = camera;
                        // TODO handle
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        CameraNew.this.camera = camera;
                        // TODO handle
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        CameraNew.this.camera = camera;
                        // TODO handle
                    }
                }, null);
            }
        } catch (Exception e) {
            // TODO handle
        }
        return this;
    }

    @Override
    public int getOrientation(int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager
                    .getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics
                    .SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            // TODO handle
            return 0;
        }
    }
}
