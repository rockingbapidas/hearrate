package com.bapidas.heartrate.ui.fragment.viewmodel

import app.cash.turbine.test
import com.bapidas.heartrate.core.HeartSupport
import com.bapidas.heartrate.core.PulseListener
import com.bapidas.heartrate.core.TimerListener
import com.bapidas.heartrate.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times

@ExperimentalCoroutinesApi
class HeartViewModelTest {

    @Mock
    private lateinit var repository: HistoryRepository
    @Mock
    private lateinit var heartSupport: HeartSupport

    private lateinit var viewModel: HeartViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(heartSupport.addOnResultListener(any())).thenReturn(heartSupport)
        `when`(heartSupport.addOnTimerListener(any())).thenReturn(heartSupport)
        
        viewModel = HeartViewModel(repository, heartSupport)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `beatsPerMinute initial value is 000`() = runTest {
        assertEquals("000", viewModel.beatsPerMinute.value)
    }

    @Test
    fun `startMeasurement calls heartSupport startPulseCheck when not started`() = runTest {
        viewModel.startMeasurement()
        verify(heartSupport).startPulseCheck(anyLong())
        assertEquals(true, viewModel.isStarted.value)
    }

    @Test
    fun `startMeasurement stops pulse check when already started`() = runTest {
        // First start
        viewModel.startMeasurement()
        assertEquals(true, viewModel.isStarted.value)
        
        // Then toggle off
        viewModel.startMeasurement()
        verify(heartSupport).stopPulseCheck()
        assertEquals(false, viewModel.isStarted.value)
    }

    @Test
    fun `onPulseResult updates beatsPerMinute with padding`() = runTest {
        val pulseCaptor = argumentCaptor<PulseListener>()
        viewModel.startMeasurement()
        verify(heartSupport).addOnResultListener(pulseCaptor.capture())
        
        pulseCaptor.firstValue.onPulseResult("75")
        assertEquals("075", viewModel.beatsPerMinute.value)
        
        pulseCaptor.firstValue.onPulseResult("105")
        assertEquals("105", viewModel.beatsPerMinute.value)
    }

    @Test
    fun `onTimerRunning updates progress percentage`() = runTest {
        val timerCaptor = argumentCaptor<TimerListener>()
        viewModel.startMeasurement()
        verify(heartSupport).addOnTimerListener(timerCaptor.capture())
        
        // timeLimit is 20000. If 15000 remaining, progress should be (20000-15000)*100/20000 = 25%
        timerCaptor.firstValue.onTimerRunning(15000)
        assertEquals(25, viewModel.progress.value)
        
        timerCaptor.firstValue.onTimerRunning(0)
        assertEquals(100, viewModel.progress.value)
    }

    @Test
    fun `onTimerStarted resets states`() = runTest {
        val timerCaptor = argumentCaptor<TimerListener>()
        viewModel.startMeasurement()
        verify(heartSupport).addOnTimerListener(timerCaptor.capture())
        
        timerCaptor.firstValue.onTimerStarted()
        assertEquals(true, viewModel.isStarted.value)
        assertEquals("000", viewModel.beatsPerMinute.value)
        assertEquals(0, viewModel.progress.value)
        assertEquals(null, viewModel.measurementResult.value)
    }

    @Test
    fun `onTimerStopped updates measurement result to Success`() = runTest {
        val timerCaptor = argumentCaptor<TimerListener>()
        val pulseCaptor = argumentCaptor<PulseListener>()
        viewModel.startMeasurement()
        verify(heartSupport).addOnTimerListener(timerCaptor.capture())
        verify(heartSupport).addOnResultListener(pulseCaptor.capture())
        
        // Simulate a valid measurement
        pulseCaptor.firstValue.onPulseResult("75")
        timerCaptor.firstValue.onTimerStopped()
        
        assertEquals(100, viewModel.progress.value)
        assertEquals(false, viewModel.isStarted.value)
        val result = viewModel.measurementResult.value
        assert(result is HeartViewModel.MeasurementResult.Success)
        assertEquals("075", (result as HeartViewModel.MeasurementResult.Success).bpm)
    }

    @Test
    fun `onTimerStopped with invalid pulse returns Error`() = runTest {
        val timerCaptor = argumentCaptor<TimerListener>()
        val pulseCaptor = argumentCaptor<PulseListener>()
        viewModel.startMeasurement()
        verify(heartSupport).addOnTimerListener(timerCaptor.capture())
        verify(heartSupport).addOnResultListener(pulseCaptor.capture())
        
        // Simulate a low measurement
        pulseCaptor.firstValue.onPulseResult("40")
        timerCaptor.firstValue.onTimerStopped()
        
        assertEquals(HeartViewModel.MeasurementResult.Error, viewModel.measurementResult.value)
    }

    @Test
    fun `saveHeartRate inserts history and resets state`() = runTest {
        viewModel.startMeasurement()
        val pulseCaptor = argumentCaptor<PulseListener>()
        verify(heartSupport).addOnResultListener(pulseCaptor.capture())
        pulseCaptor.firstValue.onPulseResult("80")
        
        viewModel.saveHeartRate()
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify(repository).insertHistory(any())
        assertEquals(null, viewModel.measurementResult.value)
        assertEquals("000", viewModel.beatsPerMinute.value)
        assertEquals(0, viewModel.progress.value)
    }

    @Test
    fun `reset clears all measurement values`() = runTest {
        viewModel.reset()
        assertEquals("000", viewModel.beatsPerMinute.value)
        assertEquals(0, viewModel.progress.value)
        assertEquals(null, viewModel.measurementResult.value)
    }
}
