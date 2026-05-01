//package com.example.myfirsthelloworld;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.View;
//
//public class BoundingBoxView extends View {
//    private Rect boundingBox;
//    private Paint paint;
//
//    public BoundingBoxView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(10f);
//    }
//
//    public void setBoundingBox(Rect box) {
//        this.boundingBox = box;
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (boundingBox != null) {
//            canvas.drawRect(boundingBox, paint);
//        }
//    }
//}


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
        paint.setStrokeWidth(8f);
        paint.setPathEffect(new DashPathEffect(new float[]{20, 15}, 0));

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAlpha(150);
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

            float centerX = boundingBox.centerX();
            float centerY = boundingBox.centerY();
            canvas.drawText("DETECTION", centerX, centerY - 10, textPaint);
            canvas.drawText("ZONE", centerX, centerY + 50, textPaint);
        }
    }
}