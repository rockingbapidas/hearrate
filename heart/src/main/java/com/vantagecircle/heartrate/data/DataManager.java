package com.vantagecircle.heartrate.data;

import android.content.Context;

import com.vantagecircle.heartrate.model.HistoryModel;
import com.vantagecircle.heartrate.scope.ApplicationContext;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by SiD on 11/11/2017.
 */
@Singleton
public class DataManager {
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    @Inject
    public DataManager(@ApplicationContext Context mContext, DatabaseHelper mDatabaseHelper) {
        this.mContext = mContext;
        this.mDatabaseHelper = mDatabaseHelper;
    }

    public boolean insertHistory(HistoryModel historyModel) {
        return mDatabaseHelper.insertHistory(historyModel);
    }

    public ArrayList<HistoryModel> getHistory() {
        return mDatabaseHelper.getHistory();
    }

    public ArrayList<HistoryModel> getHistory(int limit) {
        return mDatabaseHelper.getHistory(limit);
    }
}
