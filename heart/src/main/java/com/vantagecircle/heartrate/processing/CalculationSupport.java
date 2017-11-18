package com.vantagecircle.heartrate.processing;

/**
 * Created by bapidas on 18/11/17.
 */

public interface CalculationSupport {
    double[] calculate(byte[] data, int width, int height);
}
