package com.webcloud.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ScrollView;

public class MultiScroll extends ScrollView {

	private Handler handler;
	private View view;

	public MultiScroll(Context context) {
		super(context);

	}

	public MultiScroll(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public MultiScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @Override public boolean onInterceptTouchEvent(MotionEvent event)
	 *           //这个方法如果返回 true 的话 两个手指移动，启动一个按下的手指的移动不能被传播出去。 {
	 *           super.onInterceptTouchEvent(event); return false; }
	 * @Override public boolean onTouchEvent(MotionEvent event) //这个方法如果 true
	 *           则整个Activity 的 onTouchEvent() 不会被系统回调 {
	 *           super.onTouchEvent(event); return false; }
	 */
	// 这个获得总的高度
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	private void init() {

		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// process incoming messages here
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if (view.getMeasuredHeight() <= getScrollY() + getHeight()) {
						if (onScrollListener != null) {
							onScrollListener.onBottom();
						}

					} else if (getScrollY() == 0) {
						if (onScrollListener != null) {
							onScrollListener.onTop();
						}
					} else {
						if (onScrollListener != null) {
							onScrollListener.onScroll();
						}
					}
					break;
				default:
					break;
				}
			}
		};

	}

	private VelocityTracker mVelocityTracker;
	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (view != null && onScrollListener != null) {
					//handler.sendMessageDelayed(handler.obtainMessage(1), 200);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityY = (int) velocityTracker.getYVelocity();
				int velocityX = (int) velocityTracker.getYVelocity();
				
				if (velocityY > 100) {
					if (view != null && onScrollListener != null) {
						handler.sendMessageDelayed(handler.obtainMessage(1), 200);
					}
				}
				
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
			default:
				break;
			}
			return false;
		}

	};

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);
		if (view != null) {
			init();
		}
	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

}
