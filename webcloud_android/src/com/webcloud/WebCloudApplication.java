package com.webcloud;

import java.io.File;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.baidu.android.common.logging.Log;
import com.funlib.file.FileUtily;
import com.funlib.log.CrashLogCtrl;
import com.funlib.utily.ManifestUtil;
import com.funlib.utily.Utily;
import com.webcloud.define.Global;
import com.webcloud.define.ModelStyle;
import com.webcloud.imgcache.CacheManager;
import com.webcloud.manager.SystemInit;
import com.webcloud.model.LoginParam;

public class WebCloudApplication extends Application {
    
    public static boolean isDebug = false;
    
    private static WebCloudApplication sInstance = null;
    
    public static Bitmap sBitmapForEffect;
    
    public static String mLocationAddr;
    
    public static double mLocationLon;
    
    public static double mLocationLat;
    
    SharedPreferences spFirst = null;
    
    // 获取屏幕宽高
    public static int screenWidth = 0;
    
    public static int screenHeight = 0;
    
    public static float xdpi = 0;
    
    public static float ydpi = 0;
    
    public String getRandom() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(String.valueOf((int)(Math.random() * 9)));
        }
        return sb.toString();
    }
    
    public WebCloudApplication() {
        sInstance = this;
    }
    
    public static WebCloudApplication getInstance() {
        if (sInstance == null) {
            sInstance = new WebCloudApplication();
        }
        return sInstance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Object modelStyle = ManifestUtil.get(this, ModelStyle.MODELSTYLE);
        Log.d("WebCloudApplication", modelStyle+"");
        if(ModelStyle.MODEL_GRIDVIEW.equals(modelStyle)){
            ModelStyle.theme = R.style.AppGridTheme;
        } else if(ModelStyle.MODEL_SLIDEMENU.equals(modelStyle)){
            ModelStyle.theme = R.style.AppSlideTheme;
        } else if(ModelStyle.MODEL_METRO.equals(modelStyle)){
            ModelStyle.theme = R.style.AppMetroTheme;
        } else {
            ModelStyle.theme = R.style.AppTheme;
        }
            
        
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        screenWidth = display.widthPixels;
        screenHeight = display.heightPixels;
        xdpi = display.xdpi;
        ydpi = display.ydpi;
        
        isDebug = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        com.funlib.thread.ThreadPoolUtily.init();
        com.funlib.config.WorldSharedPreferences.init(this);
        com.funlib.utily.Utily.init(this);
        FileUtily.initAppSDPath(Global.DIR);
        
        Global.sLoginParam = new LoginParam();
        Global.sLoginParam.imei = Utily.getDeviceImei();
        Global.sLoginParam.imsi = Utily.getDeviceIMSI();
        Global.sLoginParam.msisdn = Utily.getDeviceMSISDN(this);
        
        Global.sSystemDCIMPath = Utily.getSystemDCIMPath(this);
        if (!TextUtils.isEmpty(Global.sSystemDCIMPath)) {
            Global.sSystemCameraPath = Global.sSystemDCIMPath + Global.CAMERA;
            Global.sLocalAlbumPath = Global.sSystemDCIMPath + Global.DIR + File.separator;
            FileUtily.mkDir(Global.sLocalAlbumPath);
        } else {
            
            Global.sSystemCameraPath = "";
            Global.sLocalAlbumPath = "";
            Global.sSystemDCIMPath = "";
            
            String appSDPath = FileUtily.getAppSDPath();
            if (appSDPath != null) {
                
                Global.sLocalAlbumPath = appSDPath + File.separator + "album" + File.separator;
                FileUtily.mkDir(Global.sLocalAlbumPath);
            }
        }
        
        SystemInit.getInstance().init();
        
        // 应用崩溃处理
        CrashHandler handler = CrashHandler.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        
        // 发送崩溃日志,如果之前应用有崩溃过
        CrashLogCtrl.sendCrashLog();
        
        CacheManager.init(this);
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        SystemInit.getInstance().destory();
        Log.d("application+++++++++++++++", "application 被回收");
    }
    
}
