package com.webcloud.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

public class MapUtils {
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
    
    /** 
     * 返回公里。
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        
        double theta = lon1 - lon2;
        double dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double miles = dist * 60 * 1.1515;
        return miles * 1.609344;
    }
    
    //将角度转换为弧度
    private static double deg2rad(double degree) {
        return degree / 180 * Math.PI;
    }
    
    //将弧度转换为角度
    private static double rad2deg(double radian) {
        return radian * 180 / Math.PI;
    }
    
public static String SOSO_STATIC_PIC = "http://st.map.soso.com/api?";//size=604*300&center=116.39782,39.90611&zoom=16
    
    Context context;
    
    public static String getCityNameExcludeShi(String cityName){
        if(TextUtils.isEmpty(cityName)) return "长沙";
        if (cityName.lastIndexOf("市") == cityName.length() - 1) {
            cityName = cityName.substring(0,cityName.length() - 1);
        }
        return cityName;
    }
    
    public static String getAddressAfterShi(String address){
        if(TextUtils.isEmpty(address) || !address.contains("市") || address.lastIndexOf("市") == (address.length() - 1)) return address;
        address = address.substring(address.indexOf("市")+1);
        return address;
    }
    
    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }
    
    /**
     * 获取屏幕主题
     * 
     * @param v
     *            截取的视图
     * @param text
     *            在图片中显示的文本
     * @return
     */
    public static Bitmap getScreenShotBitmap(View v) {
        // 能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
}
