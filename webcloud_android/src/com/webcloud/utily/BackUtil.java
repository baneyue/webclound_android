package com.webcloud.utily;

import android.app.Activity;

import com.webcloud.ActivitysManager;
import com.webcloud.R;
import com.webcloud.component.NewToast;

public class BackUtil {
    
    public static void exit(Activity context) {
        ActivitysManager.getInstance().popAllActivity();
        context.overridePendingTransition(0, R.anim.scale_out);
        firstTime = 0;
    }
    
    private static long firstTime;
    
    /** 
     * 
     * 连续两次点击退出应用。
     * 
     * @return true退出，否则不退出
     */
    public boolean doubleClickBackExit(Activity context) {
        //监测主页的onBack事件，提示连续点击两下的时候触发退出事件
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 1000) {//如果两次按键时间间隔大于800毫秒，则不退出 
            NewToast.show(context,"再按一次退出程序…");
            firstTime = secondTime;//更新firstTime
            return false;
        } else {
            exit(context);
            return true;
        }
    }
}
