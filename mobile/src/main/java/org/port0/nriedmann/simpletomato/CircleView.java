package org.port0.nriedmann.simpletomato;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by nicol on 3/3/2016.
 */
public class CircleView extends View {

    private int start_angle = 0;
    private float angle = 00.0f;
    private Paint paint;
    private RectF box;

    public CircleView(Context c, AttributeSet attr){
        super(c, attr);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(60);
        this.paint.setColor(Color.RED);
        box = new RectF(60,60,860,860);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint ghost = new Paint();
        ghost.setAntiAlias(true);
        ghost.setAlpha(50);
        ghost.setStyle(Paint.Style.STROKE);
        ghost.setStrokeWidth(50);
        ghost.setColor(Color.GRAY);
        canvas.drawCircle(box.centerX(),box.centerY(),400,ghost);
        canvas.drawArc(box, start_angle, angle, false, paint);
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

}
