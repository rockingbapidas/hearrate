package com.bapidas.heartrate.core

/**
 * Created by bapidas on 06/11/17.
 */
interface TimerListener {
    fun onTimerStarted()
    fun onTimerRunning(milliSecond: Long)
    fun onTimerStopped()
}