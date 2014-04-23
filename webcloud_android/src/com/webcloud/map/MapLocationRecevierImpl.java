package com.webcloud.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.baidu.android.common.logging.Log;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.service.LocationService;

/**
 * 定位接收器，实现类。
 * 简单使用方法：
 * 1.在四大组件中，使用Context初始化该组件，然后实现其两个接口。
 * 2.在活动的OnCreate方法中调用registeReceiver
 * 3.在活动的在onDestory中调用，或者不需要该接收器的时候调用,一旦注册过广播，最终就必须调用此处。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-11]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class MapLocationRecevierImpl implements MapLocationRecevier {
    Context mContext;
    
    private static final String TAG = "MapLocationRecevierImpl";
    
    protected MapLocationRecevierImpl(Context context) {
        this.mContext = context;
    }
    
    /** 
     * 广播接收到定位位置。
     *
     * @param gdAddress
     */
    public abstract void onReceiveAddress(GaoDeAddress gdAddress);
    
    /** 
     * 广播接收到定位位置。
     *
     * @param iLatitu
     * @param iLongti
     */
    public abstract void onReceiveLocation(double iLatitu, double iLongti);
    
    //注册广播接收器，接收来自定位的通知
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            String action = intent.getAction();
            if (LocationService.BroadcastAction.ADDRESS.equals(action)) {
                GaoDeAddress gdAddress = intent.getParcelableExtra(LocationService.Config.GAODE_ADDRESS);
                if (gdAddress != null) {
                    onReceiveAddress(gdAddress);
                }
            } else if (LocationService.BroadcastAction.COORDINATE.equals(action)) {
                
                try {
                    double iLatitu = intent.getDoubleExtra(LocationService.Config.LATITUDE, 0);
                    double iLongti = intent.getDoubleExtra(LocationService.Config.LONGTITUDE, 0);
                    
                    onReceiveLocation(iLatitu, iLongti);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    
    /** 
     * 在onCreate中调用。
     *
     * @see [类、类#方法、类#成员]
     */
    public void registeReceiver() {
        IntentFilter filter = new IntentFilter(LocationService.BroadcastAction.ADDRESS);
        mContext.registerReceiver(mReceiver, filter);
        
        filter = new IntentFilter(LocationService.BroadcastAction.COORDINATE);
        mContext.registerReceiver(mReceiver, filter);
        
        LocationService.actionStart(mContext);
    }
    
    /** 
     * 在onDestory中调用，或者不需要该接收器的时候调用。
     * 这个是必须的。
     *
     * @see [类、类#方法、类#成员]
     */
    public void unregisteReceiver() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e(TAG, "未进行广播注册，此异常无妨。^-^");
            //e.printStackTrace();
        }
    }
}
