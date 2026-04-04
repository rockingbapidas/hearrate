package com.bapidas.heartrate.core

import android.util.Log
import com.bapidas.heartrate.camera.CameraSupport
import com.bapidas.heartrate.camera.PreviewListener
import com.bapidas.heartrate.utils.TYPE
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Refactored to use modern Kotlin Coroutines for timing and idiomatic code practices.
 */
class HeartRate(private val mCameraSupport: CameraSupport) : HeartSupport, PreviewListener {
    
    private var mTimerListener: TimerListener? = null
    private var mPulseListener: PulseListener? = null
    
    private var timeLimit: Long = 0
    private var measurementJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val averageArraySize = 4
    private val averageArray = IntArray(averageArraySize)
    private val beatsArraySize = 3
    private val beatsArray = DoubleArray(beatsArraySize)
    
    private var averageIndex = 0
    private var beatsIndex = 0
    private var beats = 0.0
    private var startTime = System.currentTimeMillis()
    private var currentType = TYPE.GREEN
    private val processing = AtomicBoolean(false)

    override fun startPulseCheck(timeLimit: Long): HeartSupport {
        this.timeLimit = timeLimit
        startMeasurement()
        return this
    }

    override fun startPulseCheck(): HeartSupport {
        startMeasurement()
        return this
    }

    private fun startMeasurement() {
        if (!mCameraSupport.isCameraInUse) {
            reset()
            mCameraSupport.open().addOnPreviewListener(this)
            mTimerListener?.onTimerStarted()
            startTimer()
        } else {
            Log.e(TAG, "Camera is already in use")
        }
    }

    override fun addOnResultListener(pulseListener: PulseListener): HeartSupport {
        mPulseListener = pulseListener
        return this
    }

    override fun addOnTimerListener(timerListener: TimerListener): HeartSupport {
        mTimerListener = timerListener
        return this
    }

    override fun stopPulseCheck() {
        if (mCameraSupport.isCameraInUse) {
            mCameraSupport.close()
            measurementJob?.cancel()
            mTimerListener?.onTimerStopped()
        }
    }

    override fun onPreviewData(data: ByteArray?) {
        // Not used in current implementation
    }

    override fun onPreviewCount(count: Int) {
        calculatePulse(count)
    }

    private fun reset() {
        averageIndex = 0
        beatsIndex = 0
        beats = 0.0
        currentType = TYPE.GREEN
        startTime = System.currentTimeMillis()
        averageArray.fill(0)
        beatsArray.fill(0.0)
    }

    private fun startTimer() {
        if (timeLimit <= 0) return

        measurementJob?.cancel()
        measurementJob = scope.launch {
            var remaining = timeLimit
            while (remaining > 0) {
                mTimerListener?.onTimerRunning(remaining)
                delay(500)
                remaining -= 500
            }
            withContext(Dispatchers.Main) {
                stopPulseCheck()
            }
        }
    }

    private fun calculatePulse(pixelAverage: Int) {
        if (!processing.compareAndSet(false, true)) return
        
        if (pixelAverage !in 1..<255) {
            processing.set(false)
            return
        }

        // Calculate rolling average using idiomatic Kotlin
        val validAverages = averageArray.filter { it > 0 }
        val rollingAverage = if (validAverages.isNotEmpty()) validAverages.average().toInt() else 0
        
        var newType = currentType
        if (pixelAverage < rollingAverage) {
            newType = TYPE.RED
            if (newType != currentType) {
                beats++
            }
        } else if (pixelAverage > rollingAverage) {
            newType = TYPE.GREEN
        }

        if (averageIndex >= averageArraySize) averageIndex = 0
        averageArray[averageIndex++] = pixelAverage
        
        currentType = newType

        val totalTimeInSecs = (System.currentTimeMillis() - startTime) / 1000.0
        if (totalTimeInSecs >= 5) {
            val dpm = (beats / totalTimeInSecs) * 60.0
            
            if (dpm in 30.0..180.0) {
                if (beatsIndex >= beatsArraySize) beatsIndex = 0
                beatsArray[beatsIndex++] = dpm
                
                val validBeats = beatsArray.filter { it > 0 }
                val beatsAvg = if (validBeats.isNotEmpty()) validBeats.average().toInt() else 0
                
                mPulseListener?.onPulseResult(beatsAvg.toString())
            }
            
            // Reset for next window
            startTime = System.currentTimeMillis()
            beats = 0.0
        }
        
        processing.set(false)
    }

    companion object {
        private val TAG = HeartRate::class.java.simpleName
    }
}
