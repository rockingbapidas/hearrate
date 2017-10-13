package com.vantagecircle.heartrate.activity.presenter;

import android.hardware.Camera;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.mock;

/**
 * Created by bapidas on 12/10/17.
 */
public class HeartActivityPresenterTest {
    private final AtomicBoolean processing = new AtomicBoolean(false);

    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];

    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private double beats = 0;

    private long startTime = 0;
    private enum TYPE {
        GREEN, RED, BLUE
    }
    private TYPE currentType = TYPE.GREEN;

    @Test
    public void start() throws Exception {
        Camera camera = mock(Camera.class);
        startTime = System.currentTimeMillis();
        Camera.Parameters parameters = mock(Camera.Parameters.class);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        Camera.Size size = getSmallestPreviewSize(176, 144, parameters);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
        }
        camera.setParameters(parameters);
        camera.startPreview();
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

    @Test
    public void stop() throws Exception {
        Camera camera = mock(Camera.class);
        camera.stopPreview();
        camera.release();
    }

    @Test
    public void calculate(){
        if (!processing.compareAndSet(false, true))
            return;
        int imgAvg = 200;
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false);
            return;
        }
        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int anAverageArray : averageArray) {
            if (anAverageArray > 0) {
                averageArrayAvg += anAverageArray;
                averageArrayCnt++;
            }
        }
        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
        TYPE newType = currentType;
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = imgAvg;
        averageIndex++;

        if (newType != currentType) {
            currentType = newType;
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;

        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis();
                beats = 0;
                processing.set(false);
                return;
            }

            if (beatsIndex == beatsArraySize)
                beatsIndex = 0;
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;

            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for (int aBeatsArray : beatsArray) {
                if (aBeatsArray > 0) {
                    beatsArrayAvg += aBeatsArray;
                    beatsArrayCnt++;
                }
            }
            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            String beatsPerMinuteValue = String.valueOf(beatsAvg);
            System.out.println(beatsPerMinuteValue);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }
}