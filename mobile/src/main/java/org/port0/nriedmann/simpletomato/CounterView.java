package org.port0.nriedmann.simpletomato;

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
 * Created by nicol on 3/7/2016.
 */
public class CounterView extends View {

    private int count;
    private int max_count;
    private Paint paint, ghost;

    public CounterView(Context c, AttributeSet attr) {
        super(c, attr);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(Color.RED);
        this.ghost = new Paint();
        this.ghost.setAntiAlias(true);
        this.ghost.setAlpha(50);
        this.ghost.setStyle(Paint.Style.FILL);
        this.ghost.setColor(Color.GRAY);
        this.count = 0;
        this.max_count = 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = canvas.getWidth();
        //canvas.drawRect(box,paint);
        float pos = width/max_count/2;
        for (float i = 0;i<max_count;i++){
            Paint p = (i<count)?paint:ghost;
            float percent = (i<count)?0.4f:0.3f;
            Log.i("Circle "+i, (pos+width*i/max_count)+"");
            canvas.drawCircle(pos+width*i/max_count, (width/max_count)*0.5f, (width/max_count)*percent, p);
        }
    }

    public void update(int counter){
        this.count=counter;
        this.forceLayout();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMax_count() {
        return max_count;
    }

    public void setMax_count(int max_count) {
        this.max_count = max_count;
    }
}