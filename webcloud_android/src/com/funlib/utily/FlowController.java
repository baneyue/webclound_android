package com.funlib.utily;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.funlib.config.WorldSharedPreferences;
import com.funlib.network.NetWork;
import com.webcloud.WebCloudApplication;

public class FlowController {
    
    private static String FLAG_UP_WIFI = "wifiupflowcount";
    
    private static String FLAG_UP_3G = "3gupflowcount";
    
    private static String FLAG_DOWN_WIFI = "wifidownflowcount";
    
    private static String FLAG_DOWN_3G = "3gdownflowcount";
    
    public static int FLOW_UP = 0;// 上行流量
    
    public static int FLOW_DOWN = 1;// 下行流量
    
    private final static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    private final static Lock read = readWriteLock.readLock();
    
    private final static Lock write = readWriteLock.writeLock();
    
    public static void reset() {
        
        WorldSharedPreferences.saveLong(FLAG_UP_WIFI, 0);
        WorldSharedPreferences.saveLong(FLAG_UP_3G, 0);
        WorldSharedPreferences.saveLong(FLAG_DOWN_WIFI, 0);
        WorldSharedPreferences.saveLong(FLAG_DOWN_3G, 0);
    }
    
    /** 
     * 记录流量使用情况。
     *
     * @param type
     * @param count
     */
    public static void count(int type, long count) {
        
        write.lock();
        
        if (NetWork.isDefaultWifi(WebCloudApplication.getInstance())) {
            
            if (type == FLOW_UP) {
                
                long tmpCnt = WorldSharedPreferences.getLong(FLAG_UP_WIFI);
                count += tmpCnt;
                WorldSharedPreferences.saveLong(FLAG_UP_WIFI, count);
            } else if (type == FLOW_DOWN) {
                
                long tmpCnt = WorldSharedPreferences.getLong(FLAG_DOWN_WIFI);
                count += tmpCnt;
                WorldSharedPreferences.saveLong(FLAG_DOWN_WIFI, count);
            }
            
        } else {
            
            if (type == FLOW_UP) {
                
                long tmpCnt = WorldSharedPreferences.getLong(FLAG_UP_3G);
                count += tmpCnt;
                WorldSharedPreferences.saveLong(FLAG_UP_3G, count);
            } else if (type == FLOW_DOWN) {
                
                long tmpCnt = WorldSharedPreferences.getLong(FLAG_DOWN_3G);
                count += tmpCnt;
                WorldSharedPreferences.saveLong(FLAG_DOWN_3G, count);
            }
        }
        
        write.unlock();
    }
    
    public static void sendCount() {
        
        read.lock();
        
        long tgUpflow = WorldSharedPreferences.getLong(FLAG_UP_3G);
        long wifiUpflow = WorldSharedPreferences.getLong(FLAG_UP_WIFI);
        long tgDownflow = WorldSharedPreferences.getLong(FLAG_DOWN_3G);
        long wifiDownflow = WorldSharedPreferences.getLong(FLAG_DOWN_WIFI);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("ctUpFlow", String.valueOf(tgUpflow));
        params.put("wifiUpFlow", String.valueOf(wifiUpflow));
        params.put("ctDownFlow", String.valueOf(tgDownflow));
        params.put("wifiDownFlow", String.valueOf(wifiDownflow));
       /* new Requester(CTCloudApplication.getInstance()).request(new RequestListener() {
            
            @Override
            public void requestStatusChanged(int statusCode, HttpUrlImpl requestId, String responseString) {
                //流量统计成功，重置客户端统计
                if(statusCode == RequestStatus.SUCCESS)
                    FlowController.reset();
                JsonFriend<String> jf = new JsonFriend<String>(String.class);
                String str = jf.parseObject(responseString);
                
                
            }
        }, HttpUrlImpl.V1.THEMES, Utily.getWholeUrl(HttpUrlImpl.V1.THEMES), params);*/
        
        read.unlock();
    }
    
}
