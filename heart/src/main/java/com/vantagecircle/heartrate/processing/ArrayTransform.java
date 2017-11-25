package com.vantagecircle.heartrate.processing;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.MathArrays;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayTransform {
    public static final double[] samples = new double[]{
            -0.05900213d, -0.05847387d,
            -0.05707053d, -0.05134577d,
            -0.03823765d, -0.01643437d,
             0.01271789d,  0.04515131d,
             0.07495643d,  0.0959668d,
             0.10354379d,  0.0959668d,
             0.07495643d,  0.04515131d,
             0.01271789d, -0.01643437d,
            -0.03823765d, -0.05134577d,
            -0.05707053d, -0.05847387d,
            -0.05900213d
    };
    public static double[] result;

    public static void compare(ArrayList<Double> arrayList, ArrayList<Double> arrayList2) {
        int i;
        int size = arrayList.size();
        double[] dArr = new double[size];
        for (i = 0; i < size; i++) {
            dArr[i] = (arrayList.get(i) - arrayList.get(0)) / 1000.0d;
        }
        double[] a = m15894a(dArr[0], dArr[size - 1], 0.03333333333333333d);
        double[] dArr2 = new double[size];
        for (i = 0; i < size; i++) {
            dArr2[i] = arrayList2.get(i) - arrayList2.get(0);
        }
        result = combine(dArr, dArr2, a);
        ArrayTransform.copy();
    }

    public static double[] sort(double[] dArr) {
        int i;
        double[] b = ArrayTransform.normalize(dArr);
        int length = b.length;
        int i2 = (int) ((1.0d * ((double) length)) / 30.0d);
        int i3 = (int) ((7.0d * ((double) length)) / 30.0d);
        double d = b[i2];
        int i4 = i2;
        for (i = i2; i < i3; i++) {
            if (b[i] > d) {
                d = b[i];
                i4 = i;
            }
        }
        double d2 = 0.0d;
        for (i = i2; i < i3; i++) {
            d2 += b[i];
        }
        return new double[]{(((double) i4) / ((double) length)) * 15.0d, d / d2};
    }

    public static int m15893a(int[] iArr) {
        int i = iArr[0];
        for (int i2 = 1; i2 < iArr.length; i2++) {
            if (iArr[i2] > i) {
                i = iArr[i2];
            }
        }
        return i;
    }

    public static double[] m15895a(double[] dArr) {
        double b = getLength(m15899c(dArr));
        if (b > 0.0d) {
            for (int i = 0; i < dArr.length; i++) {
                dArr[i] = dArr[i] / b;
            }
        }
        return dArr;
    }

    public static int m15898b(int[] iArr) {
        int i = 210;
        for (int i2 = 1; i2 < iArr.length; i2++) {
            if (((iArr[i2] != 0 ? 1 : 0) & (iArr[i2] < i ? 1 : 0)) != 0) {
                i = iArr[i2];
            }
        }
        return i;
    }


    private static void copy() {
        for (int i = 0; i < result.length; i++) {
            double[] dArr = result;
            dArr[i] = dArr[i] - result[0];
        }
        System.arraycopy(MathArrays.convolve(result, samples), (samples.length / 2) - 1, result, 0, result.length);
    }

    private static double[] normalize(double[] dArr) {
        double[] dArr2 = new double[4096];
        System.arraycopy(dArr, 0, dArr2, 0, dArr.length);
        Complex[] a = new FastFourierTransformer(DftNormalization.STANDARD).transform(dArr2, TransformType.FORWARD);
        double[] dArr3 = new double[2048];
        int i = (int) ((3.5d * ((double) 4096)) / 30.0d);
        for (int i2 = (int) ((0.5d * ((double) 4096)) / 30.0d); i2 < i; i2++) {
            dArr3[i2] = a[i2].abs();
        }
        return dArr3;
    }

    private static double[] combine(double[] dArr, double[] dArr2, double[] dArr3) {
        if (dArr.length != dArr2.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        } else if (dArr.length == 1) {
            return dArr2;
        } else {
            double[] dArr4 = new double[(dArr.length - 1)];
            double[] dArr5 = new double[(dArr.length - 1)];
            double[] dArr6 = new double[(dArr.length - 1)];
            double[] dArr7 = new double[(dArr.length - 1)];
            int length = dArr.length;
            int i = 0;
            while (i < length - 1) {
                dArr4[i] = dArr[i + 1] - dArr[i];
                if (dArr4[i] == 0.0d) {
                    throw new IllegalArgumentException("X must be montotonic. A duplicate x-value was found");
                } else if (dArr4[i] < 0.0d) {
                    throw new IllegalArgumentException("X must be sorted");
                } else {
                    dArr5[i] = dArr2[i + 1] - dArr2[i];
                    dArr6[i] = dArr5[i] / dArr4[i];
                    dArr7[i] = dArr2[i] - (dArr[i] * dArr6[i]);
                    i++;
                }
            }
            double[] dArr8 = new double[dArr3.length];
            int length2 = dArr3.length;
            int i2 = 0;
            while (i2 < length2) {
                if (dArr3[i2] > dArr[dArr.length - 1] || dArr3[i2] < dArr[0]) {
                    dArr8[i2] = Double.NaN;
                } else {
                    length = Arrays.binarySearch(dArr, dArr3[i2]);
                    if (length < -1) {
                        length = (-length) - 2;
                        dArr8[i2] = (dArr6[length] * dArr3[i2]) + dArr7[length];
                    } else {
                        dArr8[i2] = dArr2[length];
                    }
                }
                i2++;
            }
            dArr8[dArr8.length - 1] = dArr2[dArr2.length - 1];
            return dArr8;
        }
    }

    private static double getLength(double[] dArr) {
        int length = dArr.length - 150;
        double d = dArr[length];
        for (length++; length < dArr.length; length++) {
            if (dArr[length] > d) {
                d = dArr[length];
            }
        }
        return d;
    }

    private static double[] m15899c(double[] dArr) {
        double[] dArr2 = new double[dArr.length];
        for (int i = 0; i < dArr.length; i++) {
            if (dArr[i] > 0.0d) {
                dArr2[i] = dArr[i];
            } else {
                dArr2[i] = -dArr[i];
            }
        }
        return dArr2;
    }

    private static double[] m15894a(double d, double d2, double d3) {
        int i = 0;
        int i2 = (int) ((d2 - d) / d3);
        if (i2 > 1) {
            double[] dArr = new double[i2];
            while (i < i2) {
                dArr[i] = (((double) i) * d3) + d;
                i++;
            }
            return dArr;
        }
        return new double[]{d};
    }
}
