package com.vantagecircle.heartrate.fragment.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.DaggerFragmentComponent;
import com.vantagecircle.heartrate.fragment.FragmentComponent;
import com.vantagecircle.heartrate.fragment.FragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HistoryFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private Unbinder mUnBinder;
    private FragmentComponent mFragmentComponent;
    private HistoryFragmentPresenter mHistoryFragmentPresenter;
    @Inject
    DataManager mDataManager;
    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

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
        View mView = inflater.inflate(R.layout.history_layout, container, false);
        mUnBinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mHistoryFragmentPresenter = new HistoryFragmentPresenter(
                this.getActivity(), mDataManager, mRecyclerView);
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
        mUnBinder.unbind();
    }
}
