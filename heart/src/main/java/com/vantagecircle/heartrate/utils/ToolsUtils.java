package com.vantagecircle.heartrate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

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

    /*
    private void createGraph() {
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
    }*/
}
