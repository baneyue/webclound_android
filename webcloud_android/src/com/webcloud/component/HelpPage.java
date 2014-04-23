package com.webcloud.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.webcloud.utily.Utily;

public class HelpPage {
	Activity mcontext;

	WindowManager winMgr;

	LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
			LayoutParams.FILL_PARENT, LayoutParams.TYPE_APPLICATION,
			LayoutParams.FLAG_LAYOUT_NO_LIMITS
					| LayoutParams.FLAG_LAYOUT_IN_SCREEN
					| LayoutParams.FLAG_ALT_FOCUSABLE_IM
					| LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING,
			PixelFormat.RGBA_8888);

	ImageView iv,iv2;

	public HelpPage(Activity context, int resId,int resId2) {
		lp.alpha = 0.9f;
		// lp.windowAnimations = R.style.loading_page_anim;
		winMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		this.mcontext = context;
		iv = new ImageView(context);
		iv.setImageBitmap(Utily.readBitMap(context, resId));
		iv.setScaleType(ScaleType.FIT_XY);

		if (resId2 != 0) {
			iv2 = new ImageView(context);
			iv2.setImageBitmap(Utily.readBitMap(context, resId2));
			iv2.setScaleType(ScaleType.FIT_XY);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (iv != null) {
						winMgr.removeView(iv);
					}
					iv = null;
					winMgr.addView(iv2, lp);
				}
			});
			iv2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (iv2 != null) {
						winMgr.removeView(iv2);
					}
					iv2 = null;
				}
			});
		} else {
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (iv != null) {
						winMgr.removeView(iv);
					}
					iv = null;
				}
			});
		}

		winMgr.addView(iv, lp);

		/*
		 * //提示框处理器，异步的来显示提示窗口 mHandler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * winMgr.removeView(iv); iv = null; } },1500);
		 */
	}

	/** 弹出窗口handler */
	Handler mHandler = new Handler();

	public void onDestory() {
		try {
			if (iv != null)
				winMgr.removeView(iv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
