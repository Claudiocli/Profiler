package com.ccmu.profiler.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class GestureListener extends GestureDetector.SimpleOnGestureListener    {

    /**
     * Threshold used to analyze a potential user-made gesture.
     */
    private static final int SWIPE_THRESHOLD=100;
    /**
     * Threshold used to analyze a potential user-made gesture.
     */
    private static final int VELOCITY_THRESHOLD=100;

    private View view;

    public GestureListener()    {
        super();
    }
    public GestureListener(View v)    {
        super();
        this.view=v;
    }

    /**
     * Function that evaluate if a certain movement was executed.
     * It will analyze the difference between the start coordinates and the end coordinates of the given events <code>e1</code> and <code>e2</code>.
     * If that value is greater of the <code>SWIPE_THRESHOLD</code> constant and the velocity of the action is greater than the <code>VELOCITY_THRESHOLD</code>
     * then, the action detected, will be treated as an intended user's gesture. For the above reasons, if the gesture is evaluated as voluntary, the function
     * return <code>true</code> (consuming the <code>event</code>), <code>false</code> otherwise.
     * @param e1 The first down <code>MotionEvent</code> that started the fling.
     * @param e2 The move <code>MotionEvent</code> that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second along the y axis.
     * @return <code>True</code> if the gesture is one voluntarily by the user and so the <code>event</code> is consumed, <code>false</code> otherwise.
     * @see #SWIPE_THRESHOLD
     * @see #VELOCITY_THRESHOLD
     * @see #onSwipeRight()
     * @see #onSwipeLeft()
     * @see #onSwipeUp()
     * @see #onSwipeDown()
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean return_value = false;

        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > VELOCITY_THRESHOLD) {
            if (deltaX > 0)
                onSwipeRight();
            else
                onSwipeLeft();
            return_value = true;
        }
        else if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > VELOCITY_THRESHOLD) {
            if (deltaY > 0)
                onSwipeDown();
            else
                onSwipeUp();
            return_value=true;
        }
        return return_value;
    }

    public View getView()   {return view;}
    /**
     * Abstract function that define the detection of a right swipe from the user.
     * It's called when (and if) the corresponding gesture is identified.
     * @see #onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
     * @see #onSwipeLeft()
     * @see #onSwipeUp()
     * @see #onSwipeDown()
     */
    public abstract void onSwipeRight();
    /**
     * Abstract function that define the detection of a right swipe from the user.
     * It's called when (and if) the corresponding gesture is identified.
     * @see #onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
     * @see #onSwipeRight()
     * @see #onSwipeUp()
     * @see #onSwipeDown()
     */
    public abstract void onSwipeLeft();
    /**
     * Abstract function that define the detection of a right swipe from the user.
     * It's called when (and if) the corresponding gesture is identified.
     * @see #onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
     * @see #onSwipeRight()
     * @see #onSwipeLeft()
     * @see #onSwipeDown()
     */
    public abstract void onSwipeUp();
    /**
     * Abstract function that define the detection of a right swipe from the user.
     * It's called when (and if) the corresponding gesture is identified.
     * @see #onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
     * @see #onSwipeRight()
     * @see #onSwipeLeft()
     * @see #onSwipeUp()
     */
    public abstract void onSwipeDown();

}
