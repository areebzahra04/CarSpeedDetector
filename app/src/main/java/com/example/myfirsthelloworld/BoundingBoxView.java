package com.example.myfirsthelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoundingBoxView extends View {
    private Rect boundingBox;
    private Paint paint;

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
    }

    public void setBoundingBox(Rect box) {
        this.boundingBox = box;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boundingBox != null) {
            canvas.drawRect(boundingBox, paint);
        }
    }
}