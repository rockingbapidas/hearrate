package com.bapidas.heartrate.data.repository

import com.bapidas.heartrate.data.model.HistoryModel
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryModel>>
    fun getHistoryWithLimit(limit: Int): Flow<List<HistoryModel>>
    suspend fun insertHistory(history: HistoryModel)
}