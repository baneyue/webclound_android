package com.webcloud.func.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.PoiItem;
import com.webcloud.R;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.UserManager;
import com.webcloud.map.BaseMapActivity;
import com.webcloud.model.PoiInfo;

/**
 * 设置家和公司页面。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-29]
 * @version  [版本号, 2013-12-5]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VehicleSetHomeCompanyActivity extends BaseMapActivity implements OnClickListener {
    public static final String TAG = "VehicleSetHomeCompanyActivity";
    
    /**设置家或公司的类型：3家/4公司*/
    int searchType = 3;
    
    private Button btnBack;
    
    private TextView tvHome, tvCompany, tvHomeTitle, tvCompanyTitle;
    
    private View laySetCompany, laySetHome;
    
    private UserManager userMgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_home_company_set);
        userMgr = SystemManager.getInstance(this).userMgr;
        btnBack = (Button)findViewById(R.id.btnBack);
        
        btnBack.setOnClickListener(this);
        tvHome = (TextView)findViewById(R.id.tvHome);
        tvCompany = (TextView)findViewById(R.id.tvCompany);
        tvHomeTitle = (TextView)findViewById(R.id.tvHomeTitle);
        tvCompanyTitle = (TextView)findViewById(R.id.tvCompanyTitle);
        laySetCompany = findViewById(R.id.laySetCompany);
        laySetHome = findViewById(R.id.laySetHome);
        
        laySetHome.setOnClickListener(this);
        laySetCompany.setOnClickListener(this);
        Intent data = getIntent();
        parseIntentData(data);
    }
    
    private void parseIntentData(Intent intent) {
        searchType = intent.getIntExtra("searchType", 3);
        if (searchType == 3) {
            setHome();
        } else {
            setCompany();
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            parseIntentData(intent);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            //设置家的地址
            case R.id.laySetHome: {
                Intent intent = new Intent(this, VehiclePoiSearchActivity.class);
                searchType = 3;
                intent.putExtra("searchType", 3);
                this.startActivityForResult(intent, 10);
            }
                break;
            //设置公司地址
            case R.id.laySetCompany: {
                Intent intent = new Intent(this, VehiclePoiSearchActivity.class);
                searchType = 4;
                intent.putExtra("searchType", 4);
                this.startActivityForResult(intent, 10);
            }
                break;
            
            default:
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == 10) {
            PoiItem PoiItem = (PoiItem)data.getParcelableExtra("poiItem");
            PoiInfo poi = new PoiInfo(PoiItem.getLatLonPoint(), PoiItem.getTitle(), PoiItem.getTitle(), null);
            if (searchType == 3) {
                userMgr.saveCache(UserManager.CACHE_HOME, JSON.toJSONString(poi));
                userMgr.home = poi;
                setHome();
            } else if (searchType == 4) {
                userMgr.saveCache(UserManager.CACHE_COMPANY, JSON.toJSONString(poi));
                userMgr.company = poi;
                setCompany();
            }
        }
    }
    
    /** 
     * 设置家位置
     *
     * @see [类、类#方法、类#成员]
     */
    public void setHome() {
        if (userMgr.home != null) {
            tvHomeTitle.setText("修改家地址");
            tvHome.setText(userMgr.home.getAddress());
        } else {
            tvHomeTitle.setText("家地址");
            tvHome.setText("点击设置");
        }
    }
    
    /** 
     * 设置公司位置
     *
     * @see [类、类#方法、类#成员]
     */
    public void setCompany() {
        if (userMgr.company != null) {
            tvCompanyTitle.setText("修改公司地址");
            tvCompany.setText(userMgr.company.getAddress());
        } else {
            tvCompanyTitle.setText("公司地址");
            tvCompany.setText("点击设置");
        }
    }
}
