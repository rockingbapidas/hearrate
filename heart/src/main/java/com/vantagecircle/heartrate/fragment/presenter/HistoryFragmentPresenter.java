package com.vantagecircle.heartrate.fragment.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vantagecircle.heartrate.BR;
import com.vantagecircle.heartrate.adapter.HistoryAdapter;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.History;

import java.util.ArrayList;

/**
 * Created by bapidas on 08/11/17.
 */
@SuppressLint("StaticFieldLeak")
public class HistoryFragmentPresenter extends BaseObservable implements HistoryViewListener {
    private static final String TAG = HistoryFragmentPresenter.class.getSimpleName();
    private DataManager mDataManger;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mHistoryAdapter;

    private HistoryViewListener historyViewListener;
    private boolean isDataAvailable;

    public HistoryFragmentPresenter(Context context, DataManager dataManger) {
        mDataManger = dataManger;
        mContext = context;
        setHistoryViewListener(this);
    }

    @Bindable
    public HistoryViewListener getHistoryViewListener() {
        return historyViewListener;
    }

    private void setHistoryViewListener(HistoryViewListener historyViewListener) {
        this.historyViewListener = historyViewListener;
        notifyPropertyChanged(BR.historyViewListener);
    }

    @Bindable
    public boolean isDataAvailable() {
        return isDataAvailable;
    }

    private void setDataAvailable(boolean dataAvailable) {
        isDataAvailable = dataAvailable;
        notifyPropertyChanged(BR.dataAvailable);
    }

    @BindingAdapter("setAdapter")
    public static void setAdapter(RecyclerView recyclerView, HistoryViewListener historyViewListener) {
        if (recyclerView != null) {
            historyViewListener.getRecyclerView(recyclerView);
        }
    }

    @Override
    public void getRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecyclerView.setLayoutManager(mLinearLayoutManager);
        this.mRecyclerView.scrollToPosition(0);
        this.mHistoryAdapter = new HistoryAdapter();
        mRecyclerView.setAdapter(mHistoryAdapter);
        update();
    }

    public void update() {
        if (mRecyclerView != null && mHistoryAdapter != null) {
            ArrayList<History> mArrayList = mDataManger.getHistory();
            if (mArrayList != null && mArrayList.size() > 0) {
                setDataAvailable(true);
                mHistoryAdapter.bindData(mArrayList);
            } else {
                setDataAvailable(false);
            }
        }
    }
}
