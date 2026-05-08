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
    private Rect detectedBox;
    private Paint boxPaint;
    private Paint textPaint;

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint for detected object box
        boxPaint = new Paint();
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(8f);

        // Paint for text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setBoundingBox(Rect box) {
        this.detectedBox = box;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (detectedBox != null) {
            // Draw box around detected object
            canvas.drawRect(detectedBox, boxPaint);

            // Draw label above the box
            textPaint.setColor(Color.GREEN);
            canvas.drawText("OBJECT DETECTED",
                    detectedBox.centerX(),
                    detectedBox.top - 20,
                    textPaint);
        }
    }
}