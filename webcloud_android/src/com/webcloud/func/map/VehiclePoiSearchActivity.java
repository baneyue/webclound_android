package com.webcloud.func.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.funlib.json.JsonFriend;
import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.component.MessageDialog;
import com.webcloud.component.MessageDialogListener;
import com.webcloud.component.NewToast;
import com.webcloud.define.BusiPoiType;
import com.webcloud.func.map.adapter.RouteSearchAdapter;
import com.webcloud.func.voice.IatPreferenceActivity;
import com.webcloud.func.voice.TtsPreferenceActivity;
import com.webcloud.func.voice.VoiceIatHolder;
import com.webcloud.func.voice.VoiceTextListener;
import com.webcloud.func.voice.VoiceTtsHolder;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.UserManager;
import com.webcloud.model.GaoDeAddress;
import com.webcloud.model.PoiInfo;
import com.webcloud.service.LocationService;

/**
 * 搜寻poi点。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VehiclePoiSearchActivity extends BaseActivity implements OnClickListener, VoiceTextListener {
    public static final String TAG = "VehiclePoiSearchActivity";
    
    private EditText etSearchInput;
    
    private ImageView btnClear;
    
    private ImageView btnVoice;
    
    private Button btnBack;
    
    private Button btnTts;
    
    private View btnUseCurr;
    
    private String poiName;
    
    private VoiceTtsHolder vcTtsHolder;
    
    private VoiceIatHolder voiceIatHolder;
    
    public SharedPreferences mCachePrefs;
    
    VehiclePoiSearchMapViewHolder mapHolder;
    
    public String cityName;
    
    View laySearchResult;
    
    public int searchType = 0;//1查询起点,2查询终点,3查询家，4查询公司,0无效
    
    /**搜索列表历史记录列表数据*/
    public List<PoiInfo> poiHis = new ArrayList<PoiInfo>();
    
    View layPoiSearch;
    
    PoiSearchViewHolder pvh;
    
    public static MessageDialog dialog = new MessageDialog();
    
    private UserManager userMgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_poi_search);
        userMgr = SystemManager.getInstance(getApplication()).userMgr;
        mCachePrefs = this.getSharedPreferences(LocationService.TAG, MODE_PRIVATE);
        mapHolder = new VehiclePoiSearchMapViewHolder(this, savedInstanceState);
        initView();
        Intent intent = getIntent();
        if (intent != null) {
            searchType = intent.getIntExtra("searchType", 1);
        }
        initMapData();
        registeReceiver();
    }
    
    public void initView() {
        laySearchResult = (View)findViewById(R.id.laySearchResult);
        btnClear = (ImageView)findViewById(R.id.btnClear);
        btnVoice = (ImageView)findViewById(R.id.btnVoice);
        btnTts = (Button)findViewById(R.id.btnTts);
        btnBack = (Button)findViewById(R.id.btnBack);
       // btnUseCurr = findViewById(R.id.btnUseCurr);
        btnBack.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnTts.setOnClickListener(this);
        btnUseCurr.setOnClickListener(this);
        etSearchInput = (EditText)findViewById(R.id.etSearchInput);
        etSearchInput.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    layPoiSearch.setVisibility(View.VISIBLE);
                    if (poiHis.size() != 0) {
                        setPoiSearchHis();
                    }
                    
                    //输入框为空，且历史记录不为空，则显示历史记录到列表中
                    if (TextUtils.isEmpty(etSearchInput.getText().toString()) && poiHis.size() != 0) {
                        showHistoryList();
                    } else if (pvh.poiItems.size() > 0) {
                        if(pvh.routeSearchAdapter.isHistory()){
                            showHistoryList();
                        }else
                            showSearchList(null);
                    }
                }
                return false;
            }
        });
        btnClear.setOnClickListener(this);
        //非常重要：编辑框中只要有内容，就显示列表页，以及遮罩层，没有内容就显示，缓存结果，以及地图选点操作
        etSearchInput.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    btnClear.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.VISIBLE);
                } else {
                    btnVoice.setVisibility(View.GONE);
                    btnClear.setVisibility(View.VISIBLE);
                }
                String input = s.toString();
                //输入长度大于2才执行查询
                if (!TextUtils.isEmpty(input)) {
                    //查询poi，显示列表层
                    pvh.laySearchResult.setVisibility(View.VISIBLE);
                    mapHolder.startSearchResult(input);
                } else {
                    //隐藏列表层，显示历史记录及地图选点层。
                    pvh.laySearchResult.setVisibility(View.GONE);
                    //输入为空，判断有无历史记录，有就显示出来
                    if (poiHis.size() != 0) {
                        showHistoryList();
                    } else {
                        clearList();
                    }
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }
        });
        vcTtsHolder = new VoiceTtsHolder(this, this);
        voiceIatHolder = new VoiceIatHolder(this, this);
        //初始化poi搜索层控件
        pvh = new PoiSearchViewHolder();
        layPoiSearch = findViewById(R.id.layPoiSearch);
        pvh.layHistory = layPoiSearch.findViewById(R.id.layHistory);
        pvh.laySearchResult = layPoiSearch.findViewById(R.id.laySearchResult);
        pvh.laySelInMap = layPoiSearch.findViewById(R.id.laySelInMap);
        pvh.lvSearchResult = (ListView)layPoiSearch.findViewById(R.id.lvSearchResult);
        pvh.svDaoHang = (ScrollView)layPoiSearch.findViewById(R.id.svDaoHang);
        pvh.tvClearHis = (TextView)layPoiSearch.findViewById(R.id.tvClearHis);
        pvh.layInputMask = layPoiSearch.findViewById(R.id.layInputMask);
        pvh.layInputMask.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //屏蔽下方
                return true;
            }
        });
        pvh.tvClearHis.setOnClickListener(this);
        pvh.laySelInMap.setOnClickListener(this);
        pvh.routeSearchAdapter = new RouteSearchAdapter(this, pvh.poiItems);
        pvh.lvSearchResult.setAdapter(pvh.routeSearchAdapter);
        pvh.lvSearchResult.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo startpoiItem = pvh.poiItems.get(position);
                //把点标记到到地图上，并且保持查询历史记录，最多保存3条
                poiHis.add(0, startpoiItem);
                if (poiHis.size() > 3) {
                    Iterator<PoiInfo> it = poiHis.iterator();
                    for (int i = 0; it.hasNext(); i++) {
                        it.next();
                        if (i >= 3)
                            it.remove();
                    }
                }
                saveHomeCompanyCache(UserManager.CACHE_POI, JSON.toJSONString(poiHis));
                //返回上一页面已选择的值
                
                backToLastPage(startpoiItem);
                VehiclePoiSearchActivity.this.finish();
            }
        });
    }
    
    /**从缓存中取得家和公司数据,并解析成指定格式*/
    public void parseHomeCompanyCache() {
        String poiStr = mCachePrefs.getString(UserManager.CACHE_POI, null);
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
                        stPoi.setPoint(new LatLonPoint(point.getDoubleValue("latitude"), point.getDoubleValue("longitude")));
                        
                        poiHis.add(stPoi);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPoiSearchHis();
    }
    
    /** 
     * 设置poi查询点击的历史记录。
     *
     */
    public void setPoiSearchHis() {
        pvh.svDaoHang.removeAllViews();
        LinearLayout lin = new LinearLayout(this);
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.setGravity(Gravity.CENTER);
        
        int count = poiHis.size();
        //显示默认则最多只显示前两条
        if (count == 0) {
            pvh.layHistory.setVisibility(View.GONE);
        } else {
            pvh.layHistory.setVisibility(View.VISIBLE);
            pvh.svDaoHang.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        }
        //增加路况展示
        for (int i = 0; i < count; i++) {
            PoiInfo poi = poiHis.get(i);
            FrameLayout fLayout =
                (FrameLayout)getLayoutInflater().inflate(R.layout.vehicle_map_daohang_history_item, null);
            //TextView tvStart = (TextView)fLayout.findViewById(R.id.tvStart);
            TextView tvEnd = (TextView)fLayout.findViewById(R.id.tvEnd);
            //tvStart.setSelected(true);
            tvEnd.setSelected(true);
            //tvStart.setText(poi.getName());
            //tvStart.setTag(poi);
            tvEnd.setText(poi.getName());
            tvEnd.setTag(poi);
            
            fLayout.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //TextView tvStart = (TextView)v.findViewById(R.id.tvStart);
                    TextView tvEnd = (TextView)v.findViewById(R.id.tvEnd);
                    //PoiInfo stPoi = (PoiInfo)tvStart.getTag();
                    //把此点标记在地图上，作为一个临时点
                    PoiInfo enPoi = (PoiInfo)tvEnd.getTag();
                    //setRoutePoiInfo(1, stPoi);
                    //setRoutePoiInfo(2, enPoi);
                    layPoiSearch.setVisibility(View.GONE);
                    //mapHolder.initSearchMark(true, enPoi);
                    backToLastPage(enPoi);
                }
            });
            lin.addView(fLayout);
        }
        pvh.svDaoHang.addView(lin);
    }
    
    /** 
     * 保存家或公司缓存。
     *
     * @param cacheName
     * @param data
     * @see [类、类#方法、类#成员]
     */
    public void saveHomeCompanyCache(String cacheName, String data) {
        String cache = mCachePrefs.getString(cacheName, null);
        if (data == null || !data.equals(cache)) {
            Editor edit = mCachePrefs.edit();
            edit.putString(cacheName, data);
            edit.commit();
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            searchType = intent.getIntExtra("searchType", 1);
        }
    }
    
    public void showHistoryList() {
        try {
            pvh.routeSearchAdapter.setHistory(true);
            pvh.poiItems.clear();
            pvh.poiItems.addAll(poiHis);
            pvh.routeSearchAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void clearList() {
        pvh.routeSearchAdapter.setHistory(true);
        pvh.poiItems.clear();
        pvh.routeSearchAdapter.notifyDataSetChanged();
    }
    
    public void setAddressByRecoderSearch(PoiInfo poi) {
        etSearchInput.setTag(poi);
        etSearchInput.setText(poi.getName());
        
        backToLastPage(poi);
    }
    
    public void showSearchList(List<PoiInfo> tempInfos) {
        try {
            pvh.routeSearchAdapter.setHistory(false);
            if (tempInfos != null && tempInfos.size() > 0) {
                pvh.poiItems.clear();
                pvh.poiItems.addAll(tempInfos);
            }
            pvh.routeSearchAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initMapData() {
        //初始化当前位置和城市名称
        LatLng pointCurr = null;
        String cityName = null;
        try {
            double latitude = Double.valueOf(mCachePrefs.getString(LocationService.Config.LATITUDE, "0"));
            double longtitude = Double.valueOf(mCachePrefs.getString(LocationService.Config.LONGTITUDE, "0"));
            pointCurr = new LatLng(latitude, longtitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String gdAddressStr = mCachePrefs.getString(LocationService.Config.GAODE_ADDRESS, null);
        //这个地址bean中的address是：省+城市名+区名+poiname
        //城市名包含市
        JsonFriend<GaoDeAddress> gdJs = new JsonFriend<GaoDeAddress>(GaoDeAddress.class);
        GaoDeAddress gdAddress = gdJs.parseObject(gdAddressStr);
        if (gdAddress != null) {
            cityName = gdAddress.getCityname();
        }
        
        mapHolder.setCurrPoint(pointCurr);
        mapHolder.setCityName(cityName);
    }
    
    @Override
    public void onBackPressed() {
        if(layPoiSearch.getVisibility() == View.VISIBLE){
            layPoiSearch.setVisibility(View.GONE);
        } else
          super.onBackPressed();
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                etSearchInput.setText("");
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnVoice:
                voiceIatHolder.showIat();
                break;
            case R.id.btnTts: {
                vcTtsHolder.showTts();
            }
                break;
            /*case R.id.btnUseCurr: {
                PoiInfo poi = mapHolder.getCurrAddress();
                if(poi != null){
                    backToLastPage(poi);
                }
            }
                break;*/
            case R.id.tvClearHis:
                //清空导航历史
                dialog.showDialogOKCancel(VehiclePoiSearchActivity.this, "是否清空选择记录？", new MessageDialogListener() {
                    
                    @Override
                    public void onMessageDialogClick(MessageDialog dialog, int which) {
                        if (which == MessageDialog.MESSAGEDIALOG_OK) {
                            dialog.dismissMessageDialog();
                            //启动网络设置界面
                            try {
                                poiHis.clear();
                                userMgr.saveCache(UserManager.CACHE_POI, JSON.toJSONString(poiHis));
                                setPoiSearchHis();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dialog.dismissMessageDialog();
                        }
                    }
                });
                break;
            default:
                break;
        }
    };
    
    /** 
     * 返回到上一页面。
     *
     * @see [类、类#方法、类#成员]
     */
    public void backToLastPage(PoiInfo poi){
        Intent data = new Intent();
        data.putExtra("poiItem", JSON.toJSONString(poi));
        data.putExtra("searchInput", poi.getName());
        //返回码随便设
        VehiclePoiSearchActivity.this.setResult(searchType, data);
        VehiclePoiSearchActivity.this.onBackPressed();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "合成设置").setIcon(android.R.drawable.ic_menu_manage);
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, "转写设置").setIcon(android.R.drawable.ic_menu_manage);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                //跳转到语音合成页面
                startActivity(new Intent(this, TtsPreferenceActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case Menu.FIRST + 2:
                //跳转到语音转写页面
                startActivity(new Intent(this, IatPreferenceActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        vcTtsHolder.release();
    }
    
    @Override
    public String getText() {
        String tts = "";
        if (TextUtils.isEmpty(tts)) {
            tts = "抱歉！您输入的" + poiName + "没有查询到路况信息，请确认您输入的地点是否正确。";
        }
        return tts;
    }
    
    @Override
    public void setText(String txt) {
        etSearchInput.setText(txt);
    }
    
    @Override
    public void addText(String txt) {
        String temp = etSearchInput.getText().toString();
        etSearchInput.setText(temp + txt);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceIatHolder != null) {
            voiceIatHolder.release();
        }
        NewToast.release();
        mapHolder.releaseMap();
        unregisteReceiver();
        mapHolder.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mapHolder.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mapHolder.onPause();
    }
    
    //注册广播接收器，接收来自定位的通知
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            String action = intent.getAction();
            if (LocationService.BroadcastAction.ADDRESS.equals(action)) {
                GaoDeAddress gdAddress = intent.getParcelableExtra(LocationService.Config.GAODE_ADDRESS);
                if (gdAddress != null) {
                    cityName = gdAddress.getCityname();
                    mapHolder.setCityName(cityName);
                }
            } else if (LocationService.BroadcastAction.COORDINATE.equals(action)) {
                try {
                    double iLatitu = intent.getDoubleExtra(LocationService.Config.LATITUDE, 0);
                    double iLongti = intent.getDoubleExtra(LocationService.Config.LONGTITUDE, 0);
                    LatLng pointCurr = new LatLng(iLatitu, iLongti);
                    mapHolder.setCurrPoint(pointCurr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    
    public void registeReceiver() {
        IntentFilter filter = new IntentFilter(LocationService.BroadcastAction.ADDRESS);
        this.registerReceiver(mReceiver, filter);
        
        filter = new IntentFilter(LocationService.BroadcastAction.COORDINATE);
        this.registerReceiver(mReceiver, filter);
        
        LocationService.actionStart(this);
    }
    
    public void unregisteReceiver() {
        this.unregisterReceiver(mReceiver);
    }
    
    /**
     * 
     * 地图主页的，poi搜索相关布局内容。
     */
    static class PoiSearchViewHolder {
        View laySelInMap;//地图选点
        
        View layHistory;//历史搜索布局
        
        ScrollView svDaoHang;//历史点击容器
        
        TextView tvClearHis;//清空按钮
        
        View layInputMask;//背景遮罩
        
        View laySearchResult;//查询poi列表父布局
        
        ListView lvSearchResult;//查询列表
        
        RouteSearchAdapter routeSearchAdapter;
        
        List<PoiInfo> poiItems = new ArrayList<PoiInfo>();
    }
}
