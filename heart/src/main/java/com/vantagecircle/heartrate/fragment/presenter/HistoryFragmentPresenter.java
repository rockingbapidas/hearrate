package com.vantagecircle.heartrate.fragment.presenter;

import android.content.Context;
import android.databinding.BindingAdapter;
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
    private static final String TAG = HistoryFragmentPresenter.class.getSimpleName();
    private static DataManager mDataManger;
    private static Context mContext;
    private static RecyclerView mRecyclerView;

    public HistoryFragmentPresenter(Context context, DataManager dataManger) {
        mDataManger = dataManger;
        mContext = context;
    }

    @BindingAdapter("setAdapter")
    public static void setAdapter(RecyclerView recyclerView, boolean aBoolean) {
        mRecyclerView = recyclerView;
        initialize();
    }

    public static void initialize() {
        if (mRecyclerView != null) {
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
}
