package com.webcloud;

import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

/**
 * 视图管理器，用于完全退出
 * @author  zoubangyue
 * @version  [版本号, 2013-10-14]
 */
public class ActivitysManager {
    private static final String TAG = "ActivitysManager";
    private static Stack<Activity> activityStack;
    
    private static ActivitysManager instance;
    
    private ActivitysManager() {
        
    }
    
    public static ActivitysManager getInstance() {
        if (instance == null) {
            instance = new ActivitysManager();
        }
        return instance;
    }
    
    /**
     * 回收堆栈中指定的activity
     * 
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }
    
    /** 回收堆栈，考虑finish的情况。
     * @param activity
     */
    public void popFinishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }
    
    /**
     * 将activity压入堆栈
     * 
     * @param activity
     *            需要压入堆栈的activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.push(activity);
    }
    
    /**
     * 回收堆栈中所有Activity
     */
    public void popAllActivity() {
        Activity activity = null;
        try {
            int count = activityStack.size();
            while (!activityStack.isEmpty()) {
                activity = activityStack.pop();
                //最后一个不finish
                if (1 != count)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isRunningForeground(Context context) {
        String packageName = getPackageName(context);
        String topActivityClassName = getTopActivityName(context);
        //Log.d(TAG,"packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            //Log.d(TAG,"---> isRunningForeGround");
            return true;
        } else {
            //Log.d(TAG,"---> isRunningBackGround");
            return false;
        }
    }
    
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        android.app.ActivityManager activityManager =
            (android.app.ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }
    
    public static String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }
    
    /** 
     * 获取本地，也就是本管理器中最顶端的activity名称。
     *
     * @return
     */
    public static Activity getLocalTopActivity(){
        if (activityStack != null && activityStack.size() > 0) {
            return activityStack.peek();
        }
        return null;
    }
}
