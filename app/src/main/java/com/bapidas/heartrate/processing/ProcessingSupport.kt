package com.bapidas.heartrate.processing

import android.media.Image

/**
 * Created by bapidas on 12/10/17.
 */
interface ProcessingSupport {
    fun yuvToNv(image: Image): ByteArray

    fun yuvSpToRedAvg(yuv420sp: ByteArray?, width: Int, height: Int): Int
}