package com.vantagecircle.heartrate.data;

import android.content.Context;

import com.vantagecircle.heartrate.model.History;
import com.vantagecircle.heartrate.annotation.ApplicationContext;

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

    public boolean insertHistory(History history) {
        return mDatabaseHelper.insertHistory(history);
    }

    public ArrayList<History> getHistory() {
        return mDatabaseHelper.getHistory();
    }

    public ArrayList<History> getHistory(int limit) {
        return mDatabaseHelper.getHistory(limit);
    }
}
