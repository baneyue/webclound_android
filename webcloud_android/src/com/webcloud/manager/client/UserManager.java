package com.webcloud.manager.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.LatLonPoint;
import com.funlib.json.JsonFriend;
import com.webcloud.WebCloudApplication;
import com.webcloud.define.BusiPoiType;
import com.webcloud.model.PoiInfo;

/**
 * 图片缓存管理器。
 * a
 * @author  bangyue
 * @version  [版本号, 2013-12-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class UserManager {
    public static final String TAG = "UserManager";
    
    public static final String CACHE_POI_LIST = "busiPoiList";
    
    public static final String CACHE_HOME = "home";
    
    public static final String CACHE_HOME_TIME = "homeTime";
    
    public static final String CACHE_HOME_WEEK = "homeWeek";
    
    public static final String CACHE_COMPANY = "company";
    
    public static final String CACHE_COMPANY_TIME = "companyTime";
    
    public static final String CACHE_COMPANY_WEEK = "companyWeek";
    
    /**导航历史记录*/
    public static final String CACHE_DAOHANG = "daohanghis";
    
    /**poi搜索历史记录*/
    public static final String CACHE_POI = "poihis";
    
    Context ctx;
    
    public SharedPreferences mCachePrefs;
    
    //家和公司的日期设置
    public int[] homeWeek = new int[] {1, 1, 1, 1, 1, 0, 0};
    
    public int[] companyWeek = new int[] {1, 1, 1, 1, 1, 0, 0};
    
    public String homeTime = "17:20";
    
    public String companyTime = "7:30";
    
    public PoiInfo home, company;
    
    /**poi历史查询记录,只在缓存中保存3条记录，新的记录会把旧的记录替换*/
    public List<PoiInfo> poiHis = new ArrayList<PoiInfo>();
    
    /**导航历史记录数据对,只在缓存中保存3条记录，新的记录会把旧的记录替换*/
    public List<Map<String, PoiInfo>> daohangList = new ArrayList<Map<String, PoiInfo>>();
    
    public UserManager(Context ctx) {
        this.ctx = WebCloudApplication.getInstance();
        mCachePrefs = this.ctx.getSharedPreferences(UserManager.TAG, Context.MODE_PRIVATE);
        parseHomeCache();
        parseCompanyCache();
        parsePoiSearchHistoryCache();
        parseDaohangCache();
    }
    
    /**从缓存中取得家和公司数据,并解析成指定格式*/
    public void parseHomeCache() {
        String homeStr = mCachePrefs.getString(CACHE_HOME, null);
        homeTime = mCachePrefs.getString(CACHE_HOME_TIME, "17:20");
        
        String homeW = mCachePrefs.getString(CACHE_HOME_WEEK, null);
        
        try {
            if (homeStr != null) {
                JSONObject homeJs = JsonFriend.parseJSONObject(homeStr);
                if (homeJs != null) {
                    home = new PoiInfo();
                    home.setType(BusiPoiType.L_HOME);
                    home.setAddress(homeJs.getString("address"));
                    home.setName(homeJs.getString("name"));
                    home.setPoint(new LatLonPoint(homeJs.getJSONObject("point").getDoubleValue("latitude"),
                        homeJs.getJSONObject("point").getDoubleValue("longitude")));
                }
            }
            
            if (homeW != null) {
                JsonFriend<Integer> js = new JsonFriend<Integer>(Integer.class);
                List<Integer> ii = js.parseArray(homeW);
                if (ii != null && ii.size() == 7) {
                    for (int i = 0; i < ii.size(); i++) {
                        homeWeek[i] = ii.get(i);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**从缓存中取得家和公司数据,并解析成指定格式*/
    public void parseCompanyCache() {
        String companyStr = mCachePrefs.getString(CACHE_COMPANY, null);
        
        companyTime = mCachePrefs.getString(CACHE_COMPANY_TIME, "7:30");
        
        String companyW = mCachePrefs.getString(CACHE_COMPANY_WEEK, null);
        
        try {
            if (companyStr != null) {
                JSONObject companyJs = JsonFriend.parseJSONObject(companyStr);
                if (companyJs != null) {
                    company = new PoiInfo();
                    company.setType(BusiPoiType.L_COMPANY);
                    company.setAddress(companyJs.getString("address"));
                    company.setName(companyJs.getString("name"));
                    company.setPoint(new LatLonPoint(companyJs.getJSONObject("point").getDoubleValue("latitude"),
                        companyJs.getJSONObject("point").getDoubleValue("longitude")));
                }
            }
            if (companyW != null) {
                JsonFriend<Integer> js = new JsonFriend<Integer>(Integer.class);
                List<Integer> ii = js.parseArray(companyW);
                if (ii != null && ii.size() == 7) {
                    for (int i = 0; i < ii.size(); i++) {
                        companyWeek[i] = ii.get(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** 
     * 解析poi手动查询历史记录。
     *
     */
    public void parsePoiSearchHistoryCache() {
        String poiStr = mCachePrefs.getString(CACHE_POI, null);
        poiHis.clear();
        try {
            if (poiStr != null) {
                JSONArray poiJs = JsonFriend.parseJSONArray(poiStr);
                if (poiJs != null && poiJs.size() > 0) {
                    Iterator<Object> it = poiJs.iterator();
                    while (it.hasNext()) {
                        Object obj = it.next();
                        if (!(obj instanceof JSONObject)) {
                            continue;
                        }
                        JSONObject jo = (JSONObject)obj;
                        PoiInfo stPoi = new PoiInfo();
                        stPoi.setType(BusiPoiType.L_SEARCH);
                        stPoi.setAddress(jo.getString("address"));
                        stPoi.setName(jo.getString("name"));
                        JSONObject point = jo.getJSONObject("point");
                        stPoi.setPoint(new LatLonPoint(point.getDoubleValue("latitude"),
                            point.getDoubleValue("longitude")));
                        
                        poiHis.add(stPoi);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** 
     * 解析导航缓存。
     */
    public void parseDaohangCache() {
        String daoStr = mCachePrefs.getString(CACHE_DAOHANG, null);
        daohangList.clear();
        try {
            if (daoStr != null) {
                JSONArray daoJs = JsonFriend.parseJSONArray(daoStr);
                if (daoJs != null && daoJs.size() > 0) {
                    Iterator<Object> it = daoJs.iterator();
                    while (it.hasNext()) {
                        Object obj = it.next();
                        if (!(obj instanceof JSONObject)) {
                            continue;
                        }
                        try {
                            JSONObject jo = (JSONObject)obj;
                            JSONObject stJo = jo.getJSONObject("start");
                            JSONObject enJo = jo.getJSONObject("end");
                            
                            PoiInfo stPoi = new PoiInfo();
                            stPoi.setType(BusiPoiType.L_START);
                            stPoi.setAddress(stJo.getString("address"));
                            stPoi.setName(stJo.getString("name"));
                            JSONObject point = stJo.getJSONObject("point");
                            stPoi.setPoint(new LatLonPoint(point.getDoubleValue("latitude"),
                                point.getDoubleValue("longitude")));
                            
                            PoiInfo enPoi = new PoiInfo();
                            enPoi.setType(BusiPoiType.L_END);
                            enPoi.setAddress(enJo.getString("address"));
                            enPoi.setName(enJo.getString("name"));
                            point = enJo.getJSONObject("point");
                            enPoi.setPoint(new LatLonPoint(point.getDoubleValue("latitude"),
                                point.getDoubleValue("longitude")));
                            
                            Map<String, PoiInfo> poiMap = new HashMap<String, PoiInfo>();
                            poiMap.put("start", stPoi);
                            poiMap.put("end", enPoi);
                            daohangList.add(poiMap);
                        } catch (Exception e) {
                            it.remove();
                            //覆盖老记录
                            saveCache(UserManager.CACHE_DAOHANG, "");
                            parseDaohangCache();
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** 
     * 保存缓存。
     * 当保存的内容为空或者和已缓存内容不同时替换缓存。
     *
     * @param cacheName
     * @param data
     */
    public void saveCache(String cacheName, String data) {
        String cache = mCachePrefs.getString(cacheName, null);
        if (data == null || !data.equals(cache)) {
            Editor edit = mCachePrefs.edit();
            edit.putString(cacheName, data);
            edit.commit();
        }
    }
    
    /*public void parseUserAddress(ArrayList<Reminds> reminds) {
        if (reminds == null || reminds.size() == 0)
            return;
        for (Reminds remind : reminds) {
            if ("1".equals(remind.getRemindtype())) {
                LatLonPoint pt = null;
                if (RegexUtil.isPostiveRealDigit(remind.getRemindlat())
                    && RegexUtil.isPostiveRealDigit(remind.getRemindlong())) {
                    double lat = Double.parseDouble(remind.getRemindlat());
                    double lng = Double.parseDouble(remind.getRemindlong());
                    if (lat == 0 || lng == 0)
                        continue;
                    pt = new LatLonPoint(lat, lng);
                } else {
                    continue;
                    //pt = new LatLonPoint(28, 113);
                }
                PoiInfo poi = new PoiInfo(pt, remind.getRemindname(), remind.getRemindaddress(), null);
                poi.setType(BusiPoiType.L_COMPANY);
                
                company = poi;
                
                if (!TextUtils.isEmpty(remind.getRemindday())) {
                    String day = remind.getRemindday();
                    if (day.length() == 7) {
                        for (int i = 0; i < day.length(); i++) {
                            if(String.valueOf(day.charAt(i)).matches("\\d+")){
                                companyWeek[i] = Integer.parseInt(String.valueOf(day.charAt(i)));
                            } else {
                                companyWeek[i] = 0;
                            }
                        }
                    }
                }
                if (!TextUtils.isEmpty(remind.getRemindtime()) && remind.getRemindtime().contains(":")) {
                    String[] arr = remind.getRemindtime().split(":");
                    if(arr.length == 3){
                        companyTime = arr[0]+":"+arr[1];//remind.getRemindtime();
                    }
                    //companyTime = remind.getRemindtime();
                }
            } else if ("2".equals(remind.getRemindtype())) {
                LatLonPoint pt = null;
                if (RegexUtil.isPostiveRealDigit(remind.getRemindlat())
                    && RegexUtil.isPostiveRealDigit(remind.getRemindlong())) {
                    double lat = Double.parseDouble(remind.getRemindlat());
                    double lng = Double.parseDouble(remind.getRemindlong());
                    if (lat == 0 || lng == 0)
                        continue;
                    pt = new LatLonPoint(lat, lng);
                } else {
                    continue;
                    //pt = new LatLonPoint(28, 113);
                }
                PoiInfo poi = new PoiInfo(pt, remind.getRemindname(), remind.getRemindaddress(), null);
                poi.setType(BusiPoiType.L_HOME);
                
                home = poi;
                
                if (!TextUtils.isEmpty(remind.getRemindday())) {
                    String day = remind.getRemindday();
                    if (day.length() == 7) {
                        for (int i = 0; i < day.length(); i++) {
                            if(String.valueOf(day.charAt(i)).matches("\\d+")){
                                homeWeek[i] = Integer.parseInt(String.valueOf(day.charAt(i)));
                            } else {
                                homeWeek[i] = 0;
                            }
                        }
                    }
                }
                if (!TextUtils.isEmpty(remind.getRemindtime()) && remind.getRemindtime().contains(":")) {
                    String[] arr = remind.getRemindtime().split(":");
                    if(arr.length == 3){
                        homeTime = arr[0]+":"+arr[1];//remind.getRemindtime();
                    }
                }
            }
        }
        //启动定时任务
        LocationService.actionStartAlarm(ctx, 3);
        LocationService.actionStartAlarm(ctx, 4);
    }*/
}
