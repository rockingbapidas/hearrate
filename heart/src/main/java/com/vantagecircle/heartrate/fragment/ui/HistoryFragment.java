package com.vantagecircle.heartrate.fragment.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.DaggerFragmentComponent;
import com.vantagecircle.heartrate.component.FragmentComponent;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.databinding.HistoryLayoutBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.presenter.HistoryFragmentPresenter;
import com.vantagecircle.heartrate.module.FragmentModule;

import javax.inject.Inject;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    @Inject
    DataManager mDataManager;
    private FragmentComponent mFragmentComponent;
    private HistoryFragmentPresenter mHistoryFragmentPresenter;
    private HistoryLayoutBinding mHistoryLayoutBinding;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHistoryLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.history_layout, container, false);
        return mHistoryLayoutBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mHistoryFragmentPresenter = new HistoryFragmentPresenter(this.getActivity(), mDataManager);
        mHistoryLayoutBinding.setHistoryPresenter(mHistoryFragmentPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        HistoryFragmentPresenter.initialize();
    }
}
