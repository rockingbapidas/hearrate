package com.bapidas.heartrate.ui.fragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bapidas.heartrate.core.HeartSupport
import com.bapidas.heartrate.core.PulseListener
import com.bapidas.heartrate.core.TimerListener
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.data.repository.HistoryRepository
import com.bapidas.heartrate.utils.ToolsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeartViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val heartSupport: HeartSupport
) : ViewModel() {

    private val _beatsPerMinute = MutableStateFlow("000")
    val beatsPerMinute: StateFlow<String> = _beatsPerMinute.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _isStarted = MutableStateFlow(false)
    val isStarted: StateFlow<Boolean> = _isStarted.asStateFlow()

    private val _measurementResult = MutableStateFlow<MeasurementResult?>(null)
    val measurementResult: StateFlow<MeasurementResult?> = _measurementResult.asStateFlow()

    private val timeLimit: Long = 20000

    fun startMeasurement() {
        if (_isStarted.value) {
            _isStarted.value = false
            heartSupport.stopPulseCheck()
        } else {
            initHeart()
        }
    }

    private fun initHeart() {
        heartSupport.addOnResultListener(object : PulseListener {
            override fun onPulseResult(pulse: String) {
                _beatsPerMinute.value = if (pulse.length == 2) "0$pulse" else pulse
            }
        }).addOnTimerListener(object : TimerListener {
            override fun onTimerStarted() {
                _isStarted.value = true
                _beatsPerMinute.value = "000"
                _progress.value = 0
                _measurementResult.value = null
            }

            override fun onTimerRunning(milliSecond: Long) {
                val n = timeLimit - milliSecond
                _progress.value = (n * 100 / timeLimit).toInt()
            }

            override fun onTimerStopped() {
                _progress.value = 100
                _isStarted.value = false
                val bpmString = _beatsPerMinute.value
                val value = bpmString.toIntOrNull() ?: 0
                if (value == 0 || value < 50) {
                    _measurementResult.value = MeasurementResult.Error
                } else {
                    _measurementResult.value = MeasurementResult.Success(bpmString)
                }
            }
        }).startPulseCheck(timeLimit)
    }

    fun saveHeartRate() {
        val bpm = _beatsPerMinute.value
        viewModelScope.launch {
            val timeStamp = System.currentTimeMillis()
            val date = ToolsUtils.getDate(timeStamp)
            val time = ToolsUtils.getTime(timeStamp)
            repository.insertHistory(HistoryModel(heartRate = bpm, dateString = date, timeString = time))
            _measurementResult.value = null
            _beatsPerMinute.value = "000"
            _progress.value = 0
        }
    }

    fun reset() {
        _measurementResult.value = null
        _beatsPerMinute.value = "000"
        _progress.value = 0
    }

    sealed class MeasurementResult {
        data class Success(val bpm: String) : MeasurementResult()
        object Error : MeasurementResult()
    }
}