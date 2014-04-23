package com.webcloud.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.amap.api.services.road.Crossroad;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.funlib.log.Log;
import com.funlib.utily.DateTimeUtils;
import com.webcloud.ActivitysManager;
import com.webcloud.R;
import com.webcloud.WebCloudApplication;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.UserManager;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.model.PoiInfo;
import com.webcloud.model.TrafficRoad;

/**
 * 定位服务。
 * 1.使用soso api进行地图定位，得到坐标或城市信息放入配置文件中。
 * 2.定位成功后通知接收者进行处理。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-4]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class LocationService extends Service {
    public static final String TAG = "LocationService";
    
    //使用服务的动作
    private static final String ACTION_START = TAG + ".START";
    
    private static final String ACTION_STOP = TAG + ".STOP";
    
    private static final String ACTION_START_ALARM = TAG + ".START.ALARM";
    
    private static final String ACTION_STOP_ALARM = TAG + ".START.ALARM";
    
    private static final String ACTION_ALARM = TAG + ".ALARM";//闹铃
    
    private static final int MAX_INTERVAL = 10 * 60 * 1000;//应用后台运行间隔10分
    
    private static final int MIN_INTERVAL = 8 * 1000;//应用前台运行间隔5秒
    /**
     * 声明储存全局变量Application
     */
    private WebCloudApplication application;
    
    public interface Config {
        public static final String LATITUDE = "latitude";
        
        public static final String LONGTITUDE = "longtitude";
        
        public static final String GAODE_ADDRESS = "gaodeAddress";
        
        public static final String GAODE_ADDRESS2 = "gaodeAddress2";
    }
    
    public interface BroadcastAction {
        /**发送广播，传递address、poiname*/
        public static final String ADDRESS = "com.ct.lbs.LocationService.address";
        
        /**发送广播，传递latitude、longtitude、cityname*/
        public static final String COORDINATE = "com.ct.lbs.LocationService.coordinate";
    }
    
    /*//得到两个管理器：网络连接、通知
    private ConnectivityManager mConnMan;
    
    private NotificationManager mNotifMan;*/
    private AlarmManager mAlarmMgr;
    private LocationManagerProxy mLocationMgr;//定位管理代理器
    
    
    private boolean mStarted;//服务是否启动
    
    private GeocodeSearch geocoderSearch;//地址编码
    private RouteSearch mRouteSearch;//地址编码
    
    private Context mContext;//上下文
    
    private SharedPreferences mPrefs;//定位共享位置信息，存储定位后信息
    
    NotificationManager mNotifMan;
    
    Notification notification;
    public static final int NOTIF_LUKUANG_HOME = 1;//回家路况提示
    public static final int NOTIF_LUKUANG_COMPANY = 2;//上班路况提示
    
    private UserManager userMgr;
    
    /**应用检查*/
    Handler checkHandler = new Handler();

    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, LocationService.class);
        i.setAction(ACTION_START);
        i.setFlags(Service.START_STICKY);
        ctx.startService(i);
    }
    /**前台运行*/
    public static final int FLAG_RUN_MODE_FOREGROUD = 1;
    /**后台运行*/
    public static final int FLAG_RUN_MODE_BACKGROUD = 2;
    /** 
     * 加标志位启动服务，保证定位服务的实时性。
     * 控制定位服务的前后台运行，主要为了节约电量。
     * 
     * {@linkplain FLAG_RUN_MODE_FOREGROUD}
     * {@linkplain FLAG_RUN_MODE_BACKGROUD}
     * @param ctx
     * @param flag
     */
    public static void actionStart(Context ctx,int flag) {
        Intent i = new Intent(ctx, LocationService.class);
        i.setAction(ACTION_START);
        i.putExtra("flag", flag);
        i.setFlags(Service.START_STICKY);
        ctx.startService(i);
    }
    
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, LocationService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }
    
    public static void actionStartAlarm(Context ctx,int type) {
        Intent i = new Intent(ctx, LocationService.class);
        i.putExtra("type", type);
        i.setAction(ACTION_START_ALARM);
        i.setFlags(Service.START_STICKY);
        ctx.startService(i);
    }
    
    public static void actionStopAlarm(Context ctx,int type) {
        Intent i = new Intent(ctx, LocationService.class);
        i.putExtra("type", type);
        i.setAction(ACTION_STOP_ALARM);
        i.setFlags(Service.START_STICKY);
        ctx.startService(i);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        application = (WebCloudApplication) getApplication();
        userMgr = SystemManager.getInstance(this).userMgr;
        /*mConnMan = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        mNotifMan = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);*/
        mContext = WebCloudApplication.getInstance();
        mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
        mAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mNotifMan = (NotificationManager)WebCloudApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        checkHandler.postDelayed(r, 1000);
    }
    
    Runnable r = new Runnable() {
        
        @Override
        public void run() {
            Log.d(TAG, "刷新定位");
            //应用后台运行，关闭服务
            /*if(foreground && ActivitysManager.getLocalTopActivity() == null){
                //应用切换到后台
                Log.d(TAG, "前台切换到后台--fore to back");
                foreground = false;
                restart();
            } else if(!foreground && ActivitysManager.getLocalTopActivity() != null){
                Log.d(TAG, "后台切换到前台--back to fore");
                foreground = true;
                restart();
            }*/
            //应用后台运行，关闭服务
            boolean currForeground = ActivitysManager.isRunningForeground(mContext);
            //当前前台，但是是后台定位，修改为前台定位
            if (!foreground && currForeground) {
                Log.d(TAG, "后台切换到前台--back to fore");
                foreground = true;
                restart();
            } else if (foreground && !currForeground) {
                Log.d(TAG, "前台切换到后台--fore to back");
                foreground = false;
                restart();
            }
            
            //循环调用
            checkHandler.postDelayed(this, 5 * 60 * 1000);
        }
    };
    
    @Override
    public void onDestroy() {
        if (mStarted == true) {
            deactivate();
        }
        mStarted = false;
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {
            if (intent.getAction().equals(ACTION_STOP) == true) {
                //关闭定位和服务
                deactivate();
                stopSelf();
            } else if (intent.getAction().equals(ACTION_START) == true) {
                //开始定位
                int flag = intent.getIntExtra("flag", FLAG_RUN_MODE_FOREGROUD);
                start();
                //控制定位服务的前后台运行，主要为了节约电量
                if(flag == FLAG_RUN_MODE_FOREGROUD){
                    if(!foreground){
                        foreground = true;
                        restart();
                    }
                } else {
                    if(foreground){
                        foreground = false;
                        restart();
                    }
                }
            }/* else if(intent.getAction().equals(ACTION_START_ALARM) == true){
                int type = intent.getIntExtra("type", 0);
                setAlarm(type);
            } else if(intent.getAction().equals(ACTION_STOP_ALARM) == true){
                int type = intent.getIntExtra("type", 0);
                if(type == 3){
                    //家闹铃
                }else if(type == 4){
                    //公司闹铃
                }
            } else if(intent.getAction().equals(ACTION_ALARM) == true){
                //闹铃引爆了，分两步操作
                //通知栏通知路况信息，需要调用地图查询路径，路径返回后再查询路况列表信息
                //刷新闹铃，进行下一次闹铃
                final int type = intent.getIntExtra("type", 0);
                final PoiInfo poi = intent.getParcelableExtra("poi");
                //1.刷新闹铃
                //String tip = "";
                if(type == 3){
                    //家闹铃
                    setAlarm(type);
                }else if(type == 4){
                    //公司闹铃
                    setAlarm(type);
                }
                final String tip = type == 3 ? "下班时间到啦，快去查看回家路况吧…" : "上班时间到啦，快去查看上班路况吧…";
                //2.点击提醒
                
                initNotification();
                Intent i = new Intent(getApplicationContext(), VehicleLocationActivity.class);
                i.putExtra("trafficTipPoi", poi);
                i.putExtra("operate", VehicleLocationActivity.OPERATE_TRAFFIC_TIP);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setLatestEventInfo(getBaseContext(), "路况小助手", tip, pi);
                mNotifMan.notify(NOTIF_LUKUANG, notification);
                roadTraffic.clear();
                searchRoute(poi);
                checkHandler.postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            
                            @Override
                            public void run() {
                                try {
                                    String result = ActionObjiectUtil.sendRoad(getApplicationContext(),application.getHashcode(),application.getImsi(), String.valueOf(type==3?2:1), JSON.toJSONString(roadTraffic));
                                    if(roadTraffic != null && roadTraffic.size() > 0){
                                    } else {
                                    }
                                    Log.d(TAG, result+"结果");
                                    if(JsonResponse.CODE_SUCC.equals(result)){
                                        //提交成功本地不提醒
                                    } else{
                                        initNotification();
                                        Intent i = new Intent(getApplicationContext(), VehicleLocationActivity.class);
                                        i.putExtra("trafficTipPoi", poi);
                                        i.putExtra("operate", VehicleLocationActivity.OPERATE_TRAFFIC_TIP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                        notification.setLatestEventInfo(getBaseContext(), "路况小助手", tip, pi);
                                        mNotifMan.notify(type==3?NOTIF_LUKUANG_HOME:NOTIF_LUKUANG_COMPANY, notification);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }, 10*1000);
            }*/
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /** 
     * 已经打开服务就不再重复打开。
     *
     */
    private synchronized void start() {
        if (!mStarted) {
            mStarted = true;
            //注册网络变化管理器
            registerReceiver(mConnectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            mRouteSearch = new RouteSearch(mContext);
            mRouteSearch.setRouteSearchListener(routeSearchLisr);
            geocoderSearch = new GeocodeSearch(mContext);
            geocoderSearch.setOnGeocodeSearchListener(geoSearchLisr);
        }
    }
    
    /** 
     * 应用退出时关闭服务、或不再使用定位时关闭。
     *
     */
    private synchronized void restart() {
        try {
            //重启
            if (mLocationMgr != null) {
                deactivate();
            }
            activate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deactivate() {
        if (mLocationMgr != null) {
            Log.d(TAG, "关闭定位代理");
            mLocationMgr.removeUpdates(amapLisr);
            //mLocationMgr.destory();
        }
        //mLocationMgr = null;
    }
    
    public void activate() {
        if(mLocationMgr == null){
             mLocationMgr = LocationManagerProxy.getInstance(mContext);
        }
        if (mLocationMgr != null) {
            Log.d(TAG, "启动定位代理");
            //mLocationMgr = LocationManagerProxy.getInstance(mContext);
            /*
             * mAMapLocManager.setGpsEnable(false);//
             * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
             */
            // Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
            if (foreground) {
                mLocationMgr.setGpsEnable(true);
                mLocationMgr.requestLocationUpdates(LocationProviderProxy.AMapNetwork, MIN_INTERVAL, 50, amapLisr);
            } else {
                mLocationMgr.setGpsEnable(false);
                mLocationMgr.requestLocationUpdates(LocationProviderProxy.AMapNetwork, MAX_INTERVAL, 500, amapLisr);
            }
        }
    }
    
    double latitude, longtitude;
    
    private AMapLocationListener amapLisr = new AMapLocationListener() {
        
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
            latitude = aLocation.getLatitude();
            longtitude = aLocation.getLongitude();
            if (Double.isNaN(latitude) || Double.isNaN(longtitude)) {
                Log.e(TAG, "坐标值double溢出：" + latitude + "," + longtitude);
                return;
            }
            
            //地址反编码，第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longtitude), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
            
            String old = mPrefs.getString(Config.LATITUDE, null) + "," + mPrefs.getString(Config.LONGTITUDE, null);
            String newInfo = String.valueOf(latitude) + "," + String.valueOf(longtitude);
            
            //Log.d(TAG, "coordinate:old="+old+",new="+newInfo);
            if (!newInfo.equals(old)) {
                Editor edit = mPrefs.edit();
                edit.putString(Config.LATITUDE, String.valueOf(latitude));
                edit.putString(Config.LONGTITUDE, String.valueOf(longtitude));
                edit.commit();
                
                //发送接收到定位信息的广播
                Intent intent = new Intent(BroadcastAction.COORDINATE);
                intent.putExtra(Config.LATITUDE, latitude);
                intent.putExtra(Config.LONGTITUDE, longtitude);
                sendBroadcast(intent);
            }
        }
    };
    
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
                    List<PoiItem> pois = geoAddress.getPois();
                    List<Crossroad> crosss = geoAddress.getCrossroads();
                    List<RegeocodeRoad> roads = geoAddress.getRoads();
                    String poiname = null;
                    LatLonPoint pt = null;
                    if (pois != null && pois.size() > 0) {
                        PoiItem item = pois.get(0);
                        poiname = item.getTitle();
                        pt = item.getLatLonPoint();
                    } else {
                        if (address != null && address.indexOf(province + city + distinct) > -1) {
                            String prefix = province + city + distinct;
                            int preIndex = 0;
                            if (!TextUtils.isEmpty(prefix)) {
                                preIndex = prefix.length();
                            }
                            poiname = address.substring(preIndex);
                        } else
                            poiname = address;
                    }
                    
                    String building = geoAddress.getBuilding();
                    String neigh = geoAddress.getNeighborhood();
                    String township = geoAddress.getTownship();
                    StreetNumber street = geoAddress.getStreetNumber();
                    String streetStr = street.getStreet();
                    
                    //Log.d(TAG, address+"|"+pois+"|"+building+"|"+crosss+"|"+city+"|"+distinct+"|"+neigh+"|"+prov+"|"+roads+"|"+street+"|"+township);
                    
                    GaoDeAddress gdAddress = new GaoDeAddress(city, address, poiname, latitude, longtitude);
                    gdAddress.setAdcode(adcode);
                    gdAddress.setBuilding(building);
                    gdAddress.setDistinct(distinct);
                    gdAddress.setNeighborhood(neigh);
                    gdAddress.setProvince(province);
                    gdAddress.setStreet(streetStr);
                    gdAddress.setTownship(township);
                    
                    String old = mPrefs.getString(Config.GAODE_ADDRESS, null);
                    String newAddress = JSON.toJSONString(gdAddress);
                    String newAddress2 = gdAddress.getAddress();
                    Log.d(TAG, newAddress2);
                    //Log.d(TAG, "address: old=" + old + ",newAddress=" + newAddress);
                    if (!newAddress.equals(old)) {
                        
                        Editor edit = mPrefs.edit();
                        edit.putString(Config.GAODE_ADDRESS, newAddress);
                        edit.putString(Config.GAODE_ADDRESS2, newAddress2);
                        edit.commit();
                        
                        //发送地址反编码成功广播
                        Intent intent = new Intent(BroadcastAction.ADDRESS);
                        intent.putExtra(Config.GAODE_ADDRESS, gdAddress);
                        sendBroadcast(intent);
                    }
                    /* 
                    //应用后台运行，关闭服务
                    boolean currForeground = ActivitysManager.isRunningForeground(mContext);
                    //当前前台，但是是后台定位，修改为前台定位
                    if (!foreground && currForeground) {
                        Log.d(TAG, "后台切换到前台--back to fore");
                        foreground = true;
                        restart();
                    } else if (foreground && !currForeground) {
                        Log.d(TAG, "前台切换到后台--fore to back");
                        foreground = false;
                        restart();
                    }*/
                    
                    checkHandler.postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            Log.d(TAG, "刷新定位");
                            //应用后台运行，关闭服务
                            /*if(foreground && ActivitysManager.getLocalTopActivity() == null){
                                //应用切换到后台
                                Log.d(TAG, "前台切换到后台--fore to back");
                                foreground = false;
                                restart();
                            } else if(!foreground && ActivitysManager.getLocalTopActivity() != null){
                                Log.d(TAG, "后台切换到前台--back to fore");
                                foreground = true;
                                restart();
                            }*/
                            //应用后台运行，关闭服务
                            boolean currForeground = ActivitysManager.isRunningForeground(mContext);
                            //当前前台，但是是后台定位，修改为前台定位
                            if (!foreground && currForeground) {
                                Log.d(TAG, "后台切换到前台--back to fore");
                                foreground = true;
                                restart();
                            } else if (foreground && !currForeground) {
                                Log.d(TAG, "前台切换到后台--fore to back");
                                foreground = false;
                                restart();
                            }
                        }
                    }, 10 * 1000);
                }
                
            }
        }
        
        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
        }
    };
    
    List<TrafficRoad> roadTraffic = new ArrayList<TrafficRoad>();
    private OnRouteSearchListener routeSearchLisr = new OnRouteSearchListener() {
        
        /** 
         * 设置poi查询点击的历史记录。
         *
         */
        public void searchPathTraffic(List<DriveStep> steps) {
            if(steps == null || steps.size() == 0) return;
            int count = steps.size();
            //移除重复的路径和为空的路径
            List<DriveStep> ds = new ArrayList<DriveStep>();
            Iterator<DriveStep> it = steps.iterator();
            while (it.hasNext()) {
                DriveStep d = it.next();
                if(TextUtils.isEmpty(d.getRoad())) continue;
                boolean isExsit = false;
                Iterator<DriveStep> it2 = ds.iterator();
                while (it2.hasNext()) {
                    DriveStep dd = it2.next();
                    if(d.getRoad().equals(dd.getRoad())){
                        isExsit = true;
                        break;
                    }
                }
                if(!isExsit){
                    ds.add(d);
                }
            }
            steps.clear();
            steps.addAll(ds);
            //增加路况展示
            for (int i = 0; i < steps.size(); i++) {
                DriveStep step = steps.get(i);
                //searchTraffic(step.getRoad());
            }
        }
        
        @Override
        public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
            
        }
        
        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
            if (rCode == 0) {
                if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                    DriveRouteResult driveRouteResult = result;
                    DrivePath drivePath = driveRouteResult.getPaths().get(0);
                    //显示路况信息到页面上来
                    DrivePath path = driveRouteResult.getPaths().get(0);
                    List<DriveStep> steps = new ArrayList<DriveStep>();
                    DecimalFormat df = new DecimalFormat("#0.0");
                    String feeTip =
                        "<font color='red'>" + df.format(path.getDistance() / 1000) + "</font>公里   打车约<font color='red'>"
                            + df.format((6 + path.getDistance() / 1000 * 1.8)) + "</font>元";
                    searchPathTraffic(path.getSteps());
                }
            }
        }
        
        @Override
        public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
        }
        /*
        public void searchTraffic(String search) {
            final String road = search;
            Map<String, String> params = new HashMap<String, String>();
            params.put("keywords", road);
            params.put("key", "ctbhndx2013");
            Log.d(TAG, params.toString());
            
            final Requester req = new Requester(mContext);
            req.request(new RequestListener() {
                
                @Override
                public void requestStatusChanged(int statusCode, HttpUrlImpl requestId, String responseString) {
                    try {
                        if (statusCode == RequestStatus.SUCCESS) {
                            JSONObject jsObj = JsonFriend.parseJSONObject(responseString);
                            String trafficInfo = jsObj.getString("trafficInfo");
                            String ret = jsObj.getString("ret");
                            if ("200".equals(ret)) {
                                JsonFriend<TrafficInfo> js = new JsonFriend<TrafficInfo>(TrafficInfo.class);
                                TrafficInfo ti = js.parseObject(trafficInfo);
                                if (ti != null) {
                                    Log.d(TAG, ti.toString());
                                    List<TrafficStatus> tss = ti.getTrafficStatus();
                                    if (tss == null) {
                                        return;
                                    }
                                    boolean succ = false;
                                    for (int i = 0; i < tss.size(); i++) {
                                        TrafficStatus ts = tss.get(i);
                                        String desc = ts.getDesc();
                                        if (!TextUtils.isEmpty(desc)) {
                                            String[] infos = desc.split("[|]");
                                            if (infos != null && infos.length > 0) {
                                                for (String str : infos) {
                                                    //结果中包含road信息
                                                    if (str != null && str.contains(road)) {
                                                        String[] arr = str.split("：");
                                                        if (arr.length >= 2) {
                                                            TrafficRoad r = new TrafficRoad(arr[0], arr[1]);
                                                            roadTraffic.add(r);
                                                        }
                                                        break;
                                                    } else {
                                                        if (str != null) {
                                                            String[] arr = str.split("：");
                                                            if (arr.length >= 2) {
                                                                TrafficRoad r = new TrafficRoad(arr[0], arr[1]);
                                                                roadTraffic.add(r);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                HttpUrlImpl.CTB.REALTIME_ROUTE_TRAFFIC,
                Utily.getWholeUrl(HttpUrlImpl.CTB.REALTIME_ROUTE_TRAFFIC),
                params,
                HttpRequest.POST,
                false);
        }*/
    };
    
    private boolean foreground = true;
    
    private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            boolean hasConnectivity = (info != null && info.isConnected()) ? true : false;
            //根据网络状态和应用当前是否在前台运行，来决定是否需要开启定位
            if (hasConnectivity && ActivitysManager.getLocalTopActivity() != null) {
                foreground = true;
                restart();
                Log.d(TAG, "开启前台定位，时间间隔5秒");
            } else if (hasConnectivity) {
                foreground = false;
                restart();
                Log.d(TAG, "开启后台定位，时间间隔6*60秒");
            } else {
                deactivate();
                Log.d(TAG, "无网络关闭定位");
            }
        }
    };
    
    public void setAlarm(int type){
        /*if(type == 3){
            //家闹铃
            long distance = getAlarmTime(userMgr.homeTime, userMgr.homeWeek);
            if(distance > 0 ){
                startAlarm(3, userMgr.home, distance);
            }
        }else if(type == 4){
            //公司闹铃
            long distance = getAlarmTime(userMgr.companyTime, userMgr.companyWeek);
            if(distance > 0 ){
                startAlarm(4, userMgr.company, distance);
            }
        }*/
    }
    
    public void searchRoute(PoiInfo poi){
        try {
            //当前位置
            LatLonPoint startPoint = null;
            if(latitude != 0 && longtitude != 0){
                startPoint = new LatLonPoint(latitude,longtitude);
            } else {
                double latitude = Double.valueOf(mPrefs.getString(LocationService.Config.LATITUDE, "0"));
                double longtitude = Double.valueOf(mPrefs.getString(LocationService.Config.LONGTITUDE, "0"));
                if(latitude == 0) return;
                startPoint = new LatLonPoint(latitude,longtitude);
            }
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, poi.getPoint());
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * 获取到下次引爆的时间间隔。
     *
     * @param poi
     * @param timeStr
     * @param weekArr
     * @return
     */
    public long getAlarmTime(String timeStr,int[] weekArr){
        long distance = 0;//距离下次定时间隔
        if(!TextUtils.isEmpty(timeStr)){
            String[] timeArr = timeStr.split(":");
            int hour = Integer.parseInt(timeArr[0].trim());
            int minute = Integer.parseInt(timeArr[1].trim());
            Calendar cal = DateTimeUtils.getCurrentDateTime();
            Log.d(TAG, cal.toString());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int myDayOfWeek = dayOfWeek-2;//本地定义的星期规则，为java标准减去2
            if(myDayOfWeek < 0) myDayOfWeek = myDayOfWeek + 7;//小于0是星期日，加7得到值为6
            //查找从今天到下一次最近的有效时间点
            for(int i = myDayOfWeek; i< myDayOfWeek + 7; i++){
                if(i >= 7){
                    if(weekArr[i-7] == 1){
                        Calendar next = (Calendar)cal.clone();
                        next.set(Calendar.AM_PM, Calendar.AM);
                        next.set(Calendar.HOUR, 0);
                        next.set(Calendar.MINUTE, 0);
                        next.set(Calendar.SECOND,0);
                        next.add(Calendar.DAY_OF_MONTH, i-myDayOfWeek);
                        next.add(Calendar.HOUR, hour);
                        next.add(Calendar.MINUTE, minute);
                        distance = DateTimeUtils.getMillBetween(next,cal);
                        if(distance <= 0){
                            continue;
                        }
                        break;
                    }
                } else if(weekArr[i] == 1){
                    Calendar next = (Calendar)cal.clone();
                    next.set(Calendar.AM_PM, Calendar.AM);
                    next.set(Calendar.HOUR, 0);
                    next.set(Calendar.MINUTE, 0);
                    next.set(Calendar.SECOND,0);
                    next.add(Calendar.DAY_OF_MONTH, i-myDayOfWeek);
                    next.add(Calendar.HOUR, hour);
                    next.add(Calendar.MINUTE, minute);
                    distance = DateTimeUtils.getMillBetween(next,cal);
                    Log.d(TAG, next.toString());
                    Log.d(TAG, "timelong:"+distance+"");
                    if(distance <= 0){
                        continue;
                    }
                    break;
                }
            }
        }
        return distance;
    }
    
    /** 
     *  启动闹铃。
     *
     * @param type 家或公司
     * @param poi  家或公司地理位置信息
     * @param time  指定时间后，单位毫秒
     */
    public void startAlarm(int type,PoiInfo poi,long time){
        Intent i = new Intent(mContext, LocationService.class);
        i.putExtra("type", type);
        i.putExtra("poi", poi);
        i.setAction(ACTION_ALARM);
        i.addCategory(""+type);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        //指定时间后引爆闹铃
        //Determine if two intents are the same for the purposes of intent resolution (filtering).
        //That is, if their action, data, type, class, and categories are the same. This does not compare any extra data included in the intents.
        mAlarmMgr.cancel(pendingIntent);
        mAlarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);
    }
    
    public Notification initNotification() {
        notification = new Notification(R.drawable.ic_launcher, "您有新的消息", System.currentTimeMillis());
        //屏幕亮、不可手动清空
        notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_NO_CLEAR | Notification.FLAG_AUTO_CANCEL;
        //默认声音和振动
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        return notification;
    }
}
