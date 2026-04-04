package com.bapidas.heartrate.processing

import android.media.Image

/**
 * Interface for image processing tasks related to heart rate detection.
 */
interface ProcessingSupport {
    /**
     * Converts an [Image] (typically YUV_420_888) to a standard NV21 byte array.
     */
    fun toNv21(image: Image): ByteArray

    /**
     * Calculates the average red intensity from an NV21 formatted image buffer.
     * This is used to detect the pulse by monitoring changes in red light absorption.
     */
    fun calculateAverageRed(nv21: ByteArray, width: Int, height: Int): Int
}