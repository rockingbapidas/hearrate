package com.vantagecircle.heartrate.processing;

import android.graphics.Color;

/**
 * Created by bapidas on 18/11/17.
 */

public class RGBCalculation {
    public static double[] calculate(byte[] data, int width, int height) {
        if (data == null) {
            return new double[3];
        }
        double[] b = redAverage(data, width, height);
        int[] a = redSum((int) b[0], (int) b[1], (int) b[2]);
        float[] r2 = new float[3];
        Color.RGBToHSV(a[0], a[1], a[2], r2);
        return new double[]{
                Math.cos(((double) (r2[0] / 180.0f)) * 3.141592653589793d),
                (double) r2[1], (double) r2[2], b[0]
        };
    }

    private static double[] redAverage(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            return new double[2];
        }
        int i3 = i * i2;
        int[] iArr = new int[]{0, 0, 0};
        int i4 = 0;
        int i5 = 0;
        while (i5 < i2) {
            int i6 = i4;
            int i7 = 0;
            int i8 = 0;
            int i9 = i3 + ((i5 >> 1) * i);
            for (i4 = 0; i4 < i; i4 += 5) {
                int i10 = (bArr[(i5 * i) + i4] & 255) - 16;
                if (i10 < 0) {
                    i10 = 0;
                }
                if ((i4 & 1) == 0) {
                    i8 = i9 + 1;
                    i7 = (bArr[i9] & 255) - 128;
                    i9 = i8 + 1;
                    i8 = (bArr[i8] & 255) - 128;
                }
                iArr[0] = i10 + iArr[0];
                iArr[1] = iArr[1] + i8;
                iArr[2] = iArr[2] + i7;
                i6++;
            }
            i5 += 5;
            i4 = i6;
        }
        return new double[]{
                ((double) iArr[0]) / ((double) i4),
                ((double) iArr[1]) / ((double) i4),
                ((double) iArr[2]) / ((double) i4)
        };
    }

    private static int[] redSum(int i, int i2, int i3) {
        int i4 = 262143;
        int[] iArr = new int[3];
        int i5 = i * 1192;
        int i6 = i5 + (i3 * 1634);
        int i7 = (i5 - (i3 * 833)) - (i2 * 400);
        i5 += i2 * 2066;
        if (i6 < 0) {
            i6 = 0;
        } else if (i6 > 262143) {
            i6 = 262143;
        }
        if (i7 < 0) {
            i7 = 0;
        } else if (i7 > 262143) {
            i7 = 262143;
        }
        if (i5 < 0) {
            i4 = 0;
        } else if (i5 <= 262143) {
            i4 = i5;
        }
        iArr[0] = (i6 >> 10) & 255;
        iArr[1] = (i7 >> 10) & 255;
        iArr[2] = (i4 >> 10) & 255;
        return iArr;
    }
}
