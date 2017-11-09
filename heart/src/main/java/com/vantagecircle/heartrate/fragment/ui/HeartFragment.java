package com.vantagecircle.heartrate.fragment.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.core.HeartSupport;
import com.vantagecircle.heartrate.core.PulseListener;
import com.vantagecircle.heartrate.core.StatusListener;
import com.vantagecircle.heartrate.databinding.HeartRateBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.component.DaggerHeartFragmentComponent;
import com.vantagecircle.heartrate.fragment.component.HeartFragmentComponent;
import com.vantagecircle.heartrate.fragment.handlers.HeartFragmentHandlers;
import com.vantagecircle.heartrate.fragment.module.HeartFragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;

import javax.inject.Inject;

public class HeartFragment extends BaseFragment {
    private static final String TAG = HeartFragment.class.getSimpleName();
    private HeartFragmentPresenter heartFragmentPresenter;
    private HeartFragmentComponent heartFragmentComponent;
    public HeartRateBinding mHeartRateBinding;

    @Inject
    HeartRate heartRate;

    public static HeartFragment newInstance() {
        return new HeartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        heartFragmentComponent = DaggerHeartFragmentComponent.builder()
                .heartFragmentModule(new HeartFragmentModule(this)).build();
        heartFragmentComponent.inject(this);

        heartFragmentPresenter = new HeartFragmentPresenter(heartRate.getHeartSupport());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeartRateBinding = DataBindingUtil.inflate(inflater, R.layout.heart_rate_layout,
                container, false);
        mHeartRateBinding.setHeartFragmentHandler(
                new HeartFragmentHandlers(heartFragmentPresenter));
        return mHeartRateBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /*protected HeartFragmentComponent setupActivityComponent() {
        if (heartFragmentComponent == null) {
            heartFragmentComponent = DaggerHeartFragmentComponent.builder()
                    .heartFragmentModule(new HeartFragmentModule(this))
                    .appComponent(HeartApplication.get(this).getAppComponent())
                    .build();
        }
        return heartFragmentComponent;
    }*/

    @Override
    protected void init() {
        //initializing code here if needed
    }
}
