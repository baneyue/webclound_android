package com.webcloud.func.map.component;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;
import com.webcloud.R;
import com.webcloud.func.map.adapter.RouteSearchAdapter;
import com.webcloud.func.voice.VoiceIatHolder;
import com.webcloud.func.voice.VoiceTextListener;

public class VehicleRouteSearchPoiDialog extends Dialog implements OnItemClickListener, OnItemSelectedListener,
    OnClickListener, VoiceTextListener {
    
    public int getSearchType() {
        return searchType;
    }
    
    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
    
    private List<PoiItem> poiItems = new ArrayList<PoiItem>();
    
    private Context context;
    
    private RouteSearchAdapter adapter;
    private ListView listView;
    private int searchType = 0;//1查询起点,2查询终点,3查询家，4查询公司,0无效
    
    protected OnListItemClick mOnClickListener;
    
    private EditText etSearchInput;
    
    private ImageView btnClear;
    
    private ImageView btnVoice;
    
    
    private VoiceIatHolder vcIatHolder;
    
    
    //private VehicleLocationMapViewHolder vh;
    
    public VehicleRouteSearchPoiDialog(Context context) {
        this(context, android.R.style.Theme_Dialog);
    }
    
    public VehicleRouteSearchPoiDialog(Context context, int theme) {
        super(context, theme);
    }
    
    /*public VehicleRouteSearchPoiDialog(Context context, VehicleLocationMapViewHolder vh, int searchType) {
        this(context, R.style.dialog_router);
        this.context = context;
        this.searchType = searchType;
        this.vh = vh;
        //adapter = new RouteSearchAdapter(context, poiItems);
    }*/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routesearch_list_poi);
        listView = (ListView)findViewById(R.id.ListView_nav_search_list_poi);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                mOnClickListener.onListItemClick(VehicleRouteSearchPoiDialog.this, poiItems.get(position));
            }
        });
        etSearchInput = (EditText)findViewById(R.id.etSearchInput);
        btnClear = (ImageView)findViewById(R.id.btnClear);
        btnVoice = (ImageView)findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        etSearchInput.addTextChangedListener(new TextWatcher() {
            
            String text = "";
            
            @Override
            public void afterTextChanged(Editable s) {
                /*if (TextUtils.isEmpty(s.toString())) {
                    btnClear.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.VISIBLE);
                } else {
                    btnVoice.setVisibility(View.GONE);
                    btnClear.setVisibility(View.VISIBLE);
                }
                String input = s.toString();
                if (!text.equals(input)) {
                    //输入长度大于2才执行查询
                    if (!TextUtils.isEmpty(input) && input.length() > 2) {
                        if (searchType == 1) {
                            vh.startSearchResult(input);
                        } else if (searchType == 2) {
                            vh.endSearchResult(input);
                        } else if (searchType == 3) {
                            vh.homeSearchResult(input);
                        } else if (searchType == 4) {
                            vh.companySearchResult(input);
                        }
                    }
                    text = input;
                }*/
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }
        });
        
        vcIatHolder = new VoiceIatHolder(this.getContext(), this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> view, View view1, int arg2, long arg3) {
    }
    
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        
    }
    
    public interface OnListItemClick {
        public void onListItemClick(VehicleRouteSearchPoiDialog dialog, PoiItem item);
    }
    
    public void setOnListClickListener(OnListItemClick l) {
        mOnClickListener = l;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                etSearchInput.setText("");
                break;
            case R.id.btnVoice:
                vcIatHolder.showIat();
                break;
            
            default:
                break;
        }
    };
    
    @Override
    public String getText() {
        String tts = "";
        /*if (hotList != null && hotList.size() > 0) {
            for (String str : hotList) {
                tts += str + "。";
            }
        }*/
        /*if (TextUtils.isEmpty(tts)) {
            tts = "抱歉！您输入的" + poiName + "没有查询到路况信息，请确认您输入的地点是否正确。";
        }*/
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
    protected void onStop() {
        super.onStop();
    }
    
    public void setData(List<PoiItem> items) {
        if (items == null || items.size() == 0) {
            poiItems.clear();
        } else {
            poiItems.clear();
            poiItems.addAll(items);
        }
        adapter.notifyDataSetChanged();
    }
}
