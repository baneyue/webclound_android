package com.webcloud.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

public class MineSlidingDrawer extends SlidingDrawer {

	private int mHandleId = 0; // 抽屉行为控件ID
	private int mTouchableIds = -1; // Handle 部分其他控件ID
	private View mHandle;//把手
    private View mContent;//内容
	private int mContentId = 0; // 内容控件ID

	public int getHandleId() {
		return mHandleId;
	}

	public void setHandleId(int mHandleId) {
		this.mHandleId = mHandleId;
	}

	public int getTouchableIds() {
		return mTouchableIds;
	}

	public void setTouchableIds(int mTouchableIds) {
		this.mTouchableIds = mTouchableIds;
	}

	public MineSlidingDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
//		int orientation = attrs.getAttributeIntValue("android", "orientation",
//				ORIENTATION_VERTICAL);
//		mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
//		mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
	}

	public MineSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		int orientation = attrs.getAttributeIntValue("android", "orientation",
//				ORIENTATION_VERTICAL);
//		mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
//		mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
	}

	/*
	 * 获取控件的屏幕区域
	 */
	public Rect getRectOnScreen(View view) {
		Rect rect = new Rect();
		int[] location = new int[2];
		View parent = view;
		if (view.getParent() instanceof View) {
			parent = (View) view.getParent();
		}
		parent.getLocationOnScreen(location);
		view.getHitRect(rect);
		rect.offset(location[0], location[1]);
		return rect;
	}

	// 拦截触摸事件，用以修改事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		// 确定控件的屏幕区域
		int[] location = new int[2];
		int x = (int) event.getX();
		int y = (int) event.getY();
		this.getLocationOnScreen(location);
		x += location[0];
		y += location[1];
		// handle部分独立按钮，循环寻找非抽屉手柄的布局。
		if (mTouchableIds != -1) {
				View view = findViewById(mTouchableIds);
				Rect rect = getRectOnScreen(view);
				if (rect.contains(x, y)) {
					boolean result = view.dispatchTouchEvent(event);
					Log.i("MySlidingDrawer dispatchTouchEvent", "" + result);
					return false;
				}
		}

		// 抽屉行为控件，本想同上，写成数组，寻找多个手柄，但是这样就没有了抽屉拖动效果
		if (event.getAction() == MotionEvent.ACTION_DOWN && mHandleId != 0) {
			View view = findViewById(mHandleId);

			Log.i("MySlidingDrawer on touch", String.format("%d,%d", x, y));

			Rect rect = getRectOnScreen(view);

			Log.i("MySlidingDrawer handle screen rect", String
					.format("%d,%d %d,%d", rect.left, rect.top, rect.right,
							rect.bottom));
			if (rect.contains(x, y)) {// 点击抽屉控件时交由系统处理
				Log.i("【########################################】MySlidingDrawer", "Hit handle");
			} else {
				return false;
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	// 获取触屏事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

	/*
	private boolean mVertical;
	private int mTopOffset;

	  @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);  
	        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);  
	        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);  
	        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);  
	  
	        final View handle = getHandle();  
	        final View content = getContent();  
	        measureChild(handle, widthMeasureSpec, heightMeasureSpec);  
	  
	        if (mVertical) {  
	            int height = heightSpecSize - handle.getMeasuredHeight() - mTopOffset;  
	            content.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightSpecMode));  
	            heightSpecSize = handle.getMeasuredHeight() + mTopOffset + content.getMeasuredHeight();  
	            widthSpecSize = content.getMeasuredWidth();  
	            if (handle.getMeasuredWidth() > widthSpecSize) widthSpecSize = handle.getMeasuredWidth();  
	        }  
	        else {  
	            int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;  
	            getContent().measure(MeasureSpec.makeMeasureSpec(width, widthSpecMode), heightMeasureSpec);  
	            widthSpecSize = handle.getMeasuredWidth() + mTopOffset + content.getMeasuredWidth();  
	            heightSpecSize = content.getMeasuredHeight();  
	            if (handle.getMeasuredHeight() > heightSpecSize) heightSpecSize = handle.getMeasuredHeight();  
	        }  
	  
	        setMeasuredDimension(widthSpecSize, heightSpecSize);  
	    }  
	  */
//	  @Override
//	protected void onFinishInflate() {
//		  mHandle = findViewById(mHandleId);
//	        if (mHandle == null) {
//	            throw new IllegalArgumentException("The handle attribute is must refer to an"
//	                    + " existing child.");
//	        }
//	        mHandle.setOnClickListener(null);//---------------------------------------------就是这里。点击事件
////	 
////	        mContent = findViewById(mContentId);
////	        if (mContent == null) {
////	            throw new IllegalArgumentException("The content attribute is must refer to an" 
////	                    + " existing child.");
////	        }
////	        mContent.setVisibility(View.GONE);
//	}
}
