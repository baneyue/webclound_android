package com.webcloud.utily;

import android.text.TextUtils;

/**
 * 正则表达式工具。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class RegexUtil {
    public static final String DOUBLE_REGX = "^+?[0-9]+(.[0-9]*)?$";//正实数匹配
    public static final String REAL_DOUBLE_REGX = "^[+-]?[0-9]+(.[0-9]*)?$";//实数匹配
    public static final String INT_REGX = "^+?[0-9]+$";//正整数匹配
    
    /** 
     * 判断是否是正实数。
     *
     * @param src
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isPostiveRealDigit(String src){
        if(TextUtils.isEmpty(src)) return false;
        return src.matches(DOUBLE_REGX);
    }
    
    /** 
     * 判断是否是正实数。
     *
     * @param src
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isRealDigit(String src){
        if(TextUtils.isEmpty(src)) return false;
        return src.matches(REAL_DOUBLE_REGX);
    }
    
    /** 
     * 判断是否是正整数。
     *
     * @param src
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isPositiveInt(String src){
        if(TextUtils.isEmpty(src)) return false;
        return src.matches(INT_REGX);
    }
}
