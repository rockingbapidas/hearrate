package com.bapidas.heartrate.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.data.repository.HistoryRepository
import com.bapidas.heartrate.ui.fragment.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = mock(HistoryRepository::class.java)
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        // Initial empty state
        `when`(repository.getAllHistory()).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun historyScreen_emptyList_showsNoHistoryText() {
        viewModel = HistoryViewModel(repository)
        
        composeTestRule.setContent {
            HistoryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("No history available").assertIsDisplayed()
    }

    @Test
    fun historyScreen_withData_showsItems() {
        val historyList = listOf(
            HistoryModel(1, "75", "01/01/2021", "12:00 PM"),
            HistoryModel(2, "85", "02/01/2021", "01:00 PM")
        )
        `when`(repository.getAllHistory()).thenReturn(flowOf(historyList))
        viewModel = HistoryViewModel(repository)

        composeTestRule.setContent {
            HistoryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("75").assertIsDisplayed()
        composeTestRule.onNodeWithText("85").assertIsDisplayed()
        composeTestRule.onNodeWithText("01/01/2021").assertIsDisplayed()
        composeTestRule.onNodeWithText("12:00 PM").assertIsDisplayed()
    }
}
