package com.vantagecircle.heartrate.fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.component.DaggerFragmentComponent;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.databinding.HistoryLayoutBinding;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.component.FragmentComponent;
import com.vantagecircle.heartrate.module.FragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HistoryFragmentPresenter;

import javax.inject.Inject;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    public HistoryLayoutBinding mUnBinder;
    private FragmentComponent mFragmentComponent;
    private HistoryFragmentPresenter mHistoryFragmentPresenter;
    @Inject
    DataManager mDataManager;

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
        mUnBinder = DataBindingUtil.inflate(inflater, R.layout.history_layout, container, false);
        return mUnBinder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mHistoryFragmentPresenter = new HistoryFragmentPresenter(
                this.getActivity(), mDataManager, mUnBinder.recyclerView);
        mHistoryFragmentPresenter.initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHistoryFragmentPresenter != null) {
            mHistoryFragmentPresenter.initialize();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
