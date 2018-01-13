package com.floatingwindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * <br> ClassName:   ${className}
 * <br> Description:
 * <br>
 * <br> @author:      谢文良
 * <br> Date:        2018/1/11 11:01
 */

public class DisView extends View {
    Paint paint;
    private float startX, startY;
    private float endX, endY;

    public DisView(Context context) {
        super(context);
        init();
    }

    public DisView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    public double getLine() {
        return Math.sqrt(Math.abs((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY)));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }
}
