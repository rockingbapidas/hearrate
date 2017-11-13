package com.vantagecircle.heartrate.fragment.presenter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.TimerListener;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.data.HistoryModel;
import com.vantagecircle.heartrate.scope.ActivityContext;
import com.vantagecircle.heartrate.scope.ApplicationContext;

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

    //bind data fields
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

    public void onHelpClick(View view) {
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

    public void onStartClick(View view) {
        if (isStarted) {
            isStarted = false;
            setBeatsPerMinute("000");
            setProgress(0);
            mHeartSupport.stopPulseCheck();
        } else {
            start();
        }
    }

    private void start() {
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
                Long p = n * 100 / timeLimit;
                int progress = p.intValue();
                setProgress(progress);
            }

            @Override
            public void OnTimerStopped() {
                isStarted = false;
                Log.e(TAG, "Final heart rate == " + getBeatsPerMinute());
            }
        }).startPulseCheck(timeLimit);
    }

    private void showFinalBpm() {
        mDataManager.insertHistory(new HistoryModel(getBeatsPerMinute(), "", ""));
    }
}
