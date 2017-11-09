package com.vantagecircle.heartrate.fragment.presenter;

import android.util.Log;

import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.StatusListener;
import com.vantagecircle.heartrate.data.HeartM;
import com.vantagecircle.heartrate.fragment.ui.HeartFragment;

/**
 * Created by bapidas on 08/11/17.
 */

public class HeartFragmentPresenter {
    private static final String TAG = HeartFragmentPresenter.class.getSimpleName();
    private HeartSupport heartSupport;
    private HeartFragment heartFragment;

    private HeartM heartM;
    private long timeLimit = 20000;

    public HeartFragmentPresenter(HeartSupport heartSupport, HeartFragment heartFragment) {
        this.heartSupport = heartSupport;
        this.heartFragment = heartFragment;
    }

    public void handleClick() {
        if (heartM != null && heartM.isStarted()) {
            heartSupport.stopPulseCheck();
            heartM.setStarted(false);
        } else {
            if (heartM != null) {
                heartM.setStarted(true);
                heartM.setBeatsPerMinuteValue("-----");
            } else {
                heartM = new HeartM();
                heartM.setStarted(true);
                heartM.setBeatsPerMinuteValue("-----");
            }
            start();
        }
    }

    private void start() {
        heartSupport.startPulseCheck(new PulseListener() {
            @Override
            public void OnPulseDetected(int success) {
                Log.e(TAG, "OnPulseDetected == " + success);

                heartM.setDetectHeartRate(true);
            }

            @Override
            public void OnPulseDetectFailed(int failed) {
                Log.e(TAG, "OnPulseDetectFailed == " + failed);

                heartM.setDetectHeartRate(false);
            }

            @Override
            public void OnPulseResult(String pulse) {
                Log.e(TAG, "OnPulseResult == " + pulse);

                heartM.setBeatsPerMinuteValue(pulse);
            }
        }).setOnStatusListener(new StatusListener() {
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

                heartM.setStarted(false);
            }
        });
    }
}
