package com.webcloud.func.map;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.StreetNumber;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.funlib.log.Log;
import com.webcloud.R;
import com.webcloud.func.map.util.AMapUtil;
import com.webcloud.func.map.util.ToastUtil;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.model.PoiInfo;

/**
 * 车行定位处理器。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VehiclePoiSearchMapViewHolder {
    public static final String TAG = "VehiclePoiSearchMapViewHolder";
    
    private VehiclePoiSearchActivity context;
    
    private ViewHolder vh;
    
    private MapViewHolder mvh;
    
    PoiSearch.Query poiSearchQuery;
    
    /**
     * 普通视图holder.
     */
    static class ViewHolder {
        TextView tvAddress;
    }
    
    /**
     * 地图holder.
     */
    static class MapViewHolder {
        MapView mvMap;
        
        AMap aMap;
        
        UiSettings mUiSettings;//地图界面设置
        
        LocationManagerProxy mLocationMgr;//定位管理代理器
        
        OnLocationChangedListener mLocationLisr;//定位改变监听
        
        LatLng currLoc;
        
        GeocodeSearch geocoderSearch;
        
        Marker currMark, searchMark;
        
        LatLonPoint searchPoint;
        
        boolean isCurrFirstShow = false;
        
        String cityName;
        
        int selectMarkStatus = 0;
    }
    
    public VehiclePoiSearchMapViewHolder(VehiclePoiSearchActivity context, Bundle savedInstanceState) {
        this.context = context;
        initView();
        initMapView(savedInstanceState);
    }
    
    public void initMapView(Bundle savedInstanceState) {
        if (mvh == null) {
            mvh = new MapViewHolder();
            //高德地图初始化
            mvh.mvMap = (MapView)context.findViewById(R.id.mvMap);
            mvh.mvMap.onCreate(savedInstanceState);
            mvh.aMap = mvh.mvMap.getMap();
            mvh.aMap.setTrafficEnabled(false);// 显示实时交通状况
            //mvh.aMap.setLocationSource(locationSrc);//设置定位监听
            mvh.aMap.setMyLocationEnabled(true);//是否可触发定位并显示定位层
            mvh.aMap.setOnMapClickListener(mapClickLisr);
            mvh.aMap.setInfoWindowAdapter(new InfoWindowAdapter() {
                
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }
                
                @Override
                public View getInfoContents(Marker mark) {
                    return null;
                }
            });
            mvh.aMap.setOnInfoWindowClickListener(infoClickLsr);
            
            mvh.geocoderSearch = new GeocodeSearch(context);
            mvh.geocoderSearch.setOnGeocodeSearchListener(geoSearchLisr);
            
            mvh.mUiSettings = mvh.aMap.getUiSettings();
            mvh.mUiSettings.setScaleControlsEnabled(true);//设置比例尺显示
            mvh.mUiSettings.setZoomControlsEnabled(true);//设置默认放大图标显示
            mvh.mUiSettings.setCompassEnabled(true);//设置指南针显示
            mvh.mUiSettings.setScrollGesturesEnabled(true);//设置手势移动位置
            mvh.mUiSettings.setZoomGesturesEnabled(false);//设置手势放大缩小
            //vh.mUiSettings.setTiltGesturesEnabled(true);//设置地图是否可用倾斜
            mvh.mUiSettings.setRotateGesturesEnabled(false);//设置地图是否可用旋转
        }
    }
    
    public void initView() {
        if (vh == null) {
            vh = new ViewHolder();
            //vh.tvAddress = (TextView)context.findViewById(R.id.tvAddress);
        }
        
    }
    
    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, CancelableCallback callback) {
        mvh.aMap.animateCamera(update, 1000, callback);
    }
    
    public void releaseMap() {
        if (mvh.mvMap != null) {
        }
        Log.d(TAG, "释放地图资源");
    }
    
    public void onCreate(Bundle savedInstanceState) {
        mvh.mvMap.onCreate(savedInstanceState);// 此方法必须重写
    }
    
    public void onSaveInstanceState(Bundle outState) {
        mvh.mvMap.onSaveInstanceState(outState);
    }
    
    public void onDestroy() {
        mvh.mvMap.onDestroy();
    }
    
    public void onPause() {
        mvh.mvMap.onPause();
        //locationSrc.deactivate();
    }
    
    public void onResume() {
        mvh.mvMap.onResume();
    }
    
