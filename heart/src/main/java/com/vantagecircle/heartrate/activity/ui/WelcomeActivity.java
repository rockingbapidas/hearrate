package com.vantagecircle.heartrate.activity.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;
import com.github.mikephil.charting.utils.Utils;
import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.BaseActivity;
import com.vantagecircle.heartrate.activity.handlers.WelcomeEventHandlers;
import com.vantagecircle.heartrate.activity.module.WelcomeActivityModule;
import com.vantagecircle.heartrate.activity.presenter.WelcomeActivityPresenter;
import com.vantagecircle.heartrate.databinding.WelcomeActivityBinding;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by bapidas on 09/10/17.
 */

public class WelcomeActivity extends BaseActivity {
    private final String TAG = WelcomeActivity.class.getSimpleName();
    @Inject
    WelcomeActivityPresenter welcomeActivityPresenter;

    private LineChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WelcomeActivityBinding mBinding = DataBindingUtil.setContentView(this, R.layout.welcome_activity);
        mBinding.setWelcomeHandlers(new WelcomeEventHandlers(welcomeActivityPresenter));

        /*setContentView(R.layout.heart_rate_layout);

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setData(generateLineData());
        mChart.animateX(3000);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

        Typeface tf = Typeface.createFromAsset(getAssets(),"OpenSans-Light.ttf");
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setAxisMaximum(1.2f);
        leftAxis.setAxisMinimum(-1.2f);
        mChart.getAxisRight().setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);*/
    }

    private LineData generateLineData() {
        Typeface tf = Typeface.createFromAsset(getAssets(),"OpenSans-Light.ttf");
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();

        LineDataSet ds2 = new LineDataSet(FileUtils
                .loadEntriesFromAssets(getAssets(), "cosine.txt"),
                "Cosine function");

        ds2.setLineWidth(3f);
        ds2.setDrawCircles(false);
        ds2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);


        sets.add(ds2);
        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }


    @Override
    protected void setupActivityComponent() {
        Log.d(TAG, "setupActivityComponent");
        HeartApplication.get(this)
                .getAppComponent()
                .plus(new WelcomeActivityModule(this))
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
