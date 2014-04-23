package com.webcloud;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.funlib.log.Log;
import com.webcloud.define.Global;

/**
 * 应用崩溃处理器，检查到应用崩溃，记录cnzz日志。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2013-9-24]
 */
public class CrashHandler implements UncaughtExceptionHandler {
    // 需求是 整个应用程序 只有一个 MyCrash-Handler 
    private static CrashHandler myCrashHandler;
    
    private Context context;
    
    private String deviceInfo;
    
    private String versionInfo;
    
    private CrashHandler() {
    	context = WebCloudApplication.getInstance();
    	this.deviceInfo = getDeviceInfo();
        this.versionInfo = getVersionInfo();
    }
    
	public static synchronized CrashHandler getInstance() {
		if (myCrashHandler == null) {
			myCrashHandler = new CrashHandler();
		}
		return myCrashHandler;
	}
    
    @Override
    public void uncaughtException(Thread th, Throwable e) {
        
        try {
        	Log.s(context, "CrashHandler", th, e, "崩溃异常");
        } catch (Exception e1) {
            e1.printStackTrace();
            e.printStackTrace();
        }
        //杀死应用并重启动应用
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    /**
     * 获取即将发送的异常信息。
     * 
     * @param tag
     * @param th
     * @param e
     * @param desc
     * @return
     */
    public static Map<String,String> getSendErrorInfo(String tag, Thread th, Throwable e, String desc){
    	CrashHandler crash = getInstance();
    	Map<String,String> errorMap = new HashMap<String,String>();
    	if(th != null)
    	    errorMap.put("线程名", th.getName());
    	errorMap.put("版本信息", crash.versionInfo);
    	errorMap.put("硬件信息", crash.deviceInfo);
    	errorMap.put("软件信息", getMobileInfo());
    	errorMap.put("标签", tag);
    	if(e != null)
    	    errorMap.put("错误信息", getErrorInfo(e));
    	errorMap.put("描述", desc);
    	return errorMap;
    }
    
    /**
     * 获取错误的信息 
     * @param arg1
     * @return
     */
    public static String getErrorInfo(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }
    
    /**
     * 获取手机的硬件信息 
     * @return
     */
    private String getDeviceInfo() {
        Map<String,String> di = new HashMap<String,String>();
        //通过反射获取系统的硬件信息 
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                //暴力反射 ,获取私有的信息 
                field.setAccessible(true);
                di.put(field.getName(), field.get(null).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return di.toString();
    }
    
    public static String getMobileInfo(){
    	Map<String,String> mobile = new HashMap<String,String>();
    	if (!TextUtils.isEmpty(Global.sLoginParam.imei)) {
			mobile.put("imei", Global.sLoginParam.imei);
		}
		if (!TextUtils.isEmpty(Global.sLoginParam.uid)) {
			mobile.put("uid", Global.sLoginParam.uid);
		}
		if (!TextUtils.isEmpty(Global.sLoginParam.mobile)) {
			mobile.put("mobile", Global.sLoginParam.mobile);
		}
		if (WebCloudApplication.isDebug) {
			String uuidRaw = UUID.randomUUID().toString();
			uuidRaw = uuidRaw.replaceAll("-", "");
			mobile.put("imsi", uuidRaw);
		} else {
			mobile.put("imsi", com.funlib.utily.Utily.getDeviceIMSI());
		}
    	return mobile.toString();
    }
    
    /**
     * 获取手机的版本信息
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }
}
