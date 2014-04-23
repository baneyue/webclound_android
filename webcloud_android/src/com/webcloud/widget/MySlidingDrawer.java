package com.webcloud.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SlidingDrawer;

/**
 * 如果slidingdrawer的属性：android:orientation="vertical"，
 * 将 childLeft = (width - childWidth) / 2;改成childLeft = l就可以左边
 * 如果slidingdrawer的属性：android:orientation="horizontal"，
 * 将 handleTop = (height - childHeight) / 2;;改成handleTop = t就可以上边
 * 如过让Handle在右边，下边 也是一样的方法。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-31]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class MySlidingDrawer extends SlidingDrawer {

    private int mHandleMarginLeft = 0;
    
    public int getmHandleMarginLeft() {
        return mHandleMarginLeft;
    }
    
    public void setmHandleMarginLeft(int mHandleMarginLeft) {
        this.mHandleMarginLeft = mHandleMarginLeft;
    }
    
    public MySlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //取得布局高度
        final int height = b - t;
        //取得布局宽度
        final int width = r - l;
        //取得手柄
        final View handle = this.getHandle();
        
        int childWidth = handle.getMeasuredWidth();
        int childHeight = handle.getMeasuredHeight();
        
        int childLeft;
        int childTop;
        
        final View content = this.getContent();
        
        //放置于右边
        childLeft = width - childWidth;
        //放置于底部
        childTop = this.isOpened()?0:(height-childHeight);
        
        if (this.isOpened()) {
            content.layout(0, this.getPaddingTop() + childHeight, content.getMeasuredWidth(), this.getPaddingTop()
                + childHeight + content.getMeasuredHeight());
        } else {
            content.layout(0,
                this.getPaddingTop() + childHeight + content.getMeasuredHeight(),
                content.getMeasuredWidth(),
                this.getPaddingTop() + childHeight + content.getMeasuredHeight());
        }
        handle.layout(childLeft, childTop, childLeft + childWidth, childTop+childHeight);
        
    }
    
    
    
}
