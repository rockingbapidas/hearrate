package com.vantagecircle.heartrate.camera;

import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;

/**
 * Created by SiD on 10/15/2017.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ImageSaver implements Runnable {
    private final Image mImage;
    private CameraCallBack cameraCallBack;

    public ImageSaver(Image image, CameraCallBack cameraCallBack) {
        this.mImage = image;
        this.cameraCallBack = cameraCallBack;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        cameraCallBack.onFrameCallback(bytes, mImage.getWidth(), mImage.getHeight());
    }
}
