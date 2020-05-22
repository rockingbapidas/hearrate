package com.bapidas.heartrate.data

import com.bapidas.heartrate.data.model.HistoryModel
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by SiD on 11/11/2017.
 */
@Singleton
class DataManager @Inject constructor(
    private val mDatabaseHelper: DatabaseHelper
) {
    fun insertHistory(historyModel: HistoryModel): Boolean {
        return mDatabaseHelper.insertHistory(historyModel)
    }

    val history: ArrayList<HistoryModel>
        get() = mDatabaseHelper.history

    fun getHistory(limit: Int): ArrayList<HistoryModel> {
        return mDatabaseHelper.getHistory(limit)
    }
}