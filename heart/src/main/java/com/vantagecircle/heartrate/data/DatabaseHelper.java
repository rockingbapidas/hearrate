package com.vantagecircle.heartrate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vantagecircle.heartrate.scope.ApplicationContext;
import com.vantagecircle.heartrate.scope.DatabaseInfo;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by SiD on 11/12/2017.
 */
@Singleton
public class DatabaseHelper extends SQLiteOpenHelper {

    @Inject
    public DatabaseHelper(@ApplicationContext Context context, @DatabaseInfo String name, @DatabaseInfo Integer version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataModel.CREATE_HEART_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataModel.HISTORY_TABLE);
        onCreate(db);
    }

    protected ArrayList<HistoryModel> getHistory() {
        ArrayList<HistoryModel> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(DataModel.GET_HISTORY_QUERY, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_ID));
                    String heartRate = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE));
                    String timeStamp = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STAMP));
                    HistoryModel historyModel = new HistoryModel(id, heartRate, Long.parseLong(timeStamp));
                    arrayList.add(historyModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }

    protected ArrayList<HistoryModel> getHistory(int limit) {
        ArrayList<HistoryModel> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(DataModel.GET_HISTORY_QUERY, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_ID));
                    String heartRate = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE));
                    String timeStamp = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STAMP));
                    HistoryModel historyModel = new HistoryModel(id, heartRate, Long.parseLong(timeStamp));
                    arrayList.add(historyModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }

    protected boolean insertHistory(HistoryModel historyModel) {
        try {
            ContentValues cValues = new ContentValues();
            cValues.put(DataModel.COLUMN_HEART_RATE, historyModel.getHeartRate());
            cValues.put(DataModel.COLUMN_TIME_STAMP, historyModel.getTimeStamp());
            return getReadableDatabase().insert(DataModel.HISTORY_TABLE, null, cValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
