package com.vantagecircle.heartrate.fragment.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.DaggerFragmentComponent;
import com.vantagecircle.heartrate.core.HeartRate;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.databinding.HeartRateLayoutBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.component.FragmentComponent;
import com.vantagecircle.heartrate.module.FragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;

import javax.inject.Inject;

public class HeartFragment extends BaseFragment {
    private static final String TAG = HeartFragment.class.getSimpleName();

    @Inject
    HeartRate heartRate;
    @Inject
    DataManager mDataManager;

    private FragmentComponent mFragmentComponent;
    private HeartRateLayoutBinding mHeartRateLayoutBinding;

    public static HeartFragment newInstance() {
        return new HeartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeartRateLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.heart_rate_layout,
                container, false);
        return mHeartRateLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected void setFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .fragmentModule(new FragmentModule(this))
                    .appComponent(HeartApplication.get(this.getContext()).getAppComponent())
                    .build();
        }
        mFragmentComponent.inject(this);
    }

    @Override
    protected void init() {
        HeartFragmentPresenter mHeartFragmentPresenter = new HeartFragmentPresenter(this.getActivity(),
                heartRate.getHeartSupport(), mDataManager);
        mHeartRateLayoutBinding.setHeartPresenter(mHeartFragmentPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
