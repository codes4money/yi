package com.studio.b56.im.app.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CusScollview extends ScrollView {
	public CusScollview(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(new Yscroll());
		setFadingEdgeLength(0);
	}

	GestureDetector gestureDetector;
	View.OnTouchListener onTouchListener;

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// return super.onInterceptTouchEvent(ev);
		return super.onInterceptTouchEvent(ev)
				&& gestureDetector.onTouchEvent(ev);
	}

	class Yscroll extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (Math.abs(distanceY) >= Math.abs(distanceX)) {
				return true;
			}
			return false;
		}
	}
}
