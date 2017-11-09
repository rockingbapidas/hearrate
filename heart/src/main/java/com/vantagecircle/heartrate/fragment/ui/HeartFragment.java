package com.vantagecircle.heartrate.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;

import javax.inject.Inject;

public class HeartFragment extends BaseFragment {
    private static final String TAG = HeartFragment.class.getSimpleName();
    @Inject
    HeartFragmentPresenter heartFragmentPresenter;
    private View mView;
    private Context mContext;

    public static HeartFragment newInstance() {
        return new HeartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.heart_rate_layout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setupActivityComponent() {
        HeartApplication.get(mContext).getUserComponent()
                .plus(new HeartFragmentModule(this))
                .inject(this);
    }

    @Override
    protected void init() {

    }
}
