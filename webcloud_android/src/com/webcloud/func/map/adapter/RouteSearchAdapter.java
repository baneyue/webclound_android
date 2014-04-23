package com.webcloud.func.map.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.model.PoiInfo;

public class RouteSearchAdapter extends BaseAdapter {
    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    boolean isHistory = false;
    
    private Context context;
    
    private List<PoiInfo> poiItems = null;
    
    private LayoutInflater mInflater;
    
    public RouteSearchAdapter(Context context, List<PoiInfo> poiItems) {
        this.context = context;
        this.poiItems = poiItems;
        mInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return poiItems.size();
    }
    
    @Override
    public Object getItem(int position) {
        return position;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vehicle_poi_search_item, null);
        }
        
        TextView PoiName = ((TextView)convertView.findViewById(R.id.poiName));
        TextView poiAddress = (TextView)convertView.findViewById(R.id.poiAddress);
        PoiInfo poi = poiItems.get(position);
        PoiName.setText(poi.getName());
        String address = poi.getAddress();
        poiAddress.setText("地址:" + address);
        return convertView;
    }
}
