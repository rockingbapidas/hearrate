package com.bapidas.heartrate.core

/**
 * Created by bapidas on 03/11/17.
 */
interface HeartSupport {
    fun startPulseCheck(timeLimit: Long): HeartSupport
    fun startPulseCheck(): HeartSupport
    fun addOnResultListener(pulseListener: PulseListener): HeartSupport
    fun addOnFingerListener(fingerListener: FingerListener): HeartSupport
    fun addOnTimerListener(timerListener: TimerListener): HeartSupport
    fun stopPulseCheck()
}