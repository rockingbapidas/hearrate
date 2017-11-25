package com.vantagecircle.heartrate.fragment.presenter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.PulseSupport;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.HistoryModel;
import com.vantagecircle.heartrate.utils.ToolsUtils;

/**
 * Created by bapidas on 08/11/17.
 */

public class HeartFragmentPresenter extends BaseObservable {
    private static final String TAG = HeartFragmentPresenter.class.getSimpleName();
    private PulseSupport mPulseSupport;
    private DataManager mDataManager;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private int mMeasurementTime = 15;
    private Vibrator mVibrate;

    private static ProgressBar mProgressBar;
    private static ObjectAnimator mObjectAnimator;

    //android data binding fields
    private String beatsPerMinute;
    private int heartColor;

    public HeartFragmentPresenter(Context mContext, PulseSupport mPulseSupport, DataManager mDataManager) {
        this.mPulseSupport = mPulseSupport;
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

    @BindingAdapter("setProgressAnimation")
    public static void setProgressAnimation(ProgressBar mProgressBar, boolean bind) {
        HeartFragmentPresenter.mProgressBar = mProgressBar;
        mObjectAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 1, 200);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
    }

    public void start(SurfaceHolder mSurfaceHolder) {
        mPulseSupport.setSurfaceHolder(mSurfaceHolder)
                .setMeasurementTime(mMeasurementTime)
                .startMeasure()
                .addOnPulseListener(new PulseListener() {
                    @Override
                    public void OnPulseCheckStarted() {
                        setBeatsPerMinute("000");
                        setHeartColor(ContextCompat.getColor(mContext, R.color.orange));
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
                        if (mPulseRate.length() > 2) {
                            setBeatsPerMinute(mPulseRate);
                        } else {
                            setBeatsPerMinute("0" + mPulseRate);
                        }
                        mVibrate.vibrate(50);
                        if (Integer.parseInt(mPulseRate) == 0) {
                            showErrorDialog();
                        } else {
                            showSuccessDialog();
                        }
                    }

                    @Override
                    public void OnPulseCheckRate(String mPulseRate) {
                        if (mPulseRate.length() > 2) {
                            setBeatsPerMinute(mPulseRate);
                        } else {
                            setBeatsPerMinute("0" + mPulseRate);
                        }
                        setHeartColor(ContextCompat.getColor(mContext, R.color.red));
                    }
                });
    }

    public void stop() {
        mPulseSupport.stopMeasure();
    }


    public void onHelpClick(SurfaceView view) {
        start(view.getHolder());
        //showHintDialog();
    }

    private void showHintDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.hint_diaog, null);
            Button btnOk = mView.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            dialog.setView(mView);
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }

    private void showSuccessDialog() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.result_success_dialog, null);
            TextView textView = mView.findViewById(R.id.txtheart);
            textView.setText(getBeatsPerMinute());
            Button btnCancel = mView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                    resume();
                }
            });

            Button btnSave = mView.findViewById(R.id.btn_save);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveHeartRate();
                }
            });
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
                    mAlertDialog.dismiss();
                    resume();
                }
            });
            Button btn_try = mView.findViewById(R.id.btn_try);
            btn_try.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                    resume();
                }
            });
            dialog.setView(mView);
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }

    private void saveHeartRate() {
        long timeStamp = System.currentTimeMillis();
        String date = ToolsUtils.getInstance().getDate(timeStamp);
        String time = ToolsUtils.getInstance().getTime(timeStamp);
        if (mDataManager.insertHistory(new HistoryModel(getBeatsPerMinute(), date, time))) {
            Toast.makeText(mContext, "Heart rate saved", Toast.LENGTH_SHORT).show();
            mAlertDialog.dismiss();
            resume();
        } else {
            Toast.makeText(mContext, "Heart rate not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void resume() {
        setBeatsPerMinute("000");
        setHeartColor(ContextCompat.getColor(mContext, R.color.orange));
        mProgressBar.setProgress(0);
        mPulseSupport.resumeMeasure();
    }
}
