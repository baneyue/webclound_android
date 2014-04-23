/**
 * 
 */

package com.webcloud.map.street;

import android.graphics.Bitmap;

import com.amap.api.services.core.LatLonPoint;
import com.tencent.mapapi.map.GeoPoint;
import com.webcloud.model.PoiInfo;

/**
 * 将创建poi overlay所需数据封装 
 * 
 */
public class StreetPoiData {
    public PoiInfo poiInfo;
    public String poiName;
    public GeoPoint poi;

    /**
     * 纬度的10E6
     */
    public int latE6;

    /**
     * 经度的10E6
     */
    public int lonE6;
    
    /**
     * poi点显示的图片
     */
    public Bitmap marker;
    
    /**
     * poi被点击时显示的图片
     */
    public Bitmap markerPressed;
    
    /**
     * 高度偏移量
     */
    public float heightOffset;
    
    public String uid;

    public StreetPoiData(int x, int y) {
        this(x, y, null, null, 0);
    }
    
    public StreetPoiData(int x, int y, Bitmap marker) {
    	this(x, y, marker, null, 0);
    }
    
	public StreetPoiData(int x, int y, Bitmap marker, Bitmap markerPressed, float offset) {
		this.latE6 = x;
		this.lonE6 = y;
		this.marker = marker;
		this.markerPressed = markerPressed;
		this.heightOffset = offset;
		this.uid = "";
	}
	
	public StreetPoiData(GeoPoint poi,String poiName,Bitmap marker, Bitmap markerPressed, float offset) {
	    this.poi = poi;
	    this.poiName = poiName;
	    this.latE6 = poi.getLatitudeE6();
	    this.lonE6 = poi.getLongitudeE6();
	    this.marker = marker;
	    this.markerPressed = markerPressed;
	    this.heightOffset = offset;
	    this.uid = "";
	}
	
	public StreetPoiData(PoiInfo poiInfo,Bitmap marker, Bitmap markerPressed, float offset) {
	    this.poiInfo = poiInfo;
	    if(poiInfo != null){
	        this.poiName = poiInfo.getName();
	        LatLonPoint pt = poiInfo.getPoint();
	        if(pt != null){
	            this.latE6 = (int)(pt.getLatitude()*1e6);
	            this.lonE6 = (int)(pt.getLongitude()*1e6);
	        }
	    }
	    this.marker = marker;
	    this.markerPressed = markerPressed;
	    this.heightOffset = offset;
	    this.uid = "";
	}
	
	public void updateMarker(Bitmap bitmap, String uid) {
		this.marker = bitmap;
		this.uid = uid;
	}

}
