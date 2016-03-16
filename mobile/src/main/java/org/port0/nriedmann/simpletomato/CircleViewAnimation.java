package org.port0.nriedmann.simpletomato;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by nicol on 3/3/2016.
 */
public class CircleViewAnimation extends Animation{
    private CircleView circle;
    private float currentAngle, newAngle;

    public CircleViewAnimation(CircleView circle, int newAngle){
        this.currentAngle = circle.getAngle();
        this.newAngle=newAngle;
        this.circle=circle;
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = currentAngle + ((newAngle - currentAngle) * interpolatedTime);
        circle.setAngle(angle);
        circle.requestLayout();
    }
}
