package com.webcloud.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.funlib.log.Log;
import com.webcloud.R;

public class MapScrollView extends ScrollView {
    private static final String TAG = "MapScrollView";
    View mvMap;
    
    public MapScrollView(Context context) {
        super(context);
        initView();
    }
    
    public MapScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    public MapScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    private void initView(){
        mvMap = findViewById(R.id.mvMap);
    }
    
    /**
     * 当Action_Down事件触发在MapView区域，那么ScrollView就不处理交给子view处理。
     * 
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        int a = action & MotionEvent.ACTION_MASK;
        if (a == MotionEvent.ACTION_DOWN) {
            final float x = ev.getX();
            final float y = ev.getY();
            if (inMap((int)x, (int)y)) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
    
    public boolean inMap(int x,int y){
        Rect r = new Rect();
        mvMap.getGlobalVisibleRect(r);
        if(r.contains(x, y)){
            return true;
        }
        Log.d(TAG, r.toString());
        mvMap.getLocalVisibleRect(r);
        if(r.contains(x, y)){
            return true;
        }
        Log.d(TAG, r.toString());
        return false;
    }
}
