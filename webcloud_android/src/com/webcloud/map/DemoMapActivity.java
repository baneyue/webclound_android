package com.webcloud.map;

import android.os.Bundle;

import com.amap.api.maps.MapView;
import com.webcloud.R;
import com.webcloud.model.GaoDeAddress;

/**
 * 使用地图的例子活动。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-26]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DemoMapActivity extends BaseMapActivity {
    DemoMapHolder mapHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局中需要包含高德地图
        setContentView(100);
        mapHolder = new DemoMapHolder(this, (MapView)findViewById(R.id.mvMap), savedInstanceState);
    }
    
    static class DemoMapHolder extends MapViewHolderImpl{

        protected DemoMapHolder(DemoMapActivity mContext, MapView mMapView, Bundle savedInstanceState) {
            super(mContext, mMapView, savedInstanceState);
            mContext.setMapHolder(this);
        }

        @Override
        protected void onMapConfig() {
            //需要自主定位不接受LocationService的广播就调用此方法
            initLocationSource();
            //需要接收LocationService的定位广播就调用此方法
            registeReceiver();
            
            //地图自身属性和视图配置
            mMap.setTrafficEnabled(true);// 显示实时交通状况
            mMap.setMyLocationEnabled(true);//是否可触发定位并显示定位层
            mMap.setInfoWindowAdapter(this);//是否显示信息窗口
            mMap.setOnMarkerClickListener(this);//是否监听mark点击
            mMap.setOnMapLoadedListener(this);//是否监听地图加载完毕事件
            mMap.setOnMapClickListener(this);//是否监听地图点击事件
            mMap.setOnInfoWindowClickListener(this);//是否监听地图窗口点击事件
            mMap.setOnMapLongClickListener(this);//是否监听地图长按事件
            mMap.setCustomRenderer(this);//
            mMap.setOnCameraChangeListener(this);//是否监听地图放大倍数或中心点改变事件
            
            mUiSettings.setScaleControlsEnabled(true);//设置比例尺显示
            mUiSettings.setZoomControlsEnabled(false);//设置默认放大图标显示
            mUiSettings.setCompassEnabled(true);//设置指南针显示
            mUiSettings.setScrollGesturesEnabled(true);//设置手势移动位置
            mUiSettings.setZoomGesturesEnabled(true);//设置手势放大缩小
            mUiSettings.setTiltGesturesEnabled(true);//设置地图是否可用倾斜
            mUiSettings.setRotateGesturesEnabled(true);//设置地图是否可用旋转
            mUiSettings.setMyLocationButtonEnabled(true); //是否显示默认的定位按钮
        }

        @Override
        protected void onReceiveAddress(GaoDeAddress gdAddress) {
            //需要就处理
        }

        @Override
        protected void onReceiveLocation(double iLatitu, double iLongti) {
            //需要就处理
        }
        
    }
}
