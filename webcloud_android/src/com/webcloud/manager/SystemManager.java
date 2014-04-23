package com.webcloud.manager;

import android.content.Context;

import com.webcloud.WebCloudApplication;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.manager.client.UserManager;

/**
 * 管理集中器。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class SystemManager {
    public static Context context;
    
    private static SystemManager SYSTEM_MANAGER = null;
    
    public ImageCacheManager imgCacheMgr;
    public UserManager userMgr;
    
    /** <默认构造函数>*/
    private SystemManager(Context context) {
        SystemManager.context = context;
        imgCacheMgr = new ImageCacheManager(context);
        userMgr = new UserManager(context);
    }
    
    public static SystemManager getInstance(Context ctx) {
        ctx = WebCloudApplication.getInstance();
        if (null == SYSTEM_MANAGER) {
            SYSTEM_MANAGER = new SystemManager(ctx);
        } else {
            SystemManager.context = ctx;
        }
        return SYSTEM_MANAGER;
    }
    
    public static void setContext(Context ctx) {
        SystemManager.context = ctx;
    }
}
