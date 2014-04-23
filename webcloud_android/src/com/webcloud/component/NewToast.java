package com.webcloud.component;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.webcloud.R;

/**
 * 全新吐司，实现自定义显示时长，或者触摸外界消失。
 * 
 * 使用PopupWindow，依赖当前上下文，在activity上下文中可以使用。
 * 注意使用时注意activity需要继承自BaseActivity或其扩展类，否则使用了NewToast，在活动退出时需要手动调用release(),防止窗口溢出。
 * 
 * @author  邹邦跃
 * @version  [版本号, 2013-4-22]
 */
public class NewToast {
    private static String TAG = "NewToast";
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1 && popWindow != null && popWindow.isShowing()){
                try {
                    popWindow.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    };
    
    private ViewHolder vh;
    
    private View toastView;
    
    private PopupWindow popWindow;
    
    private static NewToast toast;
    
    private Context context;

    private NewToast(Context context) {
        this.context = context;
    }
    
    /** 
     * 获取新吐司实例。
     *
     * @param context
     * @return
     */
    private static NewToast getInstance(Context context) {
        if(toast != null && toast.context.equals(context)){
            Log.d(TAG, "上下文对象一致："+context.getClass().getSimpleName());
            return toast;
        }else{
            //把老得释放掉
            if(toast != null) NewToast.release();
            
            Log.d(TAG, "上下文对象不一致："+context.getClass().getSimpleName());
            toast = new NewToast(context);
        }
        return toast;
    }
    
    private View getToastView() {
        if ((toastView != null)) {
            vh = (ViewHolder)toastView.getTag();
        } else {
            vh = new ViewHolder();
            toastView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comm_new_toast, null);
            vh.tvMsg = (TextView)toastView.findViewById(R.id.tvMsg);
            // 创建PopupWindow对象，并在指定位置弹出用于显示菜单的窗口 
            popWindow =
                new PopupWindow(toastView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //popWindow.setFocusable(false);
            toastView.setTag(vh);
            popWindow.setOutsideTouchable(true);
            popWindow.setTouchInterceptor(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {  
                        popWindow.dismiss();
                        return true;
                    } 
                    return false;
                }
            });
            //设置顺序才能 在外部点击消失,设置菜单背景
            popWindow.setBackgroundDrawable(new BitmapDrawable());
            popWindow.setAnimationStyle(R.style.toast_pop_anim);
            //popWindow.showAtLocation(menuView, Gravity.BOTTOM, 0, 0);
        }
        return toastView;
    }
    
    /**
     * 控制列表item，中控件焦点问题的好办法。
     */
    static class ViewHolder {
        TextView tvMsg;
    }
    
    /** 
     * 控制弹出框的，显示或者隐藏。
     * 
     * @param msg 消息内容
     * @param second 几秒，超过8秒，重置为8秒，超过8秒，重置为8秒
     */
    private void show(String msg,int second) {
        if(toastView == null){
            getToastView();
        }
        if(TextUtils.isEmpty(msg)) return;
        //超过8秒，重置为8秒
        if(second > 8) second = 8;
        //超过8秒，重置为8秒
        if(second < 3) second = 3;
        if ((toastView != null && popWindow.isShowing())) {
            vh.tvMsg.setText(msg);
        } else {
            //当前未显示，设置显示
            getToastView();
            popWindow.showAtLocation(toastView, Gravity.CENTER, 0, 300);
            vh.tvMsg.setText(msg);
        }
        //几秒后调用触发toast消失
        handler.sendEmptyMessageDelayed(1, second*1000);
    }
    
    /** 
     * 控制弹出框的，不倒计时消失。
     * 
     * @param msg 消息内容
     * 
     */
    private void show(String msg) {
        if(toastView == null){
            getToastView();
        }
        if(TextUtils.isEmpty(msg)) return;
        if ((toastView != null && popWindow.isShowing())) {
            vh.tvMsg.setText(msg);
        } else {
            //当前未显示，设置显示
            getToastView();
            popWindow.showAtLocation(toastView, Gravity.CENTER, 0, 300);
            vh.tvMsg.setText(msg);
        }
    }
    
    /** 
     * 弹出消息，不倒计时消失。
     * 
     * @param msg 消息内容
     * 
     */
    public static void show(Context context,String msg,boolean unhidden){
        if(context == null || !isActivityRunning(context)) return;
        NewToast t = getInstance(context);
        try {
            if(unhidden){
                t.show(msg);
            } else {
                t.show(msg,3);
            }
        } catch (Exception e) {
            toast = null;
            e.printStackTrace();
        }
    }
    
    /** 
     * 弹出消息，倒计时3秒消失消失。
     * 
     * @param msg 消息内容
     * 
     */
    public static void show(Context context,String msg){
        if(context == null || !isActivityRunning(context)) return;
        NewToast t = getInstance(context);
        try {
            t.show(msg,3);
        } catch (Exception e) {
            toast = null;
            e.printStackTrace();
        }
    }
    
    /** 
     * 弹出消息，倒计时second秒消失,默认3秒消失，超过8秒，会取8秒。
     * 
     * @param msg 消息内容
     * 
     */
    public static void show(Context context,String msg,int second){
        if(context == null || !isActivityRunning(context)) return;
        NewToast t = getInstance(context);
        try {
            t.show(msg,second);
        } catch (Exception e) {
            toast = null;
            e.printStackTrace();
        }
    }
    
    private static boolean isActivityRunning(Context context) {
        ComponentName comp = null;
        boolean insofAct = context instanceof Activity;
        if (insofAct) {
            Activity act = (Activity)context;
            if (act.isChild()) {
                act = act.getParent();
            }
            comp = act.getComponentName();
        }
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            if (tasksInfo.get(0).topActivity.equals(comp)) {
                return true;
            }
            //Log.d(TAG, packageName + "," +tasksInfo.get(0).topActivity.getClassName());
        }
        return false;
    }
    
    /**
     * 活动退出必须释放。
     */
    public static void release(){
        if(toast != null){
            try {
                if(toast.popWindow != null && toast.popWindow.isShowing()){
                    toast.popWindow.dismiss();
                }
                toast = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
