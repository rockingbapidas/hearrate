package com.vantagecircle.heartrate.fragment.presenter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vantagecircle.heartrate.adapter.HistoryAdapter;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.HistoryModel;

import java.util.ArrayList;

/**
 * Created by bapidas on 08/11/17.
 */

public class HistoryFragmentPresenter {
    private static final String TAG = HistoryFragmentPresenter.class.getSimpleName();
    private DataManager mDataManger;
    private Context mContext;
    private RecyclerView mRecyclerView;

    public HistoryFragmentPresenter(Context mContext, DataManager mDataManger, RecyclerView mRecyclerView) {
        this.mDataManger = mDataManger;
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
    }

    public void initialize() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.scrollToPosition(0);
        ArrayList<HistoryModel> mArrayList = mDataManger.getHistory();
        if (mArrayList != null && mArrayList.size() > 0) {
            HistoryAdapter mHistoryAdapter = new HistoryAdapter(mArrayList);
            mRecyclerView.setAdapter(mHistoryAdapter);
        }
    }
}
