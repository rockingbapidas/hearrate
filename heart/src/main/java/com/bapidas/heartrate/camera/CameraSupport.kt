package com.bapidas.heartrate.camera

/**
 * Created by bapidas on 12/10/17.
 */
interface CameraSupport {
    fun open(): CameraSupport
    fun close()
    val isCameraInUse: Boolean
    fun addOnPreviewListener(callBack: PreviewListener)
}