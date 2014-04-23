package com.webcloud.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.webcloud.R;
import com.webcloud.utily.Utily;

public class LoadingPage {
    Activity context;
    
    WindowManager winMgr;
    
    LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
        LayoutParams.TYPE_APPLICATION, LayoutParams.FLAG_LAYOUT_NO_LIMITS | LayoutParams.FLAG_LAYOUT_IN_SCREEN
            | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.RGBA_8888);
    
    ImageView iv;
    
    public LoadingPage(Activity context, int resId) {
        lp.alpha=0.9f;
		lp.windowAnimations = R.style.loading_page_anim;
        winMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        iv = new ImageView(context);
        iv.setImageBitmap(Utily.readBitMap(context, resId));
        iv.setScaleType(ScaleType.CENTER_CROP);
        winMgr.addView(iv, lp);
        
        //提示框处理器，异步的来显示提示窗口
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            	
                winMgr.removeView(iv);
                iv = null;
            }
        },800);
    }
    
    /**弹出窗口handler*/
    Handler mHandler = new Handler();
    
    
    
    public void onDestory() {
        try {
            if(iv != null) winMgr.removeView(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
