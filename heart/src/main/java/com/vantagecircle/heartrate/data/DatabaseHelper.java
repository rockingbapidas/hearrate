package com.vantagecircle.heartrate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vantagecircle.heartrate.model.History;
import com.vantagecircle.heartrate.annotation.ApplicationContext;
import com.vantagecircle.heartrate.annotation.DatabaseInfo;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by SiD on 11/12/2017.
 */
@Singleton
public class DatabaseHelper extends SQLiteOpenHelper {

    @Inject
    DatabaseHelper(@ApplicationContext Context context, @DatabaseInfo String name, @DatabaseInfo Integer version) {
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

    ArrayList<History> getHistory() {
        ArrayList<History> arrayList = new ArrayList<>();
        Cursor cursor = null;
        String query = DataModel.GET_HISTORY_QUERY + " ORDER BY " + DataModel.COLUMN_ID + " DESC";
        try {
            cursor = getReadableDatabase().rawQuery(query, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String heartRate = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE));
                    String date = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_DATE_STRING));
                    String time = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STRING));
                    History history = new History(heartRate, date, time);
                    arrayList.add(history);
                    cursor.moveToNext();
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

    ArrayList<History> getHistory(int limit) {
        ArrayList<History> arrayList = new ArrayList<>();
        Cursor cursor = null;
        String query = DataModel.GET_HISTORY_QUERY + " ORDER BY " + DataModel.COLUMN_ID + " DESC " + "LIMIT " + limit;
        try {
            cursor = getReadableDatabase().rawQuery(query, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String heartRate = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE));
                    String date = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_DATE_STRING));
                    String time = cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STRING));
                    History history = new History(heartRate, date, time);
                    arrayList.add(history);
                    cursor.moveToNext();
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

    boolean insertHistory(History history) {
        try {
            ContentValues cValues = new ContentValues();
            cValues.put(DataModel.COLUMN_HEART_RATE, history.getHeartRate());
            cValues.put(DataModel.COLUMN_DATE_STRING, history.getDateString());
            cValues.put(DataModel.COLUMN_TIME_STRING, history.getTimeString());
            return getReadableDatabase().insert(DataModel.HISTORY_TABLE, null, cValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
