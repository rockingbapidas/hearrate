package com.bapidas.heartrate.data.repository

import com.bapidas.heartrate.data.local.HistoryDao
import com.bapidas.heartrate.data.model.HistoryModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override fun getAllHistory(): Flow<List<HistoryModel>> = historyDao.getAllHistory()

    override fun getHistoryWithLimit(limit: Int): Flow<List<HistoryModel>> =
        historyDao.getHistoryWithLimit(limit)

    override suspend fun insertHistory(history: HistoryModel) {
        historyDao.insertHistory(history)
    }
}