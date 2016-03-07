package org.port0.nriedmann.simpletomato;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nicol on 3/7/2016.
 */
public class CounterView extends View {

    private int count, max_count;
    private Paint paint, ghost;
    private RectF box;



    public CounterView(Context c, AttributeSet attr, int count, int max_count) {
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
        this.count = count;
        this.max_count = max_count;
        box = new RectF(60, 60, 860, 260);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1;i<max_count;i++){
            Paint p = (i<=count)?paint:ghost;
            canvas.drawCircle(box.width()*i/max_count, box.centerY(), (box.width()-40)/max_count, p);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}