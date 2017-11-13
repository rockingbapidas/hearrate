package com.vantagecircle.heartrate.fragment.presenter;

import android.content.Context;

import com.vantagecircle.heartrate.data.DataManager;

/**
 * Created by bapidas on 08/11/17.
 */

public class HistoryFragmentPresenter {
    private DataManager mDataManger;
    private Context mContext;

    public HistoryFragmentPresenter(DataManager mDataManger, Context mContext) {
        this.mDataManger = mDataManger;
        this.mContext = mContext;
    }
}