/*    LocationSource locationSrc = new LocationSource() {
        
        @Override
        public void deactivate() {
            mvh.mLocationLisr = null;
            if (mvh.mLocationMgr != null) {
                mvh.mLocationMgr.removeUpdates(amapLisr);
                mvh.mLocationMgr.destory();
            }
            mvh.mLocationMgr = null;
        }
        
        @Override
        public void activate(OnLocationChangedListener listener) {
            mvh.mLocationLisr = listener;
            if (mvh.mLocationMgr == null) {
                mvh.mLocationMgr = LocationManagerProxy.getInstance(context);
                
                 * mAMapLocManager.setGpsEnable(false);//
                 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
                 
                // Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
                mvh.mLocationMgr.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, amapLisr);
            }
        }
    };*/
    
    public void setCityName(String cityName) {
        mvh.cityName = cityName;
    }
    
    public void setCurrPoint(LatLng point) {
        mvh.currLoc = point;
        initCurrMark(mvh.isCurrFirstShow);
    }
    
/*    AMapLocationListener amapLisr = new AMapLocationListener() {
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            
        }
        
        @Override
        public void onProviderEnabled(String provider) {
            
        }
        
        @Override
        public void onProviderDisabled(String provider) {
            
        }
        
        @Override
        public void onLocationChanged(Location location) {
            
        }
        
        @Override
        public void onLocationChanged(AMapLocation aLocation) {
            if (vh.mListener != null) {
                vh.mListener.onLocationChanged(aLocation);//显示系统小蓝点
            }
            //重置我的位置mark
            mvh.currLoc = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
            
            //vh.mvMap.invalidate();
            //Log.d(TAG, "高德定位得到位置：" + aLocation.toString());
            //地址编码
            
            initCurrMark(mvh.isCurrFirstShow);
        }
    };*/
    
    public void initCurrMark(boolean show) {
        if (mvh.currMark == null) {
            mvh.currMark = mvh.aMap.addMarker(new MarkerOptions().position(mvh.currLoc)//坐标
                .title("我的位置")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation))
                .perspective(true)
                // 设置远小近大效果,2.1.0版本新增
                .draggable(false));
            //vh.currMark.showInfoWindow();
        } else {
            //重置我的当前位置
            mvh.currMark.setPosition(mvh.currLoc);
        }
        
        //地址反编码，第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        searchPoint = new LatLonPoint(mvh.currLoc.latitude, mvh.currLoc.longitude);
        searchMark = mvh.currMark;

        RegeocodeQuery query = new RegeocodeQuery(searchPoint, 200, GeocodeSearch.AMAP);
        mvh.geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        
        //切换位置,此处只调用一次
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mvh.currLoc, 17, 30, 0)), null);
    }
    
    public void initSearchMark(boolean show, PoiInfo poi) {
        String poiName = TextUtils.isEmpty(poi.getAddress()) ? "搜索位置" : poi.getAddress();
        mvh.searchPoint = poi.getPoint();
        LatLng tempPoint = new LatLng(mvh.searchPoint.getLatitude(), mvh.searchPoint.getLongitude());
        if (mvh.searchMark == null) {
            mvh.searchMark = mvh.aMap.addMarker(new MarkerOptions().position(tempPoint)//坐标
                .title(poiName)
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_location))
                .perspective(true)
                // 设置远小近大效果,2.1.0版本新增
                .draggable(false));
            //vh.currMark.showInfoWindow();
        } else {
            //重置我的当前位置
            mvh.searchMark.setPosition(tempPoint);
        }
        
        mvh.searchMark.setObject(poi);
        
        //切换位置
        if (show) {
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(tempPoint, 16, 30, 0)), null);
        }
    }
    
    OnMapClickListener mapClickLisr = new OnMapClickListener() {
        
        @Override
        public void onMapClick(LatLng pt) {
            showClickMark(pt, true);
        }
    };
    
    private Marker searchMark;
    
    private LatLonPoint searchPoint;
    
    public PoiInfo getCurrAddress(){
        if(mvh.currMark.getObject() != null){
            return (PoiInfo)mvh.currMark.getObject();
        }
        initCurrMark(true);
        return null;
    }
    
    private OnGeocodeSearchListener geoSearchLisr = new OnGeocodeSearchListener() {
        
        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            if (rCode == 0) {
                if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                    RegeocodeAddress geoAddress = result.getRegeocodeAddress();
                    //格式化地址前缀
                    String province = geoAddress.getProvince();
                    String city = geoAddress.getCity();
                    String distinct = geoAddress.getDistrict();
                    String adcode = null;
                    
                    String address = geoAddress.getFormatAddress();
                    String poiname = null;
                    if (address != null && address.indexOf(province + city + distinct) > -1) {
                        String prefix = province + city + distinct;
                        int preIndex = 0;
                        if (!TextUtils.isEmpty(prefix)) {
                            preIndex = prefix.length();
                        }
                        poiname = address.substring(preIndex);
                    } else
                        poiname = address;
                    
                    String building = geoAddress.getBuilding();
                    String neigh = geoAddress.getNeighborhood();
                    String township = geoAddress.getTownship();
                    StreetNumber street = geoAddress.getStreetNumber();
                    String streetStr = street.getStreet();
                    
                    GaoDeAddress gdAddress =
                        new GaoDeAddress(city, address, poiname, searchPoint.getLatitude(), searchPoint.getLongitude());
                    gdAddress.setAdcode(adcode);
                    gdAddress.setBuilding(building);
                    gdAddress.setDistinct(distinct);
                    gdAddress.setNeighborhood(neigh);
                    gdAddress.setProvince(province);
                    gdAddress.setStreet(streetStr);
                    gdAddress.setTownship(township);
                    
                    if (searchMark.equals(mvh.currMark)) {
                        PoiInfo poiInfo = new PoiInfo();
                        poiInfo.setAddress(gdAddress.getAddress());
                        poiInfo.setName(gdAddress.getPoiname());
                        poiInfo.setPoint(searchPoint);
                        poiInfo.setType(0);
                        mvh.currMark.setObject(poiInfo);
                        vh.tvAddress.setText(gdAddress.getAddress());
                    } else if (searchMark.equals(mvh.searchMark)) {
                        PoiInfo poiInfo = new PoiInfo();
                        poiInfo.setAddress(gdAddress.getAddress());
                        poiInfo.setName(gdAddress.getPoiname());
                        poiInfo.setPoint(searchPoint);
                        poiInfo.setType(1);
                        mvh.searchMark.setObject(poiInfo);
                        context.setAddressByRecoderSearch(poiInfo);
                    }
                }
            }
        }
        
        @Override
        public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
            
        }
    };
    
    OnPoiSearchListener poiSearchLisr = new OnPoiSearchListener() {
        
        /**
         * POI搜索结果回调
         */
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            dissmissProgressDialog();
            if (rCode == 0) {// 返回成功
                if (result != null && result.getQuery() != null && result.getPois() != null
                    && result.getPois().size() > 0) {// 搜索poi的结果
                    List<PoiItem> pois = result.getPois();
                    List<PoiInfo> tempInfos = new ArrayList<PoiInfo>();
                    for (PoiItem poi : pois) {
                        if (!TextUtils.isEmpty(poi.getTitle()) && !TextUtils.isEmpty(poi.getSnippet())
                            && poi.getLatLonPoint().getLatitude() != 0 && poi.getLatLonPoint().getLongitude() != 0) {
                            PoiInfo info = new PoiInfo(poi.getLatLonPoint(), poi.getTitle(), poi.getSnippet(), null);
                            info.setType(1);
                            tempInfos.add(info);
                        }
                    }
                    if (tempInfos.size() > 0) {
                        context.showSearchList(tempInfos);
                    }
                } else {
                    ToastUtil.show(context, R.string.no_result);
                }
            } else {
                ToastUtil.show(context, R.string.error_network);
            }
        }
        
        @Override
        public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
            
        }
    };
    
    OnInfoWindowClickListener infoClickLsr = new OnInfoWindowClickListener() {
        
        @Override
        public void onInfoWindowClick(Marker marker) {
            //家或公司的位置设置后，如果检查到poi信息，然后列表显示出来，如果用户选择了其中之一那么就，保存为用户的地址信息
            if (marker.equals(mvh.searchMark)) {
                mvh.searchPoint = AMapUtil.convertToLatLonPoint(mvh.searchMark.getPosition());
                mvh.searchMark.hideInfoWindow();
                mvh.searchMark.setTitle("已设置");
                if (mvh.searchMark.getObject() == null) {
                    poiSearchResult("通过点搜索");
                }
            }
        }
    };
    
    /**
     * 查询路径规划起点
     */
    public void startSearchResult(String input) {
        showProgressDialog();
        poiSearchQuery = new PoiSearch.Query(input, "", mvh.cityName); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
        poiSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
        poiSearchQuery.setPageSize(20);// 设置每页返回多少条数据
        PoiSearch poiSearch = new PoiSearch(context, poiSearchQuery);
        poiSearch.setOnPoiSearchListener(poiSearchLisr);
        poiSearch.searchPOIAsyn();// 异步poi查询
    }
    
    /**
     * 查询poi的结果
     */
    public void poiSearchResult(String searchStr) {
        //如果有家的坐标，通过家坐标，否则通过文本查询起点
        if (mvh.searchPoint != null && searchStr.equals("通过点搜索")) {
            //查询regeocode得到家的详细地址
            showProgressDialog();
            RegeocodeQuery query = new RegeocodeQuery(mvh.searchPoint, 200, GeocodeSearch.AMAP);
            mvh.geocoderSearch.getFromLocationAsyn(query);//设置同步逆地理编码请求
            searchMark = mvh.searchMark;
            searchPoint = mvh.searchPoint;
        } else {
            showProgressDialog();
            poiSearchQuery = new PoiSearch.Query(searchStr, "", mvh.cityName); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
            poiSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
            poiSearchQuery.setPageSize(20);// 设置每页返回多少条数据
            PoiSearch poiSearch = new PoiSearch(context, poiSearchQuery);
            poiSearch.setOnPoiSearchListener(poiSearchLisr);
            poiSearch.searchPOIAsyn();// 异步poi查询
        }
    }
    
    private ProgressDialog progDialog = null;// 搜索时进度条
    
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }
    
    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }
    }
    
    public void setSearchType(int searchType) {
        mvh.selectMarkStatus = searchType;
    }
    
    /** 
     * 设置家、公司的mark
     *
     * @param status
     * @param pt
     * @see [类、类#方法、类#成员]
     */
    public void showClickMark(LatLng pt, boolean show) {
        String title;
        int id;
        title = "点击选择该位置";
        id = R.drawable.mark_location;
        if (mvh.searchMark == null) {
            mvh.searchMark =
                mvh.aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                    .icon(BitmapDescriptorFactory.fromResource(id))
                    .position(pt)
                    .title(title));
        } else {
            mvh.searchMark.setPosition(pt);
            mvh.searchMark.setTitle(title);
            mvh.searchMark.setIcon(BitmapDescriptorFactory.fromResource(id));
        }
        mvh.searchMark.showInfoWindow();
        mvh.searchMark.setObject(null);
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pt, 17, 30, 0)), null);
    }
}
