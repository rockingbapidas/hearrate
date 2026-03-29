package com.bapidas.heartrate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bapidas.heartrate.data.model.HistoryModel

@Database(entities = [HistoryModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}