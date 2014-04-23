package com.webcloud.manager;

import com.funlib.log.Log;
import com.webcloud.WebCloudApplication;
import com.webcloud.service.LocationService;

/**
 * 系统初始化模块。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class SystemInit {
    private static final String TAG = "SystemInit";
    
    private static SystemInit instance;
    
    WebCloudApplication app = WebCloudApplication.getInstance();
    
    private SystemInit() {
    }
    
    public static SystemInit getInstance(){
        if(instance == null){
            instance = new SystemInit();
        }
        return instance;
    }
    
    /**
     * 应用启动初始化资源。
     */
    public void init() {
        Log.d(TAG, "----------init----------");
        Log.d(TAG, "----------启动全局服务----------");
        LocationService.actionStart(app);
        Log.d(TAG, "----------初始化管理器----------");
        SystemManager.getInstance(app);
        //DemoService.actionStart(app);
    }
    
    /** 
     * 销毁资源
     */
    public void destory(){
        Log.d(TAG, "----------destory----------");
    }
}
