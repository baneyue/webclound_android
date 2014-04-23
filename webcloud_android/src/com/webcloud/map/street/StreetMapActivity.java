package com.webcloud.map.street;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.model.PoiInfo;

/**
 * 1.店铺实景
 * 2.车库实景，支持定位到店铺，给个大致的方向和距离
 * 3.详细地图，上面支持定位，显示路径，显示标注图层，显示地址名，显示业务相关控制
 * a.初始店铺坐标维护，当修改店铺时坐标相应改变
 * b.从当前位置导航到目标店铺坐标，显示路径图层
 * c.打开实时路况，要求导航到目标路径的路况，并有文字提示
 * d.手动在地图上标注一个点，然后导航当前位置到目标点的路径
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StreetMapActivity extends BaseActivity {
    public static final String TAG = "StreetMapActivity";
    private Button btnBack;
    private StreetMapHolder streetHolder;
    
    private List<PoiInfo> otherPoiList = new ArrayList<PoiInfo>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_street_view);
        //initView();
        streetHolder = new StreetMapHolder(this);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        parseIntentData(getIntent());
    }
    
    
    public void parseIntentData(Intent it) {
        List<PoiInfo> pois = it.getParcelableArrayListExtra("otherPoiList");
        if (pois != null && pois.size() > 0) {
            otherPoiList.clear();
            otherPoiList.addAll(pois);
            streetHolder.setPoiList(otherPoiList);
            PoiInfo poi = otherPoiList.get(0);
            streetHolder.showStreetMap(poi);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
        parseIntentData(intent);
    }
    
    /*private void initView() {
        btnShare = (Button)findViewById(R.id.btnShare);
        btnPhone = (Button)findViewById(R.id.btnPhone);
        
        btnShare.setOnClickListener(clicker);
        btnPhone.setOnClickListener(clicker);
    }*/
    
    OnClickListener clicker = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                /*case R.id.btnPhone:
                    NewToast.show(StreetMapActivity.this,"打饭店电话");
                    break;
                case R.id.btnShare:
                    //跳转到分享界面
                    NewToast.show(StreetMapActivity.this,"分享地图静态图和地址信息或链接");
                    break;*/
                default:
                    break;
            }
        }
    };
    
    @Override
    protected void onDestroy() {
        streetHolder.release();
        super.onDestroy();
    }
    
    public static void launch(Context context,ArrayList<PoiInfo> poiList){
        Intent it = new Intent(context,StreetMapActivity.class);
        it.putParcelableArrayListExtra("otherPoiList", poiList);
        context.startActivity(it);
    }
}
