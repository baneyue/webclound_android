package com.webcloud.map;

import android.os.Bundle;

import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CustomRenderer;

/**
 * 高德地图MapView生命周期接口。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface MapViewHolder extends OnCameraChangeListener,CustomRenderer,OnMapLoadedListener{
    /** 
     * 高德要求必须重写。
     * 在activity里面的onCreate方法中调用
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState);
    /** 
     * 高德要求必须重新。
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState);
    
    /** 
     * 高德要求必须重新。
     *
     */
    public void onResume();

    /** 
     * 高德要求必须重新。
     *
     */
    public void onPause();
    /** 
     * 高德要求必须重新。
     *
     */
    public void onDestroy();
}
