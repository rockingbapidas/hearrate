package com.bapidas.heartrate.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.di.annotation.ApplicationContext
import com.bapidas.heartrate.di.annotation.DatabaseInfo
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by SiD on 11/12/2017.
 */
@Singleton
class DatabaseHelper @Inject internal constructor(
    @ApplicationContext context: Context,
    @DatabaseInfo name: String,
    @DatabaseInfo version: Int
) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DataModel.CREATE_HEART_TABLE_QUERY)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS " + DataModel.HISTORY_TABLE)
        onCreate(db)
    }

    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT)
    val history: ArrayList<HistoryModel>
        get() {
            val arrayList = ArrayList<HistoryModel>()
            val query =
                DataModel.GET_HISTORY_QUERY + " ORDER BY " + DataModel.COLUMN_ID + " DESC"
            try {
                readableDatabase.rawQuery(query, null).use { cursor ->
                    if (cursor != null) {
                        cursor.moveToFirst()
                        while (!cursor.isAfterLast) {
                            val heartRate =
                                cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE))
                            val date =
                                cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_DATE_STRING))
                            val time =
                                cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STRING))
                            val historyModel = HistoryModel(heartRate, date, time)
                            arrayList.add(historyModel)
                            cursor.moveToNext()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return arrayList
        }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getHistory(limit: Int): ArrayList<HistoryModel> {
        val arrayList = ArrayList<HistoryModel>()
        val query =
            DataModel.GET_HISTORY_QUERY + " ORDER BY " + DataModel.COLUMN_ID + " DESC " + "LIMIT " + limit
        try {
            readableDatabase.rawQuery(query, null).use { cursor ->
                if (cursor != null) {
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        val heartRate =
                            cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_HEART_RATE))
                        val date =
                            cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_DATE_STRING))
                        val time =
                            cursor.getString(cursor.getColumnIndex(DataModel.COLUMN_TIME_STRING))
                        val historyModel = HistoryModel(heartRate, date, time)
                        arrayList.add(historyModel)
                        cursor.moveToNext()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayList
    }

    fun insertHistory(historyModel: HistoryModel): Boolean {
        try {
            val cValues = ContentValues()
            cValues.put(DataModel.COLUMN_HEART_RATE, historyModel.heartRate)
            cValues.put(DataModel.COLUMN_DATE_STRING, historyModel.dateString)
            cValues.put(DataModel.COLUMN_TIME_STRING, historyModel.timeString)
            return readableDatabase.insert(DataModel.HISTORY_TABLE, null, cValues) > 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}