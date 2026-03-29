package com.bapidas.heartrate.data.repository

import com.bapidas.heartrate.data.local.HistoryDao
import com.bapidas.heartrate.data.model.HistoryModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class HistoryRepositoryImplTest {

    @Mock
    private lateinit var historyDao: HistoryDao

    private lateinit var repository: HistoryRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = HistoryRepositoryImpl(historyDao)
    }

    @Test
    fun `getAllHistory calls dao and returns flow`() = runBlocking {
        val historyList = listOf(HistoryModel(1, "75", "01/01/2021", "12:00 PM"))
        `when`(historyDao.getAllHistory()).thenReturn(flowOf(historyList))

        repository.getAllHistory().collect {
            assertEquals(historyList, it)
        }
        verify(historyDao).getAllHistory()
    }

    @Test
    fun `getHistoryWithLimit calls dao and returns flow`() = runBlocking {
        val limit = 5
        val historyList = listOf(HistoryModel(1, "75", "01/01/2021", "12:00 PM"))
        `when`(historyDao.getHistoryWithLimit(limit)).thenReturn(flowOf(historyList))

        repository.getHistoryWithLimit(limit).collect {
            assertEquals(historyList, it)
        }
        verify(historyDao).getHistoryWithLimit(limit)
    }

    @Test
    fun `insertHistory calls dao`() = runBlocking {
        val history = HistoryModel(1, "75", "01/01/2021", "12:00 PM")
        repository.insertHistory(history)
        verify(historyDao).insertHistory(history)
    }
}
