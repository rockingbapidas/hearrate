package com.bapidas.heartrate.core

/**
 * Created by bapidas on 13/11/17.
 */
interface FingerListener {
    fun onFingerDetected(success: Int)
    fun onFingerDetectFailed(failed: Int)
}