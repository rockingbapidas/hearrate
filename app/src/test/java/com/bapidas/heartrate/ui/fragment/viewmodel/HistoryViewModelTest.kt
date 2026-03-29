package com.bapidas.heartrate.ui.fragment.viewmodel

import app.cash.turbine.test
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HistoryViewModelTest {

    @Mock
    private lateinit var repository: HistoryRepository

    private lateinit var viewModel: HistoryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        val historyList = listOf(HistoryModel(1, "75", "01/01/2021", "12:00 PM"))
        `when`(repository.getAllHistory()).thenReturn(flowOf(historyList))
        
        viewModel = HistoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `history state flow emits values from repository`() = runTest {
        viewModel.history.test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("75", item[0].heartRate)
        }
    }
}
