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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by nicol on 3/3/2016.
 */
public class CircleView extends View {

    private int start_angle = 0;
    private float angle = 00.0f;
    private Paint paint, ghost;

    public CircleView(Context c, AttributeSet attr){
        super(c, attr);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
  //      this.paint.setStrokeWidth(60);
        this.paint.setColor(Color.RED);

        this.ghost = new Paint();
        this.ghost.setAntiAlias(true);
        this.ghost.setAlpha(50);
        this.ghost.setStyle(Paint.Style.STROKE);
//        this.ghost.setStrokeWidth(50);
        this.ghost.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.paint.setStrokeWidth(canvas.getWidth() * 0.1f);
        this.ghost.setStrokeWidth(canvas.getWidth() * 0.08f);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 * 0.8f, ghost);
        RectF box = new RectF(canvas.getWidth()/2-canvas.getWidth()/2*0.8f,canvas.getHeight()/2-canvas.getWidth()/2*0.8f,canvas.getWidth()/2+canvas.getWidth()/2*0.8f, canvas.getHeight()/2+canvas.getWidth()/2*0.8f);
        canvas.drawArc(box, start_angle, angle, false, paint);
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

}
