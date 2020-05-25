package com.bapidas.heartrate.core

import android.os.CountDownTimer
import android.util.Log
import com.bapidas.heartrate.camera.CameraSupport
import com.bapidas.heartrate.camera.PreviewListener
import com.bapidas.heartrate.utils.TYPE
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by bapidas on 03/11/17.
 */
class HeartRate(private val mCameraSupport: CameraSupport) : HeartSupport, PreviewListener {
    private var mTimerListener: TimerListener? = null
    private var mPulseListener: PulseListener? = null
    private var mFingerListener: FingerListener? = null
    private var timeLimit: Long = 0
    private var isTimeRunning = false
    private var countDownTimer: CountDownTimer? = null
    private val averageArraySize = 4
    private val averageArray = IntArray(averageArraySize)
    private val beatsArraySize = 3
    private val beatsArray = DoubleArray(beatsArraySize)
    private var averageIndex = 0
    private var beatsIndex = 0
    private var beats = 0.0
    private var startTime = System.currentTimeMillis()
    private var currentType = TYPE.GREEN
    private var errorCount = 0
    private var successCount = 0
    private val processing = AtomicBoolean(false)

    val heartSupport: HeartSupport
        get() = this

    override fun startPulseCheck(timeLimit: Long): HeartSupport {
        this.timeLimit = timeLimit
        if (!mCameraSupport.isCameraInUse) {
            reset()
            startTimer()
            mCameraSupport.open().addOnPreviewListener(this)
            if (mTimerListener != null) {
                mTimerListener?.onTimerStarted()
            }
        } else {
            throw RuntimeException("Camera is already in use state")
        }
        return this
    }

    override fun startPulseCheck(): HeartSupport {
        if (!mCameraSupport.isCameraInUse) {
            reset()
            startTimer()
            mCameraSupport.open().addOnPreviewListener(this)
            if (mTimerListener != null) {
                mTimerListener?.onTimerStarted()
            }
        } else {
            throw RuntimeException("Camera is already in use state")
        }
        return this
    }

    override fun addOnResultListener(pulseListener: PulseListener): HeartSupport {
        mPulseListener = pulseListener
        return this
    }

    override fun addOnFingerListener(fingerListener: FingerListener): HeartSupport {
        mFingerListener = fingerListener
        return this
    }

    override fun addOnTimerListener(timerListener: TimerListener): HeartSupport {
        mTimerListener = timerListener
        return this
    }

    override fun stopPulseCheck() {
        if (mCameraSupport.isCameraInUse) {
            mCameraSupport.close()
            if (isTimeRunning) {
                countDownTimer?.cancel()
                isTimeRunning = false
            }
            if (mTimerListener != null) {
                mTimerListener?.onTimerStopped()
            }
        } else {
            throw RuntimeException("Camera is already in close state")
        }
    }

    override fun onPreviewData(data: ByteArray?) {

    }

    override fun onPreviewCount(count: Int) {
        calculatePulse(count)
    }

    private fun reset() {
        errorCount = 0
        successCount = 0
        averageIndex = 0
        beatsIndex = 0
        beats = 0.0
        currentType = TYPE.GREEN
        startTime = System.currentTimeMillis()
    }

    private fun startTimer() {
        if (timeLimit != 0L) {
            countDownTimer = object : CountDownTimer(timeLimit, 500) {
                override fun onTick(millisUntilFinished: Long) {
                    isTimeRunning = true
                    if (mTimerListener != null) {
                        mTimerListener?.onTimerRunning(millisUntilFinished)
                    }
                }

                override fun onFinish() {
                    isTimeRunning = false
                    stopPulseCheck()
                }
            }
            countDownTimer?.start()
        }
    }

    private fun calculatePulse(pixelAverage: Int) {
        Log.e(TAG, "pixel === $pixelAverage")
        if (!processing.compareAndSet(false, true)) {
            return
        }
        if (pixelAverage == 0 || pixelAverage >= 255) {
            processing.set(false)
            return
        }

        /*if (pixelAverage < 200) {
            errorCount++;
            if (mFingerListener != null) {
                mFingerListener.OnFingerDetectFailed(errorCount);
            }
            processing.set(false);
        } else {
            successCount++;
            if (mFingerListener != null) {
                mFingerListener.OnFingerDetected(successCount);
            }
        }*/
        var averageArrayAvg = 0
        var averageArrayCnt = 0
        for (anAverageArray in averageArray) {
            if (anAverageArray > 0) {
                averageArrayAvg += anAverageArray
                averageArrayCnt++
            }
        }
        val rollingAverage =
            if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0
        var newType = currentType
        if (pixelAverage < rollingAverage) {
            newType = TYPE.RED
            if (newType !== currentType) {
                beats++
            }
        } else if (pixelAverage > rollingAverage) {
            newType = TYPE.GREEN
        }
        if (averageIndex == averageArraySize) averageIndex = 0
        averageArray[averageIndex] = pixelAverage
        averageIndex++
        if (newType !== currentType) {
            currentType = newType
        }
        val endTime = System.currentTimeMillis()
        val totalTimeInSecs = (endTime - startTime) / 1000.0
        if (totalTimeInSecs >= 5) {
            val bps = beats / totalTimeInSecs
            val dpm = bps * 60.0
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis()
                beats = 0.0
                processing.set(false)
                return
            }
            if (beatsIndex == beatsArraySize) beatsIndex = 0
            beatsArray[beatsIndex] = dpm
            beatsIndex++
            var beatsArrayAvg = 0
            var beatsArrayCnt = 0
            for (aBeatsArray in beatsArray) {
                if (aBeatsArray > 0) {
                    beatsArrayAvg += aBeatsArray.toInt()
                    beatsArrayCnt++
                }
            }
            val beatsAvg = beatsArrayAvg / beatsArrayCnt
            val beatsPerMinuteValue = beatsAvg.toString()
            if (mPulseListener != null) {
                mPulseListener?.onPulseResult(beatsPerMinuteValue)
            }
            startTime = System.currentTimeMillis()
            beats = 0.0
        }
        processing.set(false)
    }

    companion object {
        private val TAG = HeartRate::class.java.simpleName
    }

}