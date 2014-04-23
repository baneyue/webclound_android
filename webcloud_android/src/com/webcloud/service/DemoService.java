package com.webcloud.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.webcloud.ActivitysManager;
import com.webcloud.DemoActivity;

public class DemoService extends Service {
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        checkHandler.postDelayed(r, 1000);
    }
    
    /**应用检查*/
    Handler checkHandler = new Handler();
    
    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, DemoService.class);
        //i.setAction(ACTION_START);
        i.setFlags(Service.START_STICKY);
        ctx.startService(i);
    }
    
    Runnable r = new Runnable() {
        
        @Override
        public void run() {
          //应用后台运行，关闭服务
            boolean currForeground = ActivitysManager.isRunningForeground(getApplicationContext());
            //当前前台，但是是后台定位，修改为前台定位
            if (currForeground) {
            } else if (!currForeground) {
                Intent it = new Intent(getApplicationContext(),DemoActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                getApplicationContext().startActivity(it);
            }
            //循环调用
            checkHandler.postDelayed(this, 30 * 1000);
        }
    };
}
