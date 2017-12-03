package com.vantagecircle.heartrate.fragment.presenter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.PulseSupport;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.Heart;
import com.vantagecircle.heartrate.model.History;
import com.vantagecircle.heartrate.utils.ToolsUtils;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by bapidas on 08/11/17.
 */

public class HeartFragmentPresenter extends BaseObservable {
    private static final String TAG = HeartFragmentPresenter.class.getSimpleName();
    private static PulseSupport mPulseSupport;
    private DataManager mDataManager;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private int mMeasurementTime = 15;
    private Vibrator mVibrate;
    private int healthyValue;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    private static ObjectAnimator mObjectAnimator;

    //android data binding fields
    private String beatsPerMinute;
    private int heartColor;
    private boolean isStarted;

    public HeartFragmentPresenter(Context mContext, PulseSupport mPulseSupport, DataManager mDataManager) {
        HeartFragmentPresenter.mPulseSupport = mPulseSupport;
        this.mDataManager = mDataManager;
        this.mContext = mContext;
        this.mVibrate = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Bindable
    public String getBeatsPerMinute() {
        return beatsPerMinute;
    }

    private void setBeatsPerMinute(String beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        notifyPropertyChanged(BR.beatsPerMinute);
    }

    @Bindable
    public int getHeartColor() {
        return heartColor;
    }

    private void setHeartColor(int heartColor) {
        this.heartColor = heartColor;
        notifyPropertyChanged(BR.heartColor);
    }

    @Bindable
    public boolean isStarted() {
        return isStarted;
    }

    private void setStarted(boolean started) {
        isStarted = started;
        notifyPropertyChanged(BR.started);
    }

    @BindingAdapter("setProgressAnimation")
    public static void setProgressAnimation(ProgressBar mProgressBar, boolean aBoolean) {
        HeartFragmentPresenter.mProgressBar = mProgressBar;
        mObjectAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 1, 200);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
    }

    @BindingAdapter("setSurfaceHolder")
    public static void setSurfaceHolder(SurfaceView mSurfaceView, boolean aBoolean) {
        mPulseSupport.setSurface(mSurfaceView.getHolder());
    }

    public void onStartClick(View view) {
        if (isStarted) {
            stop();
        } else {
            start();
            setStarted(true);
        }
    }

    private void start() {
        mPulseSupport.setMeasurementTime(mMeasurementTime).startMeasure().addOnPulseListener(new PulseListener() {
            @Override
            public void OnPulseCheckStarted() {
                resume();
                mVibrate.vibrate(50);
                mObjectAnimator.setValues(PropertyValuesHolder.ofInt("progress", 0, 200));
                mObjectAnimator.setDuration((long) mMeasurementTime * 1000);
                mObjectAnimator.start();
            }

            @Override
            public void OnPulseCheckStopped() {
                if (mProgressBar.getProgress() < 200) {
                    int progress = mProgressBar.getProgress();
                    mObjectAnimator.setValues(PropertyValuesHolder.ofInt("progress", progress, 0));
                    mObjectAnimator.setDuration(500);
                    mObjectAnimator.start();
                }
            }

            @Override
            public void OnPulseCheckFinished(String mPulseRate, boolean isComplete) {
                mVibrate.vibrate(50);
                if (mPulseRate.length() > 2) {
                    setBeatsPerMinute(mPulseRate);
                } else {
                    setBeatsPerMinute("0" + mPulseRate);
                }
                showSuccessDialog();
                stop();
            }

            @Override
            public void OnPulseCheckRate(String mPulseRate) {
                heartStatus(Integer.parseInt(mPulseRate));
                if (mPulseRate.length() > 2) {
                    setBeatsPerMinute(mPulseRate);
                } else {
                    setBeatsPerMinute("0" + mPulseRate);
                }
            }

            @Override
            public void OnPulseCheckError() {
                mVibrate.vibrate(50);
                showErrorDialog();
                stop();
            }
        });
    }

    public void stop() {
        mPulseSupport.stopMeasure();
        setStarted(false);
    }

    public void initialize(Heart mHeart) {
        double year = (double) mHeart.getBirthYear();
        if (mHeart.getGender().equals("female")) {
            this.healthyValue = (int) (207.2d - ((Calendar.YEAR - year) * 0.65d));
        } else {
            this.healthyValue = (int) (209.6d - ((Calendar.YEAR - year) * 0.72d));
        }
    }

    private void heartStatus(int mPulseRate){
        int i = 1;
        if (((double) mPulseRate) <= ((double) healthyValue) * 0.55d) {
            setHeartColor(Color.rgb(40, 180, 40));
            return;
        }
        int i2 = ((double) mPulseRate) > ((double) healthyValue) * 0.55d ? 1 : 0;
        if (((double) mPulseRate) > ((double) healthyValue) * 0.75d) {
            i = 0;
        }
        if ((i2 & i) != 0) {
            setHeartColor(Color.rgb(250, 230, 80));
        } else if (((double) mPulseRate) > ((double) healthyValue) * 0.75d) {
            setHeartColor(Color.rgb(195, 25, 25));
        }
    }

    private void saveHeartRate() {
        long timeStamp = System.currentTimeMillis();
        String date = ToolsUtils.getInstance().getDate(timeStamp);
        String time = ToolsUtils.getInstance().getTime(timeStamp);
        if (mDataManager.insertHistory(new History(getBeatsPerMinute(), date, time))) {
            resume();
            mAlertDialog.dismiss();
        } else {
            Toast.makeText(mContext, "Heart rate not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void resume() {
        setBeatsPerMinute("000");
        setHeartColor(ContextCompat.getColor(mContext, R.color.white));
        mProgressBar.setProgress(0);
    }

    private void showSuccessDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.result_success_dialog, null);
            ImageView imageView = mView.findViewById(R.id.heartStatus);
            imageView.setColorFilter(getHeartColor());
            TextView textView = mView.findViewById(R.id.txtheart);
            textView.setText(getBeatsPerMinute());
            Button btnCancel = mView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resume();
                    mAlertDialog.dismiss();
                }
            });

            Button btnSave = mView.findViewById(R.id.btn_save);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveHeartRate();
                }
            });
            dialog.setCancelable(false);
            dialog.setView(mView);
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }

    private void showErrorDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.result_error_dialog, null);
            Button btnCancel = mView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resume();
                    mAlertDialog.dismiss();
                }
            });
            Button btn_try = mView.findViewById(R.id.btn_try);
            btn_try.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resume();
                    start();
                    mAlertDialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.setView(mView);
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }
}
