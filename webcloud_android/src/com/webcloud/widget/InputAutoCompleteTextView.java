package com.webcloud.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * 总是满足过滤条件，使得用户未输入内容时，也显示用户名提示
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-6]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class InputAutoCompleteTextView extends AutoCompleteTextView {

    public InputAutoCompleteTextView(Context context) {
        super(context);
    }

    public InputAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InputAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    //总是满足过滤条件，使得用户未输入内容时，也显示用户名提示
    @Override
    public boolean enoughToFilter(){
        return true;
    }

    public void temp(){
        performFiltering(getText().toString(), 0);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        super.performFiltering(text, keyCode);
    }
}
