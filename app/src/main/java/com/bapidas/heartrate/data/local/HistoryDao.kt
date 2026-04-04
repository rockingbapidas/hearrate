package com.bapidas.heartrate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bapidas.heartrate.data.model.HistoryModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_table ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryModel>>

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT :limit")
    fun getHistoryWithLimit(limit: Int): Flow<List<HistoryModel>>

    @Insert
    suspend fun insertHistory(history: HistoryModel): Long
}