package com.vantagecircle.heartrate.fragment.presenter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vantagecircle.heartrate.adapter.HistoryAdapter;
import com.vantagecircle.heartrate.data.DataManager;
import com.vantagecircle.heartrate.model.HistoryModel;

import java.util.ArrayList;

/**
 * Created by bapidas on 08/11/17.
 */

public class HistoryFragmentPresenter {
    private DataManager mDataManger;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<HistoryModel> mArrayList;
    private HistoryAdapter mHistoryAdapter;

    public HistoryFragmentPresenter(Context mContext, DataManager mDataManger, RecyclerView mRecyclerView) {
        this.mDataManger = mDataManger;
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
    }

    public void initialize() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mArrayList = mDataManger.getHistory();
        if (mArrayList != null && mArrayList.size() > 0) {
            mHistoryAdapter = new HistoryAdapter(mArrayList);
            mRecyclerView.setAdapter(mHistoryAdapter);
        }
    }
}
