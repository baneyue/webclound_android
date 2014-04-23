package com.funlib.log;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.webcloud.CrashHandler;
import com.webcloud.WebCloudApplication;

/**
 * 日志打印，封装安卓Log。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2013-8-29]
 */
public class Log {
    public static boolean D;
    
    public static boolean E;
    
    public static boolean I;
    
    public static boolean V;
    
    public static boolean W;
    
    private static final String defaultTag = "LBS";
    
    private static boolean printLog;
    
    static {
        printLog = Boolean.parseBoolean(Configuration.getProperty("printLog", "false"));
        if (printLog) {
            D = Boolean.parseBoolean(Configuration.getProperty("debugLog", "false"));
            V = Boolean.parseBoolean(Configuration.getProperty("viewLog", "false"));
            I = Boolean.parseBoolean(Configuration.getProperty("infoLog", "false"));
            E = Boolean.parseBoolean(Configuration.getProperty("errorLog", "false"));
            W = Boolean.parseBoolean(Configuration.getProperty("warnLog", "false"));
        } else {
            D = false;
            V = false;
            I = false;
            E = false;
            W = false;
        }
    }
    
    /** 
     * 获取当前方法栈信息。
     * 
     * @return
     */
    private static StackTraceElement getCurrentStackTraceElement() {
        StackTraceElement[] stacks =  Thread.currentThread().getStackTrace();
        return stacks[5];
    }
    
    /** 
     * 返回当前运行期方法名和行号。
     * 
     * @param trace
     * @return
     */
    private static String getMethodName(StackTraceElement trace) {
        return String.format("%s>>%s>>%s", trace.getFileName(), trace.getMethodName(), trace.getLineNumber());
    }
    
    private static String getMsg(String msg) {
        return "[" + getMethodName(getCurrentStackTraceElement()) + "]>>>>[" + msg + "]";
    }
    
    public static void d(String tag, String info) {
        if (!D)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        android.util.Log.d(tag, getMsg(info));
    }
    
    public static void d(String tag, String info, Throwable e) {
        if (!D)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        android.util.Log.d(tag, getMsg(info), e);
    }
    
    public static void e(String tag, String info) {
        if (!E)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.e(tag, getMsg(info));
    }
    
    public static void e(String tag, String info, Throwable e) {
        if (!E)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.e(tag, getMsg(info), e);
    }
    
    public static void i(String tag, String info) {
        if (!I)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.i(tag, getMsg(info));
    }
    
    public static void i(String tag, String info, Throwable e) {
        if (!I)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.i(tag, getMsg(info), e);
    }
    
    public static void v(String tag, String info) {
        if (!V)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.v(tag, getMsg(info));
    }
    
    public static void v(String tag, String info, Throwable e) {
        if (!V)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.v(tag, getMsg(info), e);
    }
    
    public static void w(String tag, String info) {
        if (!W)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.w(tag, getMsg(info));
    }
    
    public static void w(String tag, String info, Throwable e) {
        if (!W)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.w(tag, getMsg(info), e);
    }
    
    public static void w(String tag, Throwable e) {
        if (!W)
            return;
        tag = !TextUtils.isEmpty(tag) ? tag : defaultTag;
        
        android.util.Log.w(tag, e);
    }
    
    /** 
     * 记录异常信息。
     * 非常有用，记录少量的异常信息可以达到追踪程序bug的效果，大量发送异常比较耗费客户端网络流量。
     * @param ctx 上下文
     * @param tag 
     * @param th
     * @param e
     * @param desc 一句话描述
     */
    public static void s(Context ctx, String tag, Thread th, Throwable e, String desc) {
        if (ctx == null)
            ctx = WebCloudApplication.getInstance();
        Map<String, String> errorMap = CrashHandler.getSendErrorInfo(tag, th, e, desc);
        String log = errorMap.toString();
        //MobileProbe.onError(ctx, log);
        //记录崩溃日志
        SharedPreferences sp =
            WebCloudApplication.getInstance().getSharedPreferences(CrashLogCtrl.CRASH_LOG,
                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        Editor ed = sp.edit();
        ed.putString(String.valueOf(System.currentTimeMillis()), JSON.toJSONString(errorMap));
        ed.commit();
        Log.e(defaultTag, getMsg(log), e);
    }
}
