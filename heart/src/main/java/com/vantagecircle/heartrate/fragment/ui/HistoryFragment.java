package com.vantagecircle.heartrate.fragment.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vantagecircle.heartrate.R;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.fragment.BaseFragment;
import com.vantagecircle.heartrate.fragment.component.DaggerHistoryFragmentComponent;
import com.vantagecircle.heartrate.fragment.component.HistoryFragmentComponent;
import com.vantagecircle.heartrate.fragment.module.HistoryFragmentModule;
import com.vantagecircle.heartrate.fragment.presenter.HistoryFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private Unbinder mUnBinder;
    private HistoryFragmentComponent mHistoryFragmentComponent;
    private HistoryFragmentPresenter mHistoryFragmentPresenter;

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
        if (mHistoryFragmentComponent == null) {
            mHistoryFragmentComponent = DaggerHistoryFragmentComponent.builder()
                    .historyFragmentModule(new HistoryFragmentModule(this))
                    .heartActivityComponent(((HeartActivity)getActivity()).getHeartActivityComponent())
                    .build();
        }
        mHistoryFragmentComponent.inject(this);
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
        mHistoryFragmentPresenter = new HistoryFragmentPresenter(null,
                this.getActivity().getApplicationContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }
}
