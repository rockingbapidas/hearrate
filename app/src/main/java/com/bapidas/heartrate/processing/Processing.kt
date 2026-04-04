package com.bapidas.heartrate.processing

import android.media.Image
import java.nio.ByteBuffer

/**
 * Updated processing class with clearer naming and optimized YUV-to-RGB conversion.
 */
class Processing : ProcessingSupport {

    override fun toNv21(image: Image): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // NV21 is YYYY VUVU. Note: U and V are often swapped in internal buffers.
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    override fun calculateAverageRed(nv21: ByteArray, width: Int, height: Int): Int {
        val frameSize = width * height
        var redSum = 0L // Use Long to avoid overflow during summation
        var yp = 0

        for (j in 0 until height) {
            var uvp = frameSize + (j shr 1) * width
            var u = 0
            var v = 0
            
            for (i in 0 until width) {
                // Extract Y component
                val yRaw = (0xff and nv21[yp].toInt()) - 16
                val y = if (yRaw < 0) 0 else yRaw
                
                // Extract U and V components every 2 pixels (since it's 4:2:0)
                if (i and 1 == 0) {
                    v = (0xff and nv21[uvp++].toInt()) - 128
                    u = (0xff and nv21[uvp++].toInt()) - 128
                }
                
                // standard YUV to RGB conversion formula for Red
                val y1192 = 1192 * y
                val rRaw = y1192 + 1634 * v
                
                // Clamp R value between 0 and 262143
                val r = when {
                    rRaw < 0 -> 0
                    rRaw > 262143 -> 262143
                    else -> rRaw
                }
                
                // Extract the red byte (scaled back to 0-255 range)
                val red = (r shr 10) and 0xff
                redSum += red
                
                yp++
            }
        }
        
        return if (frameSize > 0) (redSum / frameSize).toInt() else 0
    }

    companion object {
        private val TAG = Processing::class.java.simpleName
    }
}
