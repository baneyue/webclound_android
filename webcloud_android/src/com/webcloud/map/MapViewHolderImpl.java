package com.webcloud.map;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.funlib.json.JsonFriend;
import com.funlib.log.Log;
import com.webcloud.R;
import com.webcloud.define.BusiPoiType;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.model.PoiInfo;
import com.webcloud.service.LocationService;
import com.webcloud.service.LocationService.Config;

/**
 * 实现地图生命周期、定位控制。
 * 
 * 相关需要使用地图的活动中，扩展此类，见例子活动{@link DemoMapActivity}
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class MapViewHolderImpl implements MapViewHolder, MapLocationHolder, MapClickListener {
    public static LatLng getmCurrPoint() {
        return mCurrPoint;
    }

    public static String getmCityName() {
        return mCityName;
    }

    public static GaoDeAddress getmAddress() {
        return mAddress;
    }

    public static final String TAG = "MapViewHolderImpl";
    
    /**地图布局*/
    protected MapView mMapView;
    
    /**地图控制模块*/
    protected AMap mMap;
    
    /**地图界面设置*/
    protected UiSettings mUiSettings;
    
    /**定位管理代理器*/
    protected LocationManagerProxy mLocationMgr;
    
    /**定位改变监听器*/
    //protected OnLocationChangedListener mLocationLisr;//定位改变监听
    
    /**上下文会话*/
    protected Activity mContext;
    
    /**路径搜索*/
    protected RouteSearch mRouteSearch;
    
    /**地址编码*/
    protected GeocodeSearch mGeoSearch;
    
    /**我的位置mark*/
    protected Marker mCurrMark;
    
    /**我的位置坐标*/
    protected static LatLng mCurrPoint;
    
    /**我的位置所在城市名*/
    protected static String mCityName;
    
    /**我的位置地址详情*/
    protected static GaoDeAddress mAddress;
    
    /**地图定位共享配置文件*/
    protected static SharedPreferences mLocationPrefs;
    
    /**地图定位广播接收器*/
    protected MapLocationRecevierExtends mLocationReceiver;
    
    /**标识用户头像是否加载*/
    boolean headPicLoaded;
        
    /** <默认构造函数>
     *  初始化地图三变量，开启地图全部支持：除基本功能，还包括路径搜索、地址编码、定位开启。 
     */
    protected MapViewHolderImpl(Activity mContext, MapView mMapView, Bundle savedInstanceState) {
        super();
        this.mContext = mContext;
        this.mMapView = mMapView;
        onCreate(savedInstanceState);
        this.mMap = this.mMapView.getMap();
        this.mUiSettings = this.mMap.getUiSettings();
        this.mMap.setInfoWindowAdapter(this);
        if(mLocationPrefs == null){
            mLocationPrefs = mContext.getSharedPreferences(LocationService.TAG, Context.MODE_PRIVATE);
        }
        this.mLocationReceiver = new MapLocationRecevierExtends(mContext);
        initHistoryLocationData();
        initRouteSearch();
        initGeoSearch();
        onMapConfig();
    }
    
    /** 
     * 必须调用以进行地图初始化。
     * 按照所需，进行相应的配置。
     * 1.如需要监听来自{@link LocationService}的定位成功后的广播，就需要在此处注册广播，调用{@link registeReceiver}方法
     * 2.如需要控制地图的显示配置，可以进行如下配置，下面是比较完整的配置，可以按需进行配置：
     *  mMap.setTrafficEnabled(true);// 显示实时交通状况
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
        initLocationSource();//是否初始化定位模块
        registeReceiver();//是否接收LocationService的定位成功广播通知
     * 
     */
    protected abstract void onMapConfig();
    
    /** 
     * 初始化路径搜索。
     *
     */
    private void initRouteSearch() {
        this.mRouteSearch = new RouteSearch(mContext);
        this.mRouteSearch.setRouteSearchListener(this);
    }
    
    /** 
     * 初始化地址编码和反编码。
     *
     */
    private void initGeoSearch() {
        this.mGeoSearch = new GeocodeSearch(mContext);
        this.mGeoSearch.setOnGeocodeSearchListener(this);
    }
    
    /** 
     * 开启定位服务。
     *
     */
    protected void initLocationSource() {
        this.mMap.setLocationSource(this);
    }
    
    /** 
     * 解析历史定位数据。
     * 给地图一个初始的定位坐标和中心点及城市名称。
     */
    private void initHistoryLocationData() {
        //初始化当前位置和城市名称
        try {
            if(mCurrPoint == null || mCurrPoint.latitude == 0){
                double latitude = Double.valueOf(mLocationPrefs.getString(LocationService.Config.LATITUDE, "0"));
                double longtitude = Double.valueOf(mLocationPrefs.getString(LocationService.Config.LONGTITUDE, "0"));
                mCurrPoint = new LatLng(latitude, longtitude);
                if(mCurrPoint.latitude ==  0){
                    mCurrPoint = new LatLng(28.192955, 113.01995);
                    Editor ed = mLocationPrefs.edit();
                    ed.putString("latitude", "28.192456479");
                    ed.putString("longtitude", "113.01950418");
                    ed.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(mAddress == null){
            String gdAddressStr = mLocationPrefs.getString(LocationService.Config.GAODE_ADDRESS, null);
            //这个地址bean中的address是：省+城市名+区名+poiname
            //城市名包含市
            JsonFriend<GaoDeAddress> gdJs = new JsonFriend<GaoDeAddress>(GaoDeAddress.class);
            GaoDeAddress gdAddress = gdJs.parseObject(gdAddressStr);
            if (gdAddress != null) {
                mAddress = gdAddress;
                mCityName = mAddress.getCityname();
            }else{
                mAddress = new GaoDeAddress("长沙", "芙蓉区东屯渡街道长沙第二长途电信枢纽大楼(东门)", "荷花园电信", 28.1924564797, 113.019504189);
                mAddress.setDistinct("芙蓉区");
                mAddress.setProvince("湖南");
                mCityName = "长沙";
                Editor ed = mLocationPrefs.edit();
                ed.putString(Config.GAODE_ADDRESS, JSON.toJSONString(mAddress));
                ed.putString(Config.GAODE_ADDRESS2, "芙蓉区东屯渡街道长沙第二长途电信枢纽大楼(东门)");
                ed.commit();
            }
        }
    }
    
    /** {@inheritDoc 地图中心点、放大倍数改变事件，一般不使用，使用结束时的事件} */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        
    }
    
    /** {@inheritDoc 地图中心点、放大倍数改变结束事件} */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        
    }
    
    @Override
    public void OnMapReferencechanged() {
        
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        
    }
    
    /** {@inheritDoc 地图加载完毕事件，可以进行常规的处理} */
    @Override
    public void onMapLoaded() {
        
    }
    
    /**start-----------   下面的生命周期方法 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }
    
    @Override
    public void onResume() {
        try {
            mMapView.onResume();
        } catch (Exception e) {
            Log.e(TAG, "未调用mContext.setMapHolder();请在扩展类的构造方法中调用");
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
    }
    
    @Override
    public void onPause() {
        mMapView.onPause();
        try {
            deactivate();
        } catch (Exception e) {
            Log.e(TAG, "未开启定位");
        }
    }
    
    @Override
    public void onDestroy() {
        mMap.clear();
        mMapView.onDestroy();
        mLocationReceiver.unregisteReceiver();
    }
    /**end---------------------- */
    
    /** {@inheritDoc 析构地图定位模块，无需手动调用 */
    @Override
    public void deactivate() {
        //mLocationLisr = null;
        if (mLocationMgr != null) {
            mLocationMgr.removeUpdates(this);
            mLocationMgr.destory();
        }
        mLocationMgr = null;
    }
    
    /** {@inheritDoc 激活地图定位模块，无需手动调用，当在onMapConfig方法中initLocationSource时才会激活此方法}} */
    @Override
    public void activate(OnLocationChangedListener listener) {
        //mLocationLisr = listener;
        if (mLocationMgr == null) {
            mLocationMgr = LocationManagerProxy.getInstance(mContext);
            /*
             * mAMapLocManager.setGpsEnable(false);//
             * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
             */
            // Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
            mLocationMgr.setGpsEnable(true);
            mLocationMgr.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
        }
    }
    
    /**定位成功回调，可以扩展进行相应的处理*/
    @Override
    public void onLocationChanged(AMapLocation arg0) {
        
    }
    
    @Override
    @Deprecated
    /**过时不推荐使用*/
    public void onLocationChanged(Location location) {
        
    }
    
    @Override
    public void onProviderDisabled(String provider) {
        
    }
    
    @Override
    public void onProviderEnabled(String provider) {
        
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        
    }
    
    @Override
    /**地址编码回调*/
    public void onGeocodeSearched(GeocodeResult geoResult, int code) {
        
    }
    
    @Override
    /**地址反编码回调*/
    public void onRegeocodeSearched(RegeocodeResult regeoResult, int code) {
        
    }
    
    @Override
    /**搜索公交车路径回调*/
    public void onBusRouteSearched(BusRouteResult routeResult, int code) {
        
    }
    
    @Override
    /**搜索驾车路径回调*/
    public void onDriveRouteSearched(DriveRouteResult routeResult, int code) {
        
    }
    
    @Override
    /**搜索步行回调*/
    public void onWalkRouteSearched(WalkRouteResult routeResult, int code) {
        
    }
    
    @Override
    /**搜索poi详情回调*/
    public void onPoiItemDetailSearched(PoiItemDetail poiDetail, int code) {
        
    }
    
    @Override
    /**搜索poi列表回调*/
    public void onPoiSearched(PoiResult poiResult, int code) {
        
    }
    
    private ProgressDialog progDialog = null;// 搜索时进度条
    
    /**
     * 显示进度框
     */
    public void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(mContext);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }
    
    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    
    @Override
    /**获取地图信息框，若不进行扩展，就会显示地图自带的默认视图*/
    public View getInfoContents(Marker marker) {
        return null;
    }
    
    @Override
    /**获取地图信息框，若不进行扩展，就会显示地图自带的默认视图*/
    public View getInfoWindow(Marker marker) {
        return null;
    }
    
    @Override
    /**地图信息框点击事件*/
    public void onInfoWindowClick(Marker marker) {
        
    }
    
    @Override
    /**地图长按事件*/
    public void onMapLongClick(LatLng pt) {
        
    }
    
    @Override
    /**地图点击事件*/
    public void onMapClick(LatLng pt) {
        
    }
    
    @Override
    /**地图mark点击事件*/
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    
    /**注册定位广播接收器，接收来自{@link LocationService的定位通知}*/
    public void registeReceiver(){
        mLocationReceiver.registeReceiver();
    }
    
    /** 
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域.
     *
     * @param update
     * @param callback
     */
    public void changeCamera(CameraUpdate update, CancelableCallback callback) {
        mMap.animateCamera(update, 1000, callback);
    }
    
    /** 
     * 广播接收到定位位置。
     * 如需要接收来自LocationService的定位广播通知，请先在{@link onMapConfig}方法中调用{@link registeReceiver}方法注册广播接收器。
     *
     * @param gdAddress
     */
    protected abstract void onReceiveAddress(GaoDeAddress gdAddress);
    
    /** 
     * 广播接收到定位位置。
     * 如需要接收来自LocationService的定位广播通知，请先在{@link onMapConfig}方法中调用{@link registeReceiver}方法注册广播接收器。
     * 
     * @param iLatitu
     * @param iLongti
     */
    protected abstract void onReceiveLocation(double iLatitu, double iLongti);
    
    /**
     * 地图接收器内部扩展类。
     * 供外部类使用。
     * 
     */
    class MapLocationRecevierExtends extends MapLocationRecevierImpl {
        
        protected MapLocationRecevierExtends(Context context) {
            super(context);
        }
        
        @Override
        public void onReceiveAddress(GaoDeAddress gdAddress) {
            mAddress = gdAddress;
            //把城市名中的市剔除
            mCityName = MapUtils.getCityNameExcludeShi(mAddress.getCityname());
            //标记或更新当前位置到地图上
            if (mAddress != null) {
                if(mCurrMark == null){
                    markCurrPoint();
                } else {
                    resetCurrPoint();
                }
            }
            //回调父类通知定位到了新的地址
            MapViewHolderImpl.this.onReceiveAddress(gdAddress);
            //如果用户头像未加载成功，推送一次加载头像的事件
            if(!headPicLoaded){
                postToLoadUserIcon();
            }
        }
        
        @Override
        public void onReceiveLocation(double iLatitu, double iLongti) {
            mCurrPoint = new LatLng(iLatitu, iLongti);
            if(mCurrMark != null){
                mCurrMark.setPosition(mCurrPoint);
            }
            //回调父类通知定位到了新的坐标
            MapViewHolderImpl.this.onReceiveLocation(iLatitu, iLongti);
        }
    }
    
    /** 
     * 标注当前位置到地图上。
     *
     */
    public void markCurrPoint() {
        PoiInfo poi =
            new PoiInfo(new LatLonPoint(mAddress.getLatitude(), mAddress.getLongtitude()), mAddress.getPoiname(),
                mAddress.getAddress(), null);
        poi.setType(0);
        mCurrMark = markBusiPoiToMap(poi, false, true);
    }
    
    /** 
     * 重置当前位置。
     *
     */
    public void resetCurrPoint() {
        if(mAddress != null && mCurrMark != null){
            PoiInfo poi =
                new PoiInfo(new LatLonPoint(mAddress.getLatitude(), mAddress.getLongtitude()), mAddress.getPoiname(),
                    mAddress.getAddress(), null);
            mCurrMark.setTitle("我的位置");
            mCurrMark.setSnippet(MapUtils.getAddressAfterShi(poi.getAddress()));
            mCurrMark.setObject(poi);
        }
    }
    
    /** 
     * 组装mark,控制其是否显示，是否弹出pop,是否在地图中央。
     *
     * @param poi
     * @param toCenter
     * @param showMark
     * @return
     */
    public Marker markBusiPoiToMap(PoiInfo poi, boolean toCenter, boolean showMark) {
        LatLng pt = MapUtils.convertToLatLng(poi.getPoint());
        String poiName = poi.getName();
        String snippet = "";//MapUtil.getAddressAfterShi(poi.getAddress());
        int type = poi.getType();
        int iconId = BusiPoiType.getIconIdByPoiType(type,R.drawable.markpoint);
        Marker tempMark = mMap.addMarker(new MarkerOptions().position(pt)//坐标
            .title(poiName)
            .snippet(snippet)
            .icon(BitmapDescriptorFactory.fromResource(iconId))
            .perspective(true)
            // 设置远小近大效果,2.1.0版本新增
            .draggable(false));
        //vh.currMark.showInfoWindow();
        tempMark.setObject(poi);
        
        if (toCenter) {
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pt, 18, 0, 0)), null);
        }
        if (!showMark)
            tempMark.setVisible(false);
        //我的位置点，加载头像
        if (type == BusiPoiType.L_CURR){
            postToLoadUserIcon();
        }
        return tempMark;
    }
    
    /** 
     * 组装mark,控制其是否显示，是否弹出pop,是否在地图中央。
     *
     * @param poi
     * @param defaltIconId 默认的poi图标
     * @param toCenter
     * @param showMark
     * @return
     */
    public Marker markBusiPoiToMap(PoiInfo poi,int defaltIconId, boolean toCenter, boolean showMark) {
        LatLng pt = MapUtils.convertToLatLng(poi.getPoint());
        String poiName = poi.getName();
        String snippet = "";//MapUtil.getAddressAfterShi(poi.getAddress());
        int type = poi.getType();
        int iconId = BusiPoiType.getIconIdByPoiType(type,defaltIconId);
        Marker tempMark = mMap.addMarker(new MarkerOptions().position(pt)//坐标
            .title(poiName)
            .snippet(snippet)
            .icon(BitmapDescriptorFactory.fromResource(iconId))
            .perspective(true)
            // 设置远小近大效果,2.1.0版本新增
            .draggable(false));
        //vh.currMark.showInfoWindow();
        tempMark.setObject(poi);
        
        if (toCenter) {
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pt, 18, 0, 0)), null);
        }
        if (!showMark)
            tempMark.setVisible(false);
        if (type == 0){
            postToLoadUserIcon();
        }
        return tempMark;
    }
    
    
    
    /** 
     * 组装mark,PoiInfo为空。
     *
     * @param poi
     * @param toCenter
     * @param showMark
     * @return
     */
    public Marker markPointToMap(LatLng pt,String name,int type,boolean toCenter, boolean showMark,boolean showInfo) {
        int iconId = BusiPoiType.getIconIdByPoiType(type,R.drawable.mark_location);
        Marker tempMark = mMap.addMarker(new MarkerOptions().anchor(0.5f, 1).position(pt)//坐标
            .title(name)
            .snippet("")
            .icon(BitmapDescriptorFactory.fromResource(iconId))
            .perspective(true)
            // 设置远小近大效果,2.1.0版本新增
            .draggable(false));
        if (toCenter) {
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pt, 17, 30, 0)), null);
        }
        if (!showMark)
            tempMark.setVisible(false);
        if (!showInfo)
            tempMark.showInfoWindow();
        if (type == 0){
            postToLoadUserIcon();
        }
        return tempMark;
    }
    
    /** 
     * 把当前位置mark,放在地图中央。
     *
     */
    public void markCurrPointToCenter() {
        if (mCurrPoint != null) {
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mCurrPoint, 18, 30, 0)), null);
        }
    }
    
    /** 
     * 把坐标标到地图上，并且所有坐标都展示在地图可视范围内。
     *
     * @param poi
     */
    public void markToMap(List<PoiInfo> poiList,int w,int h) {
        //记录poi,点击地图时用到
        if(poiList!=null && poiList.size()>0){
            Builder build = new LatLngBounds.Builder();
            if(mCurrPoint!= null && mCurrPoint.latitude != 0){
                build = build.include(mCurrPoint);
            }
            for(int i=0;i < poiList.size();i++){
                PoiInfo poi = poiList.get(i);
                if(poi.type == BusiPoiType.TCC){
                    //停车场不显示
                    markBusiPoiToMap(poi,false,true);
                }else{
                    
                    markBusiPoiToMap(poi,false,true);
                }
                build = build.include(MapUtils.convertToLatLng(poi.getPoint()));
            }
            
            LatLngBounds bounds = build.build();
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,w,h,10));
        }else {
            markCurrPointToCenter();
        }
    }
    
    /** 
     * 获取我的位置poiinfo.
     *
     * @return
     */
    public PoiInfo getCurrPointPoiInfo(){
        if(mCurrMark != null && mCurrMark.getObject()!=null){
            return (PoiInfo)mCurrMark.getObject();
        }
        return null;
    }
    
    /** 
     * 加载用户头像。
     *
     *//*
    public void getUserIconToUserMark(){
        CTCloudApplication app = CTCloudApplication.getInstance();
        if (!TextUtils.isEmpty("")) {
            ImageCacheManager imgCacheMgr = SystemManager.getInstance(mContext).imgCacheMgr;
            imgCacheMgr.getImageLoader().displayImage(Global.PIC_SERVER_URL + app.getUserphoto(), new ImageView(mContext), imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.d(TAG, "加载头像成功");
                    Bitmap bm = SetRoundBitmap.SD(loadedImage, 51);
                    view = mContext.getLayoutInflater().inflate(R.layout.vehicle_map_user_icon_mark, null);
                    ImageView ivUser = (ImageView)view.findViewById(R.id.ivUser);
                    if(mCurrMark != null){
                        ivUser.setImageBitmap(bm);
                        mCurrMark.setIcon(BitmapDescriptorFactory.fromView(view));
                        mCurrMark.setVisible(true);
                        headPicLoaded = true;
                        Log.d(TAG, "头像设置上，看能不能显示");
                    } else {
                        Log.d(TAG, "头像加载到但是，我的位置mark没有");
                    }
                }
                
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    headPicLoaded = false;
                    super.onLoadingCancelled(imageUri, view);
                }
                
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    headPicLoaded = false;
                    super.onLoadingFailed(imageUri, view, failReason);
                }
                
            });
        }
    }*/
    
    /**地图相关操作处理器*/
    static Handler mapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    
    /** 
     * 加载用户头像。
     * 延迟1.5秒，待当前页面窗口获取到焦点。
     */
    public void postToLoadUserIcon(){
        mapHandler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                //getUserIconToUserMark();
            }
        },1500);
    }
    
    /** 
     * 加载爆料用户头像
     *
     *//*
    public void postToLoadBrokeIcon(){
        mapHandler.post(new Runnable() {
            
            @Override
            public void run() {
                getUserIconToUserMark();
            }
        });
    }*/
    
    /** 
     * 把商业信息poi列表解析成可以使用的对象。
     *
     * @param poiList
     * 
     * return poiList 非空，但长度可能为0
     */
    /*public static List<PoiInfo> parseBusiToPoiList(List<BusinessPoiModel> busiModelList) {
        //mvh.busiPoiList.clear();
        List<PoiInfo> poiList = new ArrayList<PoiInfo>();
        for (BusinessPoiModel pm : busiModelList) {
            LatLonPoint pt = null;
            if (RegexUtil.isPostiveRealDigit(pm.c_Lat) && RegexUtil.isPostiveRealDigit(pm.c_Long)) {
                double lat = Double.parseDouble(pm.c_Lat);
                double lng = Double.parseDouble(pm.c_Long);
                if(lat == 0 || lng == 0) continue;
                pt = new LatLonPoint(lat, lng);
            } else {
                continue;
                //pt = new LatLonPoint(28, 113);
            }
            PoiInfo poi = new PoiInfo(pt, pm.getName(), pm.getShop_address(), null);
            poi.setObject(pm);
            if (RegexUtil.isPostiveRealDigit(pm.panoid)) {
                poi.panoid = pm.panoid;
            }
            if (RegexUtil.isPostiveRealDigit(pm.heading)) {
                poi.heading = Float.parseFloat(pm.heading);
            }
            if (RegexUtil.isRealDigit(pm.pitch)) {
                poi.pitch = Float.parseFloat(pm.pitch);
            }
            
            if (RegexUtil.isPositiveInt(pm.c_class_type)) {
                poi.setType(Integer.parseInt(pm.getC_class_type()));
            } else {
                poi.setType(BusiPoiType.ZCD);
            }
            if(poi.type == BusiPoiType.JYZ && !"0".equals(pm.bestpaymark)){
                poi.setType(BusiPoiType.YZF);
            }
            poiList.add(poi);
        }
        Log.d(TAG, "解析通过了");
        return poiList;
    }*/
}
