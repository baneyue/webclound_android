package com.webcloud.map;

import com.webcloud.model.GaoDeAddress;

/**
 * 定位接收器接口。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-11]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface MapLocationRecevier {
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
}
