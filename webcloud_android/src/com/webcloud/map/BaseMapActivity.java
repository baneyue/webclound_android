package com.webcloud.map;

import android.os.Bundle;

import com.webcloud.BaseActivity;

/**
 * 基础活动，定义活动的基本行为。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-4-27]
 */
public abstract class BaseMapActivity extends BaseActivity {
    private final String TAG = "BaseMapActivity";
    
    private MapViewHolderImpl mapHolder;
    
    /** 
     *  设置mapHolder.
     *  必须把对应活动中的地图给设置给此处，进行相关生命周期控制。
     *  
     * @param mapHolder
     * @see [类、类#方法、类#成员]
     */
    public void setMapHolder(MapViewHolderImpl mapHolder) {
        if(mapHolder == null) throw new RuntimeException("该方法必须重新，把活动中创建的MapHolder传给此处！！！");
        this.mapHolder = mapHolder;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapHolder.onSaveInstanceState(outState);
    }
}
