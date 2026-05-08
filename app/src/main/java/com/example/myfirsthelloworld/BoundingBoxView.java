package com.example.myfirsthelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoundingBoxView extends View {
    private Rect boundingBox;
    private Paint paint;
    private Paint textPaint;

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);
        paint.setPathEffect(new DashPathEffect(new float[]{20, 15}, 0));

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAlpha(180);
    }

    public void setBoundingBox(Rect box) {
        this.boundingBox = box;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boundingBox != null) {
            // Draw the rectangle
            canvas.drawRect(boundingBox, paint);

            // Draw label
            canvas.drawText("DETECTION ZONE",
                    boundingBox.centerX(),
                    boundingBox.centerY(),
                    textPaint);
        }
    }
}