package com.vantagecircle.heartrate.fragment.presenter;

import android.view.SurfaceView;
import android.widget.ProgressBar;

/**
 * Created by SiD on 12/3/2017.
 */

public interface HeartViewListener {
    void getProgressView(ProgressBar mProgressBar);
    void getSurfaceView(SurfaceView mSurfaceView);
}
