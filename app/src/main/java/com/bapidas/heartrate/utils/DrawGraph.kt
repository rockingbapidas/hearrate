package com.bapidas.heartrate.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * Created by bapidas on 30/10/17.
 */
class DrawGraph : View {
    private var dataX = IntArray(30)
    private var dataY = IntArray(30)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: Canvas) {
        val h = 280
        val w = 600
        //Generate random
        val min = 0
        val max = 280
        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 6f
        dataX[0] = 0
        for (i in 0 until w / 20 - 1) {
            dataX[i + 1] = (i + 1) * w / 20
            dataY[w / 20 - 1] = generateRandomPositiveNegativeValue(max, min)
            dataY[i] = dataY[i + 1]
        }
        for (i in 0 until w / 20 - 1) {
            // apply some transformation on data in order to map it correctly
            // in the coordinates of the canvas
            canvas.drawLine(
                dataX[i].toFloat(),
                h / 2 - dataY[i].toFloat(), dataX[i + 1].toFloat(),
                h / 2 - dataY[i + 1].toFloat(), paint
            )
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            invalidate()
        }
    }

    private fun generateRandomPositiveNegativeValue(max: Int, min: Int): Int {
        val r = Random()
        val ii = r.nextInt(max - min + 1) + min
        return ii - 140
    }
}