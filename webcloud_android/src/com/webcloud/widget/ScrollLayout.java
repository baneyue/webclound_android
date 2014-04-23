package com.webcloud.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 自定义一个ScrollView实现屏幕的上下滑动
 */
public class ScrollLayout extends ViewGroup {
	private Scroller mScroller;
	// 用来检测用户的手势，或者也可用GestureDetector
	private VelocityTracker mVelocityTracker;
	// 当前的屏幕索引
	private int mCurrentScreen;
	// 默认的屏幕索引
	private int mDefaultScreen = 0;
	// 屏幕闲置时
	private static final int TOUCH_STATE_REST = 0;
	// 屏幕滚动时
	private static final int TOUCH_STATE_SCROLLING = 1;
	// 滚动速度
	private static final int SNAP_VELOCITY = 400;
	// 触屏状态为当前屏幕闲置
	private int mTouchState = TOUCH_STATE_REST;
	// 触屏溢出
	private int mTouchSlop;
	// 最后的纵向手势坐标
	private float mLastMotionY;
	private float mLastMotionX;

	private PageListener pageListener;

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		// 当前屏幕即为默认的屏幕，即为第一屏
		mCurrentScreen = mDefaultScreen;
		// 是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		// 0表示没有风格
		this(context, attrs, 0);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childTop = 0;
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childHeight = childView.getMeasuredHeight();
				childView.layout(0, childTop, childView.getMeasuredHeight(),
						childTop + childHeight);
				childTop += childHeight;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(0, mCurrentScreen * height);
	}

	public void snapToDestination() {
		final int screenHeight = getHeight();
		final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollY() != (whichScreen * getHeight())) {
			final int delta = whichScreen * getHeight() - getScrollY();
			mScroller.startScroll(0, getScrollY(), 0, delta,
					Math.abs(delta) * 2);
			mCurrentScreen = whichScreen;
			if (mCurrentScreen > Configure.curentPage) {
				Configure.curentPage = whichScreen;
				pageListener.page(Configure.curentPage);
			} else if (mCurrentScreen < Configure.curentPage) {
				Configure.curentPage = whichScreen;
				pageListener.page(Configure.curentPage);
			}
			invalidate(); // Redraw the layout
		}
	}

	/**
	 * 获得当前页码
	 */
	public int getCurScreen() {
		return mCurrentScreen;
	}

	/**
	 * 当滑动后的当前页码
	 */
	public int getPage() {
		return Configure.curentPage;
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurrentScreen = whichScreen;
		scrollTo(0, whichScreen * getHeight());
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float y = event.getY();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionY = y;
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			mLastMotionX = x;
			int deltaY = (int) (mLastMotionY - y);
			mLastMotionY = y;
			scrollBy(0, deltaY);
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityY = (int) velocityTracker.getYVelocity();
			int velocityX = (int) velocityTracker.getXVelocity();
			int moveX = Math.abs(velocityX);
			if (velocityY > SNAP_VELOCITY && getCurScreen() > 0) {
				snapToScreen(getCurScreen() - 1);
				pageListener.page(Configure.curentPage);
			} else if (velocityY < -SNAP_VELOCITY
					&& getCurScreen() < getChildCount() - 1) {
				snapToScreen(getCurScreen() + 1);
			} else if (getCurScreen() == 2) {
				snapToScreen(getCurScreen());
			} else {
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (Configure.isMove)
			return false;// 拦截分发给子控件
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int yDiff = (int) Math.abs(mLastMotionY - y);
			if (yDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	public void setPageListener(PageListener pageListener) {
		this.pageListener = pageListener;
	}

	public interface PageListener {
		void page(int page);
	}
}