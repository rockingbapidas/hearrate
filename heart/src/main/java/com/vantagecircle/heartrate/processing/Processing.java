package com.vantagecircle.heartrate.processing;

import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.vantagecircle.heartrate.core.HeartRate;

import java.nio.ByteBuffer;

public class Processing implements ProcessingSupport {
    private static final String TAG = Processing.class.getSimpleName();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }

    @Override
    public int YUV420SPtoRedAvg(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) return 0;
        final int frameSize = width * height;
        int sum = YUV420SPtoRedSum(yuv420sp, width, height);
        //Log.e(TAG, "Red Avg " + sum / frameSize);
        return (sum / frameSize);
    }

    private int YUV420SPtoRedSum(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) return 0;
        final int frameSize = width * height;
        int sum = 0;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & yuv420sp[yp]) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                int red = (pixel >> 16) & 0xff;
                sum += red;
            }
        }
        //Log.e(TAG, "Red sum " + sum);
        return sum;
    }
}
