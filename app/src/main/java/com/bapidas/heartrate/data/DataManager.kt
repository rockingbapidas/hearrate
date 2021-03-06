package com.bapidas.heartrate.data

import com.bapidas.heartrate.data.model.HistoryModel
import java.util.*

/**
 * Created by SiD on 11/11/2017.
 */
class DataManager constructor(
    private val mDatabaseHelper: DatabaseHelper
) {
    fun insertHistory(historyModel: HistoryModel): Boolean =
        mDatabaseHelper.insertHistory(historyModel)

    val history: ArrayList<HistoryModel>
        get() = mDatabaseHelper.history

    fun getHistory(limit: Int): ArrayList<HistoryModel> =
        mDatabaseHelper.getHistory(limit)
}