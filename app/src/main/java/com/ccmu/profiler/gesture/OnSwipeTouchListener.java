package com.ccmu.profiler.gesture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ccmu.profiler.BuildConfig;
import com.ccmu.profiler.R;

import java.util.regex.Pattern;

public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;
        private View view;

        public OnSwipeTouchListener(final View view)    {
            this.view = view;
            gestureDetector = new GestureDetector(view.getContext(), new GestureListener(view)    {

                @Override
                public void onSwipeRight() {}

                @Override
                public void onSwipeLeft() {
                    CharSequence number = ((TextView) getView().findViewById(R.id.contactNumber)).getText();
                    // Removing "Number: "
                    CharSequence dialNumber = number.subSequence(number.toString().indexOf(":") + 1, number.length());
                    // Debugging assertion to ensure a valid number to be parsed
                    if (BuildConfig.DEBUG && !(((Pattern.compile("/\\(*\\+*\\d+\\)* \\d+ \\d+ *\\d*")).matcher(number)).matches())) {
                        throw new AssertionError("Assertion failed");
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dialNumber));
                    view.getContext().startActivity(intent);
                }

                @Override
                public void onSwipeUp() {}

                @Override
                public void onSwipeDown() {}
            });
        }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
