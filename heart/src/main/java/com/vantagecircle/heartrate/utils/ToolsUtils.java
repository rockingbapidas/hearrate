package com.vantagecircle.heartrate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by bapidas on 10/07/17.
 */

public class ToolsUtils {
    private static ToolsUtils mInstance;

    public static ToolsUtils getInstance(){
        if (mInstance == null) {
            synchronized (ToolsUtils.class) {
                if (mInstance == null) {
                    mInstance = new ToolsUtils();
                }
            }
        }
        return mInstance;
    }

    public boolean isHasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /*private void createGraph() {
        LineChart mChart = heartActivity.findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setData(generateLineData());
        mChart.animateX(3000);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

        Typeface tf = Typeface.createFromAsset(heartActivity.getAssets(),
                "OpenSans-Light.ttf");
        Legend l = mChart.getLegend();
        l.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setAxisMaximum(1.2f);
        leftAxis.setAxisMinimum(-1.2f);

        mChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
    }

    private LineData generateLineData() {
        Typeface tf = Typeface.createFromAsset(heartActivity.getAssets(),
                "OpenSans-Light.ttf");
        ArrayList<ILineDataSet> sets = new ArrayList<>();

        LineDataSet ds = new LineDataSet(FileUtils
                .loadEntriesFromAssets(heartActivity.getAssets(), "cosine.txt"),
                "Cosine function");

        ds.setLineWidth(3f);
        ds.setDrawCircles(false);
        ds.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);

        sets.add(ds);
        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    private void calculatePulse(int pixelAverage) {
        if (!processing.compareAndSet(false, true))
            return;

        if (pixelAverage == 0 || pixelAverage >= 255) {
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
        if (pixelAverage < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
            }
        } else if (pixelAverage > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = pixelAverage;
        averageIndex++;

        if (newType != currentType) {
            currentType = newType;
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;

        if (totalTimeInSecs >= 5) {
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

            if (pulseListener != null) {
                pulseListener.OnPulseResult(beatsPerMinuteValue);
            }
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }*/
}
