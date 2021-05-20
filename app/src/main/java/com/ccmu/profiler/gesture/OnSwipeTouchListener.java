package com.ccmu.profiler.gesture;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import androidx.core.view.GestureDetectorCompat;

import com.ccmu.profiler.BuildConfig;
import com.ccmu.profiler.ui.contacts.ContactModel;

import java.util.regex.Pattern;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetectorCompat gestureDetector;

    public OnSwipeTouchListener(final View view) {
        gestureDetector = new GestureDetectorCompat(view.getContext(), new GestureListener(view) {

            @Override
            public void onSwipeRight(MotionEvent start) {
            }

            @Override
            public void onSwipeLeft(MotionEvent start) {
                Log.d("GestureDetection", "Swipe Left detected");

                ListView l = (ListView) view;

                int childCount = l.getChildCount();
                View child, touched = null;
                int[] listViewCoords = new int[2];
                l.getLocationOnScreen(listViewCoords);
                int x = (int) start.getRawX() - listViewCoords[0];
                int y = (int) start.getRawY() - listViewCoords[1];

                for (int i = 0; i < childCount; i++) {
                    child = l.getChildAt(i);
                    Rect r = new Rect();
                    child.getHitRect(r);
                    if (r.contains(x, y)) {
                        touched = child;
                        break;
                    }
                }

                ContactModel contactModel = (ContactModel) l.getItemAtPosition(l.getPositionForView(touched));

                CharSequence number = contactModel.getNumbers()[0];

                // Removing "Number: "
                CharSequence dialNumber = number.subSequence(number.toString().indexOf(":") + 2, number.length());

                // Debugging assertion to ensure a valid number to be parsed
                if (BuildConfig.DEBUG && !(((Pattern.compile(" *\\(*\\+*\\d+\\)* \\d+ *-* *\\d+ *\\d*")).matcher(dialNumber)).matches())) {
                    Log.d("OnSwipeTouchListener DEBUG", dialNumber.toString());
                    Log.d("OnSwipeTouchListener DEBUG - REGEX", "Regex result is: " + (((Pattern.compile(" *\\(*\\+*\\d+\\)* \\d+ *-* *\\d+ *\\d*")).matcher(dialNumber)).matches()));
                    throw new AssertionError("Assertion failed");
                }

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dialNumber));
                view.getContext().startActivity(intent);
            }

            @Override
            public void onSwipeUp(MotionEvent start) {
            }

            @Override
            public void onSwipeDown(MotionEvent start) {
            }
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
