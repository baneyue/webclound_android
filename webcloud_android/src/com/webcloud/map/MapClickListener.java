package com.webcloud.map;

import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;

/**
 * 高德地图相关点击接口总合。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface MapClickListener extends InfoWindowAdapter,OnInfoWindowClickListener,OnMapLongClickListener,OnMapClickListener,OnMarkerClickListener{
    
}
