package com.vantagecircle.heartrate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by bapidas on 30/10/17.
 */

public class DrawGraph extends View {
    int[] dataX = new int[30];
    int[] dataY = new int[30];

    public DrawGraph(Context context) {
        super(context);
    }

    public DrawGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int w;
        int h;
        h = 280;
        w = 600;
        //Generate random
        int min = 0;
        int max = 280;
        @SuppressLint("DrawAllocation")
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6);
        dataX[0] = 0;
        for (int i = 0; i < w / 20 - 1; i++) {
            dataX[i + 1] = (i + 1) * w / 20;
            dataY[w / 20 - 1] = generateRandomPositiveNegativeValue(max, min);
            dataY[i] = dataY[i + 1];
        }
        for (int i = 0; i < w / 20 - 1; i++) {
            // apply some transformation on data in order to map it correctly
            // in the coordinates of the canvas
            canvas.drawLine(dataX[i],
                    h / 2 - dataY[i], dataX[i + 1],
                    h / 2 - dataY[i + 1], paint);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            invalidate();
        }
    }

    public int generateRandomPositiveNegativeValue(int max, int min) {
        Random r = new Random();
        int ii = r.nextInt(max - min + 1) + min;
        return (ii - 140);
    }
}
