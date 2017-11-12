package com.vantagecircle.heartrate.fragment.presenter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.StatusListener;

import butterknife.ButterKnife;

/**
 * Created by bapidas on 08/11/17.
 */

public class HeartFragmentPresenter extends BaseObservable {
    private static final String TAG = HeartFragmentPresenter.class.getSimpleName();
    private HeartSupport mHeartSupport;
    private boolean isStarted;
    private String beatsPerMinute;
    private boolean isFingerDetected;
    private AlertDialog alertDialog;

    public HeartFragmentPresenter(HeartSupport mHeartSupport) {
        this.mHeartSupport = mHeartSupport;
    }

    @Bindable
    public boolean isStarted() {
        return isStarted;
    }

    @Bindable
    public String getBeatsPerMinute() {
        return beatsPerMinute;
    }

    @Bindable
    public boolean isFingerDetected() {
        return isFingerDetected;
    }

    private void setStarted(boolean started) {
        isStarted = started;
        notifyPropertyChanged(BR.started);
    }

    private void setBeatsPerMinute(String beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
        notifyPropertyChanged(BR.beatsPerMinute);
    }

    private void setFingerDetected(boolean fingerDetected) {
        isFingerDetected = fingerDetected;
        notifyPropertyChanged(BR.fingerDetected);
    }

    public void onHelpClick(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.hint_diaog, null);
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
        if (isStarted()) {
            setStarted(false);
            mHeartSupport.stopPulseCheck();
        } else {
            setStarted(true);
            setBeatsPerMinute("-----");
            start();
        }
    }

    private void start() {
        mHeartSupport.startPulseCheck(new PulseListener() {
            @Override
            public void OnPulseDetected(int success) {
                Log.e(TAG, "OnPulseDetected == " + success);
                setFingerDetected(true);
            }

            @Override
            public void OnPulseDetectFailed(int failed) {
                Log.e(TAG, "OnPulseDetectFailed == " + failed);
                setFingerDetected(false);
            }

            @Override
            public void OnPulseResult(String pulse) {
                Log.e(TAG, "OnPulseResult == " + pulse);
                setBeatsPerMinute(pulse);
            }
        });
        mHeartSupport.setOnStatusListener(new StatusListener() {
            @Override
            public void OnCheckStarted() {
                Log.e(TAG, "OnCheckStarted == ");
            }

            @Override
            public void OnCheckRunning() {
                Log.e(TAG, "OnCheckRunning == ");
            }

            @Override
            public void OnCheckStopped() {
                Log.e(TAG, "OnCheckStopped == ");
                setStarted(false);
            }
        });
    }
}
