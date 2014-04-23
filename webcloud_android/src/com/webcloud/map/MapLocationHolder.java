package com.webcloud.map;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;

/**
 * 高德地图定位搜索相关接口总合。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface MapLocationHolder extends LocationSource,AMapLocationListener,OnGeocodeSearchListener,OnRouteSearchListener,OnPoiSearchListener{
    
}
