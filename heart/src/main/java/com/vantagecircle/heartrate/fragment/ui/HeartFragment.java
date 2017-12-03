package com.vantagecircle.heartrate.fragment.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.DaggerFragmentComponent;
import com.vantagecircle.heartrate.component.FragmentComponent;
import com.vantagecircle.heartrate.core.PulseSupport;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.databinding.HeartRateLayoutBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.presenter.HeartFragmentPresenter;
import com.vantagecircle.heartrate.model.Heart;
import com.vantagecircle.heartrate.module.FragmentModule;

import javax.inject.Inject;

public class HeartFragment extends BaseFragment {
    private static final String TAG = HeartFragment.class.getSimpleName();
    @Inject
    PulseSupport mPulseSupport;
    @Inject
    DataManager mDataManager;
    @Inject
    Heart mHeart;

    private FragmentComponent mFragmentComponent;
    private HeartRateLayoutBinding mHeartRateLayoutBinding;
    private HeartFragmentPresenter mHeartFragmentPresenter;

    public static HeartFragment newInstance() {
        return new HeartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .fragmentModule(new FragmentModule(this))
                    .heartComponent(HeartApplication.get(this.getContext()).getHeartComponent())
                    .build();
        }
        mFragmentComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        mHeartRateLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.heart_rate_layout, container, false);
        return mHeartRateLayoutBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mHeartFragmentPresenter = new HeartFragmentPresenter(this.getActivity(), mPulseSupport, mDataManager);
        mHeartFragmentPresenter.initialize(mHeart);
        mHeartRateLayoutBinding.setHeartPresenter(mHeartFragmentPresenter);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        if (mHeartFragmentPresenter != null)
            mHeartFragmentPresenter.stop();
    }
}
