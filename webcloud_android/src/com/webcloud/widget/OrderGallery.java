package com.webcloud.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class OrderGallery extends Gallery {

	public static int FLINGTHRESHOLD = 0;

	public OrderGallery(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		// Convert the dips to pixels
//		float scale = getResources().getDisplayMetrics().density;
//		FLINGTHRESHOLD = (int) (20.0f * scale + 0.5f);
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			// Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			// Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return false;
	}
}
