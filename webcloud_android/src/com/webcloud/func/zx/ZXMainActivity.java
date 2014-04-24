package com.webcloud.func.zx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.webcloud.R;
import com.webcloud.ThemeStyleActivity;
import com.webcloud.define.ResContants;
import com.webcloud.func.zx.adapter.GridViewMainAdapter;

/**
 * 装修首页。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-4-24]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ZXMainActivity extends ThemeStyleActivity {
    private GridView mGVmain;
    
    private Button mBtnSetting;
    
    private GridViewMainAdapter mGVAdapter;
    
    private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zx_main_activity);
        initView();
    }
    
    private void initView() {
        mBtnSetting = (Button)findViewById(R.id.btnSetting);
        mGVmain = (GridView)findViewById(R.id.gvMain);
        datas.clear();
        for (int i = 0; i < ResContants.ZX_ITEM_TITLE.length; i++) {
            Map<String, Object> element = new HashMap<String, Object>();
            element.put("img", ResContants.ZX_ITEM_IMG[i]);
            element.put("title", ResContants.ZX_ITEM_TITLE[i]);
            element.put("index", i);
            datas.add(element);
        }
        mGVAdapter = new GridViewMainAdapter(this, datas);
        mGVmain.setAdapter(mGVAdapter);
        //点击菜单
        mGVmain.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                Map<String, Object> element = datas.get(arg2);
                int index = (Integer)element.get("index");
                switch (index) {
                    case 0:
                        
                        break;
                    case 1:
                        
                        break;
                    case 2:
                        
                        break;
                    case 3:
                        
                        break;
                    case 4:
                        
                        break;
                    case 5:
                        
                        break;
                    
                    default:
                        break;
                }
                Toast.makeText(getBaseContext(), "click gridview item", 1).show();
            }
        });
        //点击首页设置
        mBtnSetting.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "click setting", 1).show();
            }
        });
    }
}
