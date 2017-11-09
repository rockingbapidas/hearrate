package com.vantagecircle.heartrate.fragment.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.fragment.BaseFragment;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private View mView;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.history_layout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void init() {

    }
}
