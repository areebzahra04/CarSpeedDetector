package com.example.myfirsthelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoundingBoxView extends View {

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBoundingBox(Rect box) {
        // Box is hidden - detection zone is the speedometer area
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw green corner brackets
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#00FF00"));  // Bright green
        paint.setStrokeWidth(10);  // Thickness of lines
        paint.setStyle(Paint.Style.STROKE);

        int w = getWidth();   // Screen width
        int h = getHeight();  // Screen height
        int size = 100;       // Length of each corner line
        int margin = 40;      // Distance from edge

        // Top-Left corner (┌)
        canvas.drawLine(margin, margin, margin + size, margin, paint);
        canvas.drawLine(margin, margin, margin, margin + size, paint);

        // Top-Right corner (┐)
        canvas.drawLine(w - margin, margin, w - margin - size, margin, paint);
        canvas.drawLine(w - margin, margin, w - margin, margin + size, paint);

        // Bottom-Left corner (└)
        canvas.drawLine(margin, h - margin, margin + size, h - margin, paint);
        canvas.drawLine(margin, h - margin, margin, h - margin - size, paint);

        // Bottom-Right corner (┘)
        canvas.drawLine(w - margin, h - margin, w - margin - size, h - margin, paint);
        canvas.drawLine(w - margin, h - margin, w - margin, h - margin - size, paint);
    }
}