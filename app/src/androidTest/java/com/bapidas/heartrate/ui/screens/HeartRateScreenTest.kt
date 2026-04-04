package com.bapidas.heartrate.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.bapidas.heartrate.core.HeartSupport
import com.bapidas.heartrate.data.repository.HistoryRepository
import com.bapidas.heartrate.ui.fragment.viewmodel.HeartViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class HeartRateScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = mock(HistoryRepository::class.java)
    private val heartSupport = mock(HeartSupport::class.java)
    private lateinit var viewModel: HeartViewModel

    @Before
    fun setUp() {
        `when`(heartSupport.addOnResultListener(any())).thenReturn(heartSupport)
        `when`(heartSupport.addOnTimerListener(any())).thenReturn(heartSupport)
        viewModel = HeartViewModel(repository, heartSupport)
    }

    @Test
    fun heartRateScreen_initialState_showsStartButton() {
        composeTestRule.setContent {
            HeartRateScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("START").assertIsDisplayed()
        composeTestRule.onNodeWithText("000").assertIsDisplayed()
    }

    @Test
    fun heartRateScreen_clickStart_callsViewModel() {
        composeTestRule.setContent {
            HeartRateScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("START").performClick()
        
        // Verify startMeasurement was called (which calls heartSupport.startPulseCheck)
        verify(heartSupport).startPulseCheck(any())
    }

    @Test
    fun heartRateScreen_clickHowItWorks_showsDialog() {
        composeTestRule.setContent {
            HeartRateScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("HOW IT WORKS").performClick()
        
        composeTestRule.onNodeWithText("How it works").assertIsDisplayed()
        composeTestRule.onNodeWithText("Place your finger on the camera lens and flash to measure your heart rate.").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithText("How it works").assertDoesNotExist()
    }
}
