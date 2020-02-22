package com.vantagecircle.heartrate.fragment.presenter;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.TimerListener;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.HistoryModel;
import com.vantagecircle.heartrate.utils.ToolsUtils;

/**
 * Created by bapidas on 08/11/17.
 */

public class HeartFragmentPresenter extends BaseObservable {
    private static final String TAG = HeartFragmentPresenter.class.getSimpleName();
    private HeartSupport mHeartSupport;
    private DataManager mDataManager;
    private Context mContext;
    private AlertDialog alertDialog;
    private boolean isStarted;
    private long timeLimit = 20000;
    private long interval = 500;

    //android data binding fields
    private String beatsPerMinute;
    private int progress;

    public HeartFragmentPresenter(Context mContext, HeartSupport mHeartSupport, DataManager mDataManager) {
        this.mHeartSupport = mHeartSupport;
        this.mDataManager = mDataManager;
        this.mContext = mContext;
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
    public int getProgress() {
        return progress;
    }

    private void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @BindingAdapter("updateProgress")
    public static void setProgressAnimation(ProgressBar mProgressBar, int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", progress);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    public void onHelpClick(View view) {
        showHintDialog();
    }

    public void onStartClick(View view) {
        start();
    }

    private void start(){
        if (isStarted) {
            isStarted = false;
            mHeartSupport.stopPulseCheck();
        } else {
            initHeart();
        }
    }

    private void showHintDialog(){
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.hint_diaog, null);
            Button btnOk = mView.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            dialog.setView(mView);
            alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void showSuccessDialog() {
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.result_success_dialog, null);
            TextView textView = mView.findViewById(R.id.txtheart);
            textView.setText(getBeatsPerMinute());
            Button btnCancel = mView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    setBeatsPerMinute("000");
                    setProgress(0);
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
            alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void showErrorDialog() {
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            View mView = LayoutInflater.from(mContext).inflate(R.layout.result_error_dialog, null);
            Button btnCancel = mView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    setBeatsPerMinute("000");
                    setProgress(0);
                }
            });
            Button btn_try = mView.findViewById(R.id.btn_try);
            btn_try.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    start();
                }
            });
            dialog.setView(mView);
            alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void saveHeartRate() {
        long timeStamp = System.currentTimeMillis();
        String date = ToolsUtils.getInstance().getDate(timeStamp);
        String time = ToolsUtils.getInstance().getTime(timeStamp);
        if (mDataManager.insertHistory(new HistoryModel(getBeatsPerMinute(), date, time))) {
            Toast.makeText(mContext, "Heart rate saved", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
            setBeatsPerMinute("000");
            setProgress(0);
        } else {
            Toast.makeText(mContext, "Heart rate not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void initHeart() {
        mHeartSupport.addOnResultListener(new PulseListener() {
            @Override
            public void OnPulseResult(String pulse) {
                if (pulse.length() == 2) {
                    setBeatsPerMinute("0" + pulse);
                } else {
                    setBeatsPerMinute(pulse);
                }
            }
        }).addOnTimerListener(new TimerListener() {
            @Override
            public void OnTimerStarted() {
                isStarted = true;
                setBeatsPerMinute("000");
                setProgress(0);
            }

            @Override
            public void OnTimerRunning(long milliSecond) {
                long n = timeLimit - milliSecond;
                int progress = (int) (n * 100 / timeLimit);
                setProgress(progress);
            }

            @Override
            public void OnTimerStopped() {
                setProgress(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isStarted = false;
                        int value = Integer.parseInt(getBeatsPerMinute());
                        if (value == 0 || value < 50) {
                            showErrorDialog();
                        } else {
                            showSuccessDialog();
                        }
                    }
                }, 800);
            }
        }).startPulseCheck(timeLimit);
    }
}
