package com.webcloud.func.map.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.model.PoiInfo;

/**
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-4-8]
 */
public class VehiclePoiListAdapter extends BaseAdapter{
    static final String TAG = "VehiclePoiListAdapter";
    
    List<PoiInfo> poiInfos;
    
    Activity context;
    
    LayoutInflater mInflater;
    
    public VehiclePoiListAdapter(Activity context, List<PoiInfo> poiInfos) {
        this.context = context;
        this.poiInfos = poiInfos;
        this.mInflater = context.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        if (poiInfos != null)
            return poiInfos.size();
        return 0;
    }
    
    @Override
    public PoiInfo getItem(int position) {
        if (poiInfos != null) {
            return poiInfos.get(position);
        }
        return null;
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
        PoiInfo poi = poiInfos.get(position);
        PoiName.setText(poi.getName());
        poiAddress.setText(poi.getAddress());
        return convertView;
    }
}
