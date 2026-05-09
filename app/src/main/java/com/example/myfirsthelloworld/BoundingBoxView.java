package com.example.myfirsthelloworld;

import android.content.Context;
import android.graphics.Canvas;
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
        // Nothing drawn - invisible detection zone
    }
}