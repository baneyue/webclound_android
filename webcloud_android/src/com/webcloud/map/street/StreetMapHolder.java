package com.webcloud.map.street;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.funlib.json.JsonFriend;
import com.tencent.street.StreetViewListener;
import com.tencent.street.StreetViewShow;
import com.tencent.street.overlay.ItemizedOverlay;
import com.webcloud.R;
import com.webcloud.component.NewToast;
import com.webcloud.define.BusiPoiType;
import com.webcloud.map.MapUtils;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.model.PoiInfo;
import com.webcloud.service.LocationService;

/**
 * 商家实时街景处理器。
 * 实现如下效果：
 * 1.实现从当前位置到目标位置的导航，在导航坐标点上显示到该位置名称和距离，点击是该图层边框变绿色
 * 2.点击坐标点图层后，导航到该位置，并弹出一个popupWindow展示该坐标商业信息
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StreetMapHolder implements StreetViewListener, OnClickStreetOverlayListener {
    public static final String TAG = "ShopStreetViewHolder";
    
    public SharedPreferences mLocationPrefs;
    
    /**街道坐标数组*/
    private ArrayList<StreetPoiData> pois = new ArrayList<StreetPoiData>();
    
    private Activity context;
    
    public LatLng mCurrPoint;
    public PoiInfo mCenter;
    
    String mCityName;
    
    GaoDeAddress mAddress;
    
    public StreetMapHolder(Activity context) {
        this.context = context;
        mLocationPrefs = context.getSharedPreferences(LocationService.TAG, Context.MODE_PRIVATE);
        initView();
        initHistoryLocationData();
    }
    
    /** 
     * 解析历史定位数据。
     *
     */
    private void initHistoryLocationData() {
        //初始化当前位置和城市名称
        try {
            double latitude = Double.valueOf(mLocationPrefs.getString(LocationService.Config.LATITUDE, "0"));
            double longtitude = Double.valueOf(mLocationPrefs.getString(LocationService.Config.LONGTITUDE, "0"));
            mCurrPoint = new LatLng(latitude, longtitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String gdAddressStr = mLocationPrefs.getString(LocationService.Config.GAODE_ADDRESS, null);
        //这个地址bean中的address是：省+城市名+区名+poiname
        //城市名包含市
        JsonFriend<GaoDeAddress> gdJs = new JsonFriend<GaoDeAddress>(GaoDeAddress.class);
        GaoDeAddress gdAddress = gdJs.parseObject(gdAddressStr);
        if (gdAddress != null) {
            mAddress = gdAddress;
            mCityName = mAddress.getCityname();
        }
    }
    
    int positionColor = 0;
    
    int distanceColor = 0;
    
    int textSize = 0;
    
    int bgColor = 0;
    
    int borderColor = 0;
    
    int alpha = (int)(0.6 * 255);
    
    ViewHolder vh;
    
    static class ViewHolder {
        View layRealMap;//街景地图布局,父布局
        
        /**街景界面的抽屉视图，可以上下拉*/
        SlidingDrawer sdMenu;
        View sdContent;
        
        /**抽屉视图的handle*/
        Button btnSdHandle;
        
        /**放置街景的父视图*/
        LinearLayout layReal;
        
        /**街景的temp视图*/
        View mStreetView;
        
        /**街道坐标图层*/
        StreetOverlay overlay;
        
    }
    
    public void initView() {
        if (vh == null) {
            vh = new ViewHolder();
            vh.layRealMap = context.findViewById(R.id.layRealMap);
            vh.layReal = (LinearLayout)vh.layRealMap.findViewById(R.id.layReal);
            
            //初始化街景图层资源
            Resources rs = context.getResources();
            positionColor = rs.getColor(R.color.font_white);
            distanceColor = rs.getColor(R.color.font_green);
            bgColor = rs.getColor(R.color.bg2e2e2e_noalpha);
            borderColor = rs.getColor(R.color.font_green);
            textSize = rs.getDimensionPixelSize(R.dimen.text_size_small);
        }
        
    }
    
    public void showStreetMap(PoiInfo poi) {
        this.mCenter = poi;
        if (poi == null)
            return;
        TextView tvTitle = (TextView)context.findViewById(R.id.tvTitle);
        tvTitle.setText(poi.getName());
        this.refreshPois();
        if(TextUtils.isEmpty(poi.panoid)){
            com.tencent.street.map.basemap.GeoPoint streetGeo =
                new com.tencent.street.map.basemap.GeoPoint((int)(poi.getPoint().getLatitude()*1e6), (int)(poi.getPoint().getLongitude()*1e6));
            StreetViewShow.getInstance().showStreetView(context,
                streetGeo,
                200,
                this,
                poi.heading,
                poi.pitch,
                context.getResources().getString(R.string.soso_key));
        }else{
            StreetViewShow.getInstance().showStreetView(context,
            poi.panoid,
            this,
            poi.heading,
            poi.pitch,
            context.getResources().getString(R.string.soso_key));
        }
    }
    
    @Override
    public void onViewReturn(final View v) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vh.mStreetView = v;
                vh.layReal.removeAllViews();
                vh.layReal.addView(vh.mStreetView);
            }
        });
    }
    
    
    
    @Override
    public void onNetError() {
        Log.d(TAG, "onNetError");
        NewToast.show(context, "Sorry 请确保您的网络畅通 ");
    }
    
    @Override
    public void onDataError() {
        NewToast.show(context, "Sorry 该地区未找到街景 ");
        Log.d(TAG, "onDataError");
    }
    
    @Override
    public ItemizedOverlay getOverlay() {
        Log.d(TAG, "调用getOverlay，每次移动坐标位时调用一次，我们需要在此更新pois即可");
        if (vh.overlay == null) {
            vh.overlay = new StreetOverlay(pois, this);
        }
        vh.overlay.populate();
        return vh.overlay;
    }
    
    private Bitmap getBm(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inScaled = false;
        
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
    
    @Override
    public void onLoaded() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vh.mStreetView.setVisibility(View.VISIBLE);
            }
        });
    }
    
    
    @Override
    public void onAuthFail() {
        NewToast.show(context, "授权失败，请检查soso key");
        Log.d(TAG, "onAuthFail");
    }
    
    public void release() {
        StreetViewShow.getInstance().destory();
    }
    
    @Override
    public void onClick(StreetPoiData poi) {
      //判断坐标是否是当前街景展示点，如果是则弹出popupwindow展示当前点信息，否则街景导航到点击点坐标位置
        if (poi == null)
            return;
        
        double distance =
            MapUtils.distance(poi.latE6/1e6,poi.lonE6/1e6,mCenter.getPoint().getLatitude(),mCenter.getPoint().getLongitude());
        
        Log.d(TAG, String.valueOf(distance));
        
        if (Double.isNaN(distance) || distance <= 50) {
            //弹出显示层
            //MapSwitchPopupWindow.show(context, "弹出显示层");
        } else {
            //定位到目标点
            StreetViewShow.getInstance().destory();
            vh.layReal.removeAllViews();
            //切换当前点和目标点
            this.showStreetMap(poi.poiInfo);
            Log.d(TAG, poi.poi + poi.poiName);
        }
        //vh.sdMenu.animateOpen();
    }
    
    List<PoiInfo> poiList;
    public void setPoiList(List<PoiInfo> poiList){
        this.poiList = poiList;
    }
    
    /** 
     * 解析数据，解析成街景使用的坐标数据。
     *
     * @param poi
     */
    public void refreshPois() {
        pois.clear();
        //记录poi,点击地图时用到
        if (poiList != null && poiList.size() > 0) {
            if(mCenter != null){
                StreetMarkBitmap bt =
                    new StreetMarkBitmap(textSize, positionColor, distanceColor, bgColor, alpha, borderColor);
                Bitmap mark = bt.drawMap(mCenter.getName(), "");
                Bitmap markPress = bt.drawMapWithBorder(mark, mCenter.getName(), "");
                pois.add(new StreetPoiData(mCenter, mark, markPress, 20));
            }
            for (PoiInfo poi : poiList) {
                if(poi.equals(mCenter)) continue;
                LatLonPoint pt = poi.getPoint(); 
                
                String poiName = "";
                if(poi.type == BusiPoiType.L_CURR) poiName = "我的位置";
                else poiName = poi.getName();
                
                StreetMarkBitmap bt =
                    new StreetMarkBitmap(textSize, positionColor, distanceColor, bgColor, alpha, borderColor);
                double dis =
                    MapUtils.distance(mCenter.point.getLatitude(), mCenter.point.getLongitude(), pt.getLatitude(), pt.getLongitude()) * 1000;
                int intDis = (int)dis;
                int high = (int)(150 - 120 * Math.pow(0.9999, intDis));
                Log.d(TAG, String.valueOf(high));
                Bitmap mark = bt.drawMap(poiName, intDis + "米");
                Bitmap markPress = bt.drawMapWithBorder(mark, poiName, intDis + "米");
                pois.add(new StreetPoiData(poi, mark, markPress, high));
            }
        }
    }
}
