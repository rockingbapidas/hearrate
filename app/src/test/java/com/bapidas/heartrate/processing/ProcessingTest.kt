package com.bapidas.heartrate.processing

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProcessingTest {

    private lateinit var processing: Processing

    @Before
    fun setUp() {
        processing = Processing()
    }

    @Test
    fun `calculateAverageRed with empty buffer returns zero`() {
        val nv21 = ByteArray(0)
        val result = processing.calculateAverageRed(nv21, 0, 0)
        assertEquals(0, result)
    }

    @Test
    fun `calculateAverageRed with small red buffer returns average red`() {
        // 2x2 frame, size = 4
        // NV21 layout: YYYY VU
        // For a red image:
        // R=255, G=0, B=0
        // Y = 0.299*255 = 76
        // U = -0.169*255 + 128 = 85
        // V = 0.500*255 + 128 = 255 (capped)
        
        val width = 2
        val height = 2
        val frameSize = width * height
        val nv21 = ByteArray(frameSize + frameSize / 2)
        
        // Fill Y with 76
        for (i in 0 until frameSize) nv21[i] = 76.toByte()
        // Fill V with 255 (at index 4) and U with 85 (at index 5)
        nv21[4] = 255.toByte()
        nv21[5] = 85.toByte()
        
        val result = processing.calculateAverageRed(nv21, width, height)
        
        // The exact value might vary slightly due to fixed-point math in implementation
        // But it should be close to 255 for a pure red image
        // Based on formula: y1192 = 1192 * (76-16) = 71520
        // v = 255-128 = 127
        // rRaw = 71520 + 1634 * 127 = 71520 + 207518 = 279038
        // Clamp rRaw to 262143
        // red = (262143 >> 10) & 0xff = 255
        assertEquals(255, result)
    }

    @Test
    fun `calculateAverageRed with black buffer returns low red value`() {
        val width = 2
        val height = 2
        val frameSize = width * height
        val nv21 = ByteArray(frameSize + frameSize / 2)
        
        // Fill everything with zero (Y=0, U=0, V=0 which is close to black/greenish)
        // Y=16 is technically black in limited range YUV
        for (i in 0 until nv21.size) nv21[i] = 0
        
        val result = processing.calculateAverageRed(nv21, width, height)
        assertEquals(0, result)
    }
}
